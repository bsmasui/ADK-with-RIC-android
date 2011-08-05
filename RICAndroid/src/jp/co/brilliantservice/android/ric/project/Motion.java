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
public class Motion {

  Integer index;
  String fileName;

  public Motion() {
  }

  public Motion(Integer index, String fileName) {
    this.index = index;
    this.fileName = fileName;
  }

  public void parse(String entry) {
    String[] tuple = entry.split(",");
    index = Integer.parseInt(tuple[0]);
    fileName = tuple[1];
  }
}
