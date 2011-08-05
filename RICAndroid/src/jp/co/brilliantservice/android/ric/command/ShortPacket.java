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

import android.util.Log;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class ShortPacket implements Command {

  private static final String TAG = "RIC";
  private static final int LEN_BUFFER = 127;

  private byte[] header = { (byte) 0xFA, (byte) 0xAF };
  protected byte id;
  protected byte flags;
  protected byte address;
  protected byte[] data;
  // protected byte[] data = new byte[8];//konkon
  private byte[] buffer = new byte[LEN_BUFFER];

  public void send(OutputStream out) throws IOException {
    byte[] b = buffer;
    a(b, 0, header);
    a(b, 2, id);
    a(b, 3, flags);
    a(b, 4, address);
    a(b, 5, data.length);
    a(b, 6, 0x1);
    a(b, 7, data);
    int end = 7 + data.length + 1;
    updateChecksum(b, 0, end);
    Log.d(TAG, String.format("ShortPacket[" + Utils.formatData(b, 0, end)
        + "] %d", end));
    out.write(b, 0, end);
  }
}
