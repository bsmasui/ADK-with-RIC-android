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
public class MotionModel {

  public static final class Entry {
    public int keyFrame;
    public PoseModel pose;
  }

  public int keyFrames;
  public String name;
  public List<Entry> entries = new ArrayList<MotionModel.Entry>();

  public void parse(Configurations config, String fileName,
      Map<String, PoseModel> poseModel) throws IOException {
    File f = new File(config.projectDir + "/Motion/" + fileName);
    String cs = Charset.forName("Shift_JIS").name();
    BufferedReader reader = null;
    name = fileName;

    try {
      FileInputStream fis = new FileInputStream(f);
      InputStreamReader isr = new InputStreamReader(fis, cs);
      reader = new BufferedReader(isr);
      int i = 0;
      String keyframe = null;

      for (String line; (line = reader.readLine()) != null; ++i) {
        if (isHeader(i)) {
          continue;
        }
        if (isKeyframes(i)) {
          keyFrames = Integer.parseInt(line);
          continue;
        }
        if (isUnknown(i)) {
          continue;
        }
        if (line.startsWith("KFRM")) {
          keyframe = line;
          continue;
        }
        if (line.startsWith("PNME")) {
          parseEntry(keyframe, line, poseModel);
          continue;
        }
      }
    } finally {
      if (reader != null)
        reader.close();
    }
  }

  private void parseEntry(String keyframe, String pose,
      Map<String, PoseModel> poseModel) {
    Entry e = new Entry();
    e.keyFrame = Integer.parseInt(keyframe.substring(4));
    String poseFile = pose.substring(4);
    e.pose = poseModel.get(poseFile);
    entries.add(e);
  }

  private boolean isHeader(int i) {
    return i == 0;
  }

  private boolean isKeyframes(int i) {
    return i == 1;
  }

  private boolean isUnknown(int i) {
    return i == 2;
  }
}
