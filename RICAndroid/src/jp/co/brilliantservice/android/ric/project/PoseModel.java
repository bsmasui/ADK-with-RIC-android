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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class PoseModel {

  public static final class Entry {
    public ServoModel servo;
    public float angle;
    public int unknown;
    public int torque;
    public int complianceNumerator;
    public int complianceDenominator;
  }

  public int servoCount;
  public String name;
  public List<Entry> entries = new ArrayList<PoseModel.Entry>();

  public void parse(Configurations config, String fileName,
      ProjectFile.ParamDataSection paramData, TrimFile trimFile)
      throws IOException {
    File f = new File(config.projectDir + "/Pose/" + fileName);
    String cs = Charset.forName("Shift_JIS").name();
    BufferedReader reader = null;
    name = fileName;

    try {
      FileInputStream fis = new FileInputStream(f);
      InputStreamReader isr = new InputStreamReader(fis, cs);
      reader = new BufferedReader(isr);
      int i = 0;
      for (String line; (line = reader.readLine()) != null; ++i) {
        if (isHeader(i)) {
          continue;
        }
        if (isServoCount(i)) {
          servoCount = Integer.parseInt(line);
          continue;
        }
        parseEntry(line, paramData, trimFile);
      }
    } finally {
      if (reader != null)
        reader.close();
    }
  }

  private void parseEntry(String line, ProjectFile.ParamDataSection paramData,
      TrimFile trimFile) {
    Entry e = new Entry();
    String alias = line.substring(0, 4);
    ParamData pd = paramData.fromAlias(alias);
    e.servo = pd.toServoModel(trimFile);
    e.angle = Float.parseFloat(line.substring(4, 10));
    e.unknown = Integer.parseInt(line.substring(10, 15));
    e.torque = Integer.parseInt(line.substring(15, 18));
    e.complianceNumerator = Integer.parseInt(line.substring(18, 22));
    e.complianceDenominator = Integer.parseInt(line.substring(22, 26));
    entries.add(e);
  }

  private boolean isHeader(int i) {
    return i == 0;
  }

  private boolean isServoCount(int i) {
    return i == 1;
  }

}
