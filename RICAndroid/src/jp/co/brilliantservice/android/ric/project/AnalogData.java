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
package jp.co.brilliantservice.android.ric.project;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class AnalogData {

  private static final String TAG = "RIC";

  Integer index;
  String name;
  // boolean[] flags;
  Integer[] up;
  Integer[] down;
  Integer[] right;
  Integer[] left;

  public void parse(String entry) {
    String[] tuple = entry.split(",");
    index = Integer.parseInt(tuple[0]);
    name = tuple[1];
    Integer[] values = new Integer[tuple.length - 2];
    int flagIndex = 0;
    for (int i = 2, num = tuple.length; i < num; ++i)
      values[flagIndex++] = Integer.valueOf(tuple[i]);

    int nums = values.length / 4;
    up = new Integer[nums];
    down = new Integer[nums];
    right = new Integer[nums];
    left = new Integer[nums];

    // Integer[][] temp = new Integer[][] { up, down, left, right };
    Integer[][] temp = new Integer[][] { down, up, right, left };

    for (int i = 0, index = 0; i < 4; ++i) {
      Integer[] d = temp[i];
      for (int j = 0; j < nums; ++j) {
        d[j] = values[index++];
      }
    }

    // Log.d(TAG, "Analog:Name[" + name + "]");
    // Log.d(TAG, "Analog:U[" + Utils.formatData(up, 0, up.length) + "]");
    // Log.d(TAG, "Analog:D[" + Utils.formatData(down, 0, down.length) +
    // "]");
    // Log.d(TAG, "Analog:L[" + Utils.formatData(left, 0, left.length) +
    // "]");
    // Log.d(TAG, "Analog:R[" + Utils.formatData(right, 0, right.length) +
    // "]");
  }
}
