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
public enum ServoType {

  //
  RS301(1),
  //
  RS302(2),
  //
  RS303(3),
  //
  RS304(4),
  //
  RS401(5),
  //
  RS402(6),
  //
  RS405(7),
  //
  RS501(8);

  private ServoType(int value) {
    this.value = value;
  }

  private int value;

  public int getValue() {
    return value;
  }

  public static ServoType valueOf(int value) {
    for (ServoType e : values())
      if (e.value == value)
        return e;
    return null;
  }

}
