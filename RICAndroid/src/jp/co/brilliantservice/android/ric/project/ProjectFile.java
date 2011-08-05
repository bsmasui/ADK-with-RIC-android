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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.brilliantservice.android.ric.converter.Converter;
import android.content.Context;
import android.util.Log;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class ProjectFile {

  private static final boolean SCAN_DIRECTORY = false;

  public class InitSection extends AbstractSection {
    Entry<Integer> comSpeed = newIntEntry("COMSPEED");
    Entry<String> comPort = newStringEntry("COMPORT");
    Entry<String> path = newStringEntry("PATH");
    Entry<Integer> svSpeed = newIntEntry("SVSPEED");
    Entry<Integer> prjId = newIntEntry("PRJID");
    Entry<Integer> cap = newIntEntry("CAP");
    Entry<Integer> servoTime = newIntEntry("SERVOTIME");
    Entry<Integer> ctrlType = newIntEntry("CTRLTYPE");
  }

  public class ControlSection extends AbstractSection {
    Entry<Integer> count = newIntEntry("COUNT");
    Entry<Integer> value = newIntEntry("VALUE");
    Entry<Integer> expo = newIntEntry("EXPO");
    Entry<Integer> revision = newIntEntry("REVISION");
  }

  public class EventSection extends AbstractSection {
  }

  public class AutoPlaySection extends AbstractSection {
  }

  public class TrimingSection extends AbstractSection {
  }

  public class ParamSection extends AbstractSection {
  }

  public class RevisionSection extends AbstractSection {
  }

  public class PoseSection extends AbstractSection {
    Entry<List<Pose>> name = newMultipleEntry("NAME", new Converter<Pose>() {
      public Pose convert(String v) {
        Pose e = new Pose();
        e.parse(v);
        return e;
      }
    });

    public void onSectionEnd() {
      if (!SCAN_DIRECTORY)
        return;
      // 実際のファイルには中身があったようだ
      List<Pose> list = name.val();
      String[] files = listFiles(config, "Pose");
      for (int i = 0, num = files.length; i < num; ++i)
        list.add(new Pose(i, files[i]));
    };
  }

  public class MotionSection extends AbstractSection {
    Entry<List<Motion>> name = newMultipleEntry("NAME",
        new Converter<Motion>() {
          public Motion convert(String v) {
            Motion e = new Motion();
            e.parse(v);
            return e;
          }
        });

    public void onSectionEnd() {
      if (!SCAN_DIRECTORY)
        return;
      // 実際のファイルには中身があったようだ。
      List<Motion> list = name.val();
      String[] files = listFiles(config, "Motion");
      for (int i = 0, num = files.length; i < num; ++i)
        list.add(new Motion(i, files[i]));
    };
  }

  public class ScenarioSection extends AbstractSection {
    Entry<List<Scenario>> name = newMultipleEntry("NAME",
        new Converter<Scenario>() {
          public Scenario convert(String v) {
            Scenario e = new Scenario();
            e.parse(v);
            return e;
          }
        });

    public void onSectionEnd() {
      if (!SCAN_DIRECTORY)
        return;
      // 実際のファイルには中身があったようだ。
      List<Scenario> list = name.val();
      String[] files = listFiles(config, "Scenario");
      for (int i = 0, num = files.length; i < num; ++i)
        list.add(new Scenario(i, files[i]));
    };
  }

  public class MotionTransSection extends AbstractSection {
    // Entry<List<MotionTrans>> name = newMultipleEntry("NAME");
  }

  public class ScenarioTransSection extends AbstractSection {
    // Entry<List<ScenarioTrans>> name = newMultipleEntry("NAME");
  }

  public class ParamDataSection extends AbstractSection {
    Entry<Integer> revision = newIntEntry("REVISION");
    Entry<Integer> count = newIntEntry("COUNT");
    Entry<List<ParamData>> para = newMultipleEntry("PARA",
        new Converter<ParamData>() {
          public ParamData convert(String v) {
            ParamData e = new ParamData();
            e.parse(v);
            return e;
          }
        });

    public ParamData fromAlias(String alias) {
      for (ParamData pd : para.val()) {
        if (pd.alias.equals(alias))
          return pd;
      }
      return null;
    }
  }

  public class GroupDataSection extends AbstractSection {
    Entry<Integer> revision = newIntEntry("REVISION");
    Entry<Integer> count = newIntEntry("COUNT");
    Entry<List<GroupData>> group = newMultipleEntry("GROUP",
        new Converter<GroupData>() {
          public GroupData convert(String v) {
            GroupData e = new GroupData();
            e.parse(v);
            return e;
          }
        });
  }

  public class AnalogDataSection extends AbstractSection {
    Entry<Integer> revision = newIntEntry("REVISION");
    Entry<Integer> count = newIntEntry("COUNT");
    Entry<List<AnalogData>> group = newMultipleEntry("ANALOG",
        new Converter<AnalogData>() {
          public AnalogData convert(String v) {
            AnalogData e = new AnalogData();
            e.parse(v);
            return e;
          }
        });
  }

  public class JyroDataSection extends AbstractSection {
    Entry<Integer> revision = newIntEntry("REVISION");
    Entry<Integer> count = newIntEntry("COUNT");
  }

  private final Context context;
  private InitSection init;
  private ControlSection control;
  private EventSection event;
  private AutoPlaySection autoPlay;
  private TrimingSection triming;
  private ParamSection param;
  private RevisionSection revision;
  private PoseSection pose;
  private MotionSection motion;
  private ScenarioSection scenario;
  private MotionTransSection motionTrans;
  private ScenarioTransSection scenarioTrans;
  private ParamDataSection paramData;
  private GroupDataSection groupData;
  private AnalogDataSection analogData;
  private JyroDataSection jyroData;

  private Map<String, Section> sections;

  public Map<String, PoseModel> poseModels = new LinkedHashMap<String, PoseModel>();
  public Map<String, MotionModel> motionModels = new LinkedHashMap<String, MotionModel>();
  public Map<String, ScenarioModel> scenarioModels = new LinkedHashMap<String, ScenarioModel>();
  private final Configurations config;

  public ProjectFile(Context context, Configurations config) {
    this.context = context;
    this.config = config;

    sections = new LinkedHashMap<String, Section>();
    init = buildSection("INIT", "INIT", new InitSection());
    control = buildSection("CTRL", "CONTROL", new ControlSection());
    event = buildSection("EVNT", "EVENT", new EventSection());
    autoPlay = buildSection("AUTO", "AUTOPLAY", new AutoPlaySection());
    triming = buildSection("TRIM", "TRIMING", new TrimingSection());
    param = buildSection("PARM", "PARAM", new ParamSection());
    revision = buildSection("REVI", "REVISION", new RevisionSection());
    pose = buildSection("POSE", "POSE", new PoseSection());
    motion = buildSection("MOTN", "MOTION", new MotionSection());
    scenario = buildSection("SCEN", "SCENARIO", new ScenarioSection());
    motionTrans = buildSection("MTNT", "MOTIONTRANS", new MotionTransSection());
    scenarioTrans = buildSection("SCNT", "SCENARIOTRANS",
        new ScenarioTransSection());
    paramData = buildSection("PARA", "PARAMDATA", new ParamDataSection());
    groupData = buildSection("GROP", "GROUPDATA", new GroupDataSection());
    analogData = buildSection("ANAL", "ANALOGDATA", new AnalogDataSection());
    jyroData = buildSection("JYRO", "JYRODATA", new JyroDataSection());
  }

  private <T extends Section> T buildSection(String name, String comment,
      T section) {
    sections.put(name, section);
    return section;
  }

  public void parse(File f) throws IOException {
    String cs = Charset.forName("Shift_JIS").name();
    BufferedReader reader = null;

    try {
      FileInputStream fis = new FileInputStream(f);
      InputStreamReader isr = new InputStreamReader(fis, cs);
      reader = new BufferedReader(isr);
      int i = 0;
      Section section = null;
      for (String line; (line = reader.readLine()) != null; ++i) {
        if (isHeader(i)) {
          continue;
        }
        if (isEmpty(line)) {
          if (section != null)
            section.onSectionEnd();
          continue;
        }
        char c = line.charAt(0);
        switch (c) {
        case '/':
          break;
        case '[':
          String name = obtainSectionName(line);
          section = startSection(name);
          section.onSectionStart();
          break;
        default:
          String[] tuple = splitEntry(line);
          section.parseLine(tuple[0], tuple[1]);
          break;
        }
      }
      TrimFile trimFile = new TrimFile();
      try {
        trimFile.parse(config, config.trimFile);
      } catch (IOException error) {
        Log.e("RIC", error.toString(), error);
      }
      resolvePose(trimFile);
      resolveMotion();
      resolveScenario();
    } finally {
      if (reader != null)
        reader.close();
    }
  }

  private void resolvePose(TrimFile trimFile) {
    for (Pose p : pose.name.val()) {
      PoseModel pf = new PoseModel();
      String fileName = p.fileName;
      try {
        pf.parse(config, fileName, paramData, trimFile);
      } catch (IOException e) {
      }
      poseModels.put(p.fileName, pf);
    }
  }

  private void resolveMotion() {
    for (Motion m : motion.name.val()) {
      MotionModel mf = new MotionModel();
      String fileName = m.fileName;
      try {
        mf.parse(config, fileName, poseModels);
      } catch (IOException e) {
      }
      motionModels.put(m.fileName, mf);
    }
  }

  private void resolveScenario() {
    for (Scenario m : scenario.name.val()) {
      ScenarioModel mf = new ScenarioModel();
      String fileName = m.fileName;
      try {
        mf.parse(config, fileName, motionModels);
      } catch (IOException e) {
      }
      scenarioModels.put(m.fileName, mf);
    }
  }

  private String[] listFiles(Configurations config, String dirName) {
    File d = new File(config.projectDir + "/" + dirName);
    return d.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return !name.startsWith(".");
      }
    });
  }

  private Section startSection(String name) {
    Section section = sections.get(name);
    return section;
  }

  private String[] splitEntry(String s) {
    int i = s.indexOf('=');
    String name = s.substring(0, i);
    String value = s.substring(i + 1);
    return new String[] { name, value };
  }

  private String obtainSectionName(String s) {
    return s.substring(1, s.length() - 1);
  }

  private boolean isHeader(int i) {
    return i == 0;
  }

  private boolean isEmpty(String s) {
    if (s == null)
      return true;
    Pattern ptn = Pattern.compile("^[ \\s]*$");
    Matcher mt = ptn.matcher(s);
    return mt.matches();
  }
}
