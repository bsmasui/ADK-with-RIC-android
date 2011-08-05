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
public class ParamData {

  Integer index;
  Integer id;
  String alias;
  String name;
  Direction direction;
  @Unknown
  Float float1;
  Integer servoSpeed;
  Float maxAngle;
  Float minAngle;
  Float initialAngle;
  Integer maxTorque;
  @Unknown
  Integer int1;
  @Unknown
  Integer int2;
  @Unknown
  Integer int3;
  Integer complianceNumerator;
  Integer complianceDenominator;
  Integer punch;
  ServoType servoType;
  Boolean available;

  public void parse(String entry) {
    String[] tuple = entry.split(",");
    index = Integer.parseInt(tuple[0]);
    id = Integer.parseInt(tuple[1]);
    alias = tuple[2];
    name = tuple[3];
    direction = Direction.valueOf(Integer.parseInt(tuple[4]));
    // @Unknown
    float1 = Float.parseFloat(tuple[5]);
    servoSpeed = Integer.parseInt(tuple[6]);
    maxAngle = Float.parseFloat(tuple[7]);
    minAngle = Float.parseFloat(tuple[8]);
    initialAngle = Float.parseFloat(tuple[9]);
    maxTorque = Integer.parseInt(tuple[10]);
    // @Unknown
    int1 = Integer.parseInt(tuple[11]);
    // @Unknown
    int2 = Integer.parseInt(tuple[12]);
    // @Unknown
    int3 = Integer.parseInt(tuple[13]);
    complianceNumerator = Integer.parseInt(tuple[14]);
    complianceDenominator = Integer.parseInt(tuple[15]);
    punch = Integer.parseInt(tuple[16]);
    servoType = ServoType.valueOf(Integer.parseInt(tuple[17]));
    available = Boolean.valueOf(tuple[18].equals("1"));
  }

  public ServoModel toServoModel(TrimFile trimFile) {
    ServoModel s = new ServoModel();
    s.index = index;
    s.id = id;
    s.alias = alias;
    s.name = name;
    s.direction = direction;
    s.servoSpeed = servoSpeed;
    s.maxAngle = maxAngle;
    s.minAngle = minAngle;
    s.initialAngle = initialAngle;
    s.maxTorque = maxTorque;
    s.complianceNumerator = complianceNumerator;
    s.complianceDenominator = complianceDenominator;
    s.punch = punch;
    s.servoType = servoType;
    s.available = available;
    if (trimFile != null && trimFile.trims != null && !trimFile.trims.isEmpty())
      s.trim = trimFile.trims.get(index);
    return s;
  }
}
