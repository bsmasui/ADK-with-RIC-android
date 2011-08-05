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
import java.util.Map;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class ScenarioModel {

  public static final class Entry {
    public int startTime;
    public int playTime;
    public MotionModel motion;
  }

  public int motionNum;
  public String name;
  public List<Entry> entries = new ArrayList<ScenarioModel.Entry>();

  public void parse(Configurations config, String fileName,
      Map<String, MotionModel> motionModel) throws IOException {
    File f = new File(config.projectDir + "/Scenario/" + fileName);
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
        if (isComment(line)) {

          continue;
        }
        if (isMotionNum(i)) {
          motionNum = Integer.parseInt(line);
          continue;
        }

        parseEntry(line, motionModel);
      }
    } finally {
      if (reader != null)
        reader.close();
    }
  }

  private void parseEntry(String line, Map<String, MotionModel> motionModel) {
    Entry e = new Entry();
    String[] tuple = line.split(",");

    e.startTime = Integer.parseInt(tuple[1]);
    e.playTime = Integer.parseInt(tuple[2]);
    e.motion = motionModel.get(tuple[3]);

    entries.add(e);
  }

  private boolean isHeader(int i) {
    return i == 0;
  }

  private boolean isComment(String str) {
    return str.trim().startsWith("//");
  }

  private boolean isMotionNum(int i) {
    return i == 1;
  }
}
