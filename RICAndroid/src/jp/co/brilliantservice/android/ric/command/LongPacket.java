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
import static jp.co.brilliantservice.android.ric.command.CommandUtils.updateChecksum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class LongPacket implements Command {

  private static final String TAG = "RIC";

  /** RS485でRPU10に送信可能な最大サイズ */
  private static final int LEN_BUFFER = 127;

  private byte[] header = { (byte) 0xFA, (byte) 0xAF };
  private byte id = 0x00;
  private byte flags = 0x00;
  private byte address;
  private byte[] data;
  private List<ShortPacket> packets = new ArrayList<ShortPacket>();

  private byte[] buffer;

  public LongPacket() {
    this(LEN_BUFFER);
  }

  public LongPacket(int bufferSize) {
    buffer = new byte[bufferSize];
  }

  protected LongPacket add(ShortPacket packet) {
    address = packet.address;
    packets.add(packet);
    return this;
  }

  private int length() {
    int len = packets.size();
    for (ShortPacket p : packets)
      len += p.data.length;
    return len;
  }

  public void send(OutputStream out) throws IOException {
    byte[] b = buffer;
    a(b, 0, header);
    a(b, 2, id);
    a(b, 3, flags);
    a(b, 4, address);
    int len = length();
    a(b, 5, len / packets.size());
    a(b, 6, packets.size());
    data = new byte[len];
    for (int i = 0, num = packets.size(), offset = 7; i < num; ++i) {
      ShortPacket p = packets.get(i);
      a(b, offset, p.id);
      a(b, offset + 1, p.data);
      offset += (1 + p.data.length);
      Log.d(
          TAG,
          "#" + p.id + "[" + String.format("%02x:", p.id)
              + Utils.formatData(p.data, 0, p.data.length) + "]");
    }
    int end = 7 + data.length + 1;
    updateChecksum(b, 0, end);
    Log.d(TAG, String.format("LongPacket[" + Utils.formatData(b, 0, end)
        + "] %d", end));
    out.write(b, 0, end);
  }
}
