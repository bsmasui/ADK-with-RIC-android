/*
 * Copyright (C) 2011 BRILLIANTSERVICE Co., Ltd. & RT Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package jp.co.brilliantservice.android.ric.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import jp.co.brilliantservice.android.ric.project.Direction;
import jp.co.brilliantservice.android.ric.project.MotionModel;
import jp.co.brilliantservice.android.ric.project.MotionModel.Entry;
import jp.co.brilliantservice.android.ric.project.PoseModel;
import jp.co.brilliantservice.android.ric.project.ProjectFile;
import jp.co.brilliantservice.android.ric.project.ScenarioModel;
import jp.co.brilliantservice.android.ric.project.ServoModel;
import android.os.Handler;
import android.util.Log;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class Controller {

  private ProjectFile project;
  protected OutputStream out;
  protected List<ServoModel> servos = new ArrayList<ServoModel>();

  // private AtomicBoolean

  private static final int UNIT_DEFAULT = 99;

  // RPU10に対してRS484パケットを送るとき、パケットの最大サイズは127Kなので分割しないといけない。
  // RICでおくっているコマンドの長さと、サーボの部位への割り当てから、サーボ12までと12から後でパケットを分割する。
  private static final int UNIT_POSE = 12;
  private static final String TAG = "RIC";

  private int unit;
  private int unitPose;

  // private static final int UNIT_POSE = 99; // no-limit

  public Controller(ProjectFile project, OutputStream out) {
    this(project, out, UNIT_POSE);
  }

  public Controller(ProjectFile project, OutputStream out, int unitPose) {
    this.project = project;
    this.out = out;
    this.unit = UNIT_DEFAULT;
    this.unitPose = unitPose;
    initServo();
  }

  public void setOut(OutputStream out) {
    this.out = out;
  }

  private void initServo() {
    if (project.poseModels.values().isEmpty())
      return;
    PoseModel pose = project.poseModels.values().iterator().next();
    List<ServoModel> servos = new ArrayList<ServoModel>();
    for (PoseModel.Entry en : pose.entries) {
      ServoModel servo = en.servo;
      servos.add(servo);
    }
    this.servos = servos;
  }

  protected LongPacket obtainLongPacket() {
    return new LongPacket();
  }

  protected void sendPackets(int unit, ShortPacket... packets)
      throws IOException {
    // 127byteを越すパケットはRS485で送れないので分割する。
    List<LongPacket> lpackets = new ArrayList<LongPacket>();

    LongPacket lpacket = null;
    for (int i = 0, num = packets.length; i < num; ++i) {
      if (i % unit == 0) {
        lpacket = obtainLongPacket();
        lpackets.add(lpacket);
      }
      ShortPacket spacket = packets[i];
      lpacket.add(spacket);
    }
    if (out != null) {
      for (int i = 0, num = lpackets.size(); i < num; ++i) {
        LongPacket p = lpackets.get(i);
        new RS485(p).send(out);
      }
      out.flush();
    } else {
      Log.e(TAG, "OutputStream is null...");
    }
  }

  private Queue<Timer> mTimers = new ConcurrentLinkedQueue<Timer>();

  public void stopScenario() {
    for (Timer t : mTimers) {
      t.cancel();
    }
    mTimers.clear();
  }

  public boolean isMoving() {
    return !mTimers.isEmpty();
  }

  public void playScenario(final ScenarioModel scenario, final Handler handler) {
    // 残ってるシナリオを止める。
    stopScenario();

    int size = scenario.entries.size();
    long endTime = 0L;
    for (int i = 0; i < size; ++i) {
      final ScenarioModel.Entry current = scenario.entries.get(i);
      // current.startTime;
      // current.playTime;
      // current.motion;
      Timer timer = new Timer("scenario" + i);
      mTimers.add(timer);
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            move(current.motion, handler);
          } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
          }
        }
      }, current.startTime * 100);

      endTime = current.startTime + current.playTime;
    }

    Timer timer = new Timer("scenario" + "loop");
    mTimers.add(timer);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        playScenario(scenario, handler);
      }
    }, endTime * 100);

  }

  public void move(MotionModel motion, Handler handler) {
    int size = motion.entries.size();
    for (int i = 0; i < size; ++i) {
      final MotionModel.Entry current = motion.entries.get(i);
      long tDuration;
      boolean last = (i >= size - 1);
      if (last) {
        tDuration = 0L;
      } else {
        Entry next = motion.entries.get(i + 1);
        tDuration = (next.keyFrame - current.keyFrame) * 100;
      }
      final long duration = tDuration;
      if (current.keyFrame == 0) {
        try {
          pose(current.pose, duration);
        } catch (IOException e) {
          Log.e(TAG, e.getMessage(), e);
        }
      } else {
        // handler.postDelayed(new Runnable() {
        //
        // public void run() {
        // try {
        // pose(current.pose, duration);
        // } catch (IOException e) {
        // Log.e("RIC", e.getMessage(), e);
        // }
        // }
        // }, current.keyFrame * 100);
        final Timer timer = new Timer("motion" + i);
        mTimers.add(timer);

        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            try {
              pose(current.pose, duration);
            } catch (IOException e) {
              Log.e(TAG, e.getMessage(), e);
            }
          }
        }, current.keyFrame * 100);

        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            mTimers.remove(timer);
          }
        }, current.keyFrame * 100 + duration);
      }
    }
  }

  public void pose(PoseModel pose) throws IOException {
    pose(pose, 0L);
  }

  // duration は (ms)
  public void pose(PoseModel pose, final long duration) throws IOException {
    int size = pose.entries.size();
    ShortPacket[] packets = new ShortPacket[size];
    for (int i = 0; i < size; ++i) {
      final PoseModel.Entry e = pose.entries.get(i);
      ShortPacket spacket = new ShortPacket() {
        {
          id = (byte) e.servo.id;
          address = 0x1E;
          // float vTrim = e.servo.direction == Direction.POSITIVE ?
          // (float) e.servo.trim : -e.servo.trim;
          float vTrim = e.servo.trim * 0.1F;
          int vAngle = (int) ((e.servo.initialAngle + e.angle + vTrim) * 10);
          byte angle1 = (byte) (vAngle & 0xff);
          byte angle2 = (byte) (vAngle >>> 8);
          int vDuration = (int) (duration / 10);
          byte time1 = (byte) (vDuration & 0xff);
          byte time2 = (byte) (vDuration >>> 8);
          byte reserved = 0x00;
          byte torque = (byte) e.torque;
          // String desc = String
          // .format("id=%02d, address=%02X, angle=%02f, angle(packet)=%02d (initAngle=%02f, trim=%02d)",
          // id, address, e.angle, vAngle,
          // e.servo.initialAngle, e.servo.trim);
          String desc = String.format(
              "id=%02d, address=%02X, angle=%d, duration=%d", id, address,
              vAngle, vDuration);
          Log.d(TAG, "Pose[" + desc + "]");
          // data[0] = angle1;
          // data[1] = angle2;
          // data[2] = time1;
          // data[3] = time2;
          // data[4] = reserved;
          // data[5] = torque;//konkon
          data = new byte[] { angle1, angle2, time1, time2, reserved, torque };
        }
      };
      packets[i] = spacket;
    }
    sendPackets(unitPose, packets);
  }

  /**
   * Analogコントロール用。Angleのみ
   * 
   * @param pose
   * @throws IOException
   */
  public void angle(PoseModel pose) throws IOException {
    int size = pose.entries.size();
    ShortPacket[] packets = new ShortPacket[size];
    for (int i = 0; i < size; ++i) {
      final PoseModel.Entry e = pose.entries.get(i);
      ShortPacket spacket = new ShortPacket() {
        {
          id = (byte) e.servo.id;
          address = 0x1E;
          // float vTrim = e.servo.direction == Direction.POSITIVE ?
          // (float) e.servo.trim : -e.servo.trim;
          float vTrim = e.servo.trim * 0.1F;
          int vAngle = (int) ((e.servo.initialAngle + e.angle + vTrim) * 10);
          byte angle1 = (byte) (vAngle & 0xff);
          byte angle2 = (byte) (vAngle >>> 8);
          // String desc = String
          // .format("id=%02d, address=%02X, angle=%02f, angle(packet)=%02d (initAngle=%02f, trim=%02d)",
          // id, address, e.angle, vAngle,
          // e.servo.initialAngle, e.servo.trim);
          String desc = String.format("id=%02d, address=%02X, angle=%d", id,
              address, vAngle);
          Log.d(TAG, "Pose[" + desc + "]");
          // data[0] = angle1;
          // data[1] = angle2;
          // data[2] = time1;
          // data[3] = time2;
          // data[4] = reserved;
          // data[5] = torque;//konkon
          data = new byte[] { angle1, angle2 };
        }
      };
      packets[i] = spacket;
    }
    sendPackets(unitPose, packets);
  }

  public void switchTorque(final boolean torque) throws IOException {
    // byte[] torqueOn = { 0x53, 0x09, (byte) 0xFA, (byte) 0xAF, 0x01, 0x00,
    // 0x24, 0x01, 0x01, 0x01, 0x24 };
    // byte[] torqueOff = { 0x53, 0x09, (byte) 0xFA, (byte) 0xAF, 0x01,
    // 0x00,
    // 0x24, 0x01, 0x01, 0x00, 0x25 };
    int size = servos.size();
    ShortPacket[] packets = new ShortPacket[size];
    for (int i = 0; i < size; ++i) {
      final ServoModel e = servos.get(i);
      ShortPacket spacket = new ShortPacket() {
        {
          id = (byte) e.id;
          address = 0x24;
          data = new byte[] { (byte) (torque ? 0x01 : 0x00) };
        }
      };
      packets[i] = spacket;
    }
    sendPackets(unit, packets);
  }

  public void initDirection() throws IOException {
    int size = servos.size();
    ShortPacket[] packets = new ShortPacket[size];
    for (int i = 0; i < size; ++i) {
      final ServoModel e = servos.get(i);
      ShortPacket spacket = new ShortPacket() {
        {
          id = (byte) e.id;
          address = 0x05;
          data = new byte[] { (byte) (e.direction == Direction.POSITIVE ? 0x00
              : 0x01) };
        }
      };
      packets[i] = spacket;
    }
    sendPackets(unit, packets);
  }

}
