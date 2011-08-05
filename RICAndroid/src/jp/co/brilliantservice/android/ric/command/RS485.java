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

import static jp.co.brilliantservice.android.ric.command.CommandUtils.a;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class RS485 implements Command {

  private static final String TAG = "RIC";
  private Command underlying;

  public RS485(Command underlying) {
    this.underlying = underlying;
  }

  public void send(OutputStream out) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    underlying.send(buf);
    byte[] packet = buf.toByteArray();
    byte[] rs485 = new byte[packet.length + 2];
    a(rs485, 0, 0x53, (byte) (0xFF & packet.length));
    System.arraycopy(packet, 0, rs485, 2, packet.length);
    if (out != null) {
      Log.d(
          TAG,
          String.format("RS485[" + Utils.formatData(rs485, 0, rs485.length)
              + "] %d", rs485.length));
      out.write(rs485);
    } else {
      Log.e(TAG, "OutputStream is null...");
    }
  }
}
