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

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public abstract class Utils {

  public static String formatData(byte[] data, int offset, int count) {
    StringBuilder b = new StringBuilder();
    for (int i = offset; i < count; ++i) {
      if (i > 0)
        b.append(':');
      b.append(String.format("%02x", data[i]));
    }
    return b.toString();
  }

  public static String formatData(Integer[] data, int offset, int count) {
    StringBuilder b = new StringBuilder();
    for (int i = offset; i < count; ++i) {
      if (i > 0)
        b.append(',');
      b.append(String.format("%d", data[i]));
    }
    return b.toString();
  }

  public static String omitExtention(String s) {
    int i = s.lastIndexOf('.');
    String ret = s.substring(0, i);
    return ret;
  }

}
