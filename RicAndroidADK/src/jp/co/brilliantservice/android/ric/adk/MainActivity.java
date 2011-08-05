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
package jp.co.brilliantservice.android.ric.adk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.co.brilliantservice.android.ric.project.Configurations;
import jp.co.brilliantservice.android.ric.project.ProjectFile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class MainActivity extends ADKActivity {

	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		text = (TextView) findViewById(R.id.server);
	}

	private void initProject() {
		Configurations config = new Configurations();
		String dir = Environment.getExternalStorageDirectory() + "/"
				+ Constants.PROJECT_DIR;

		if (!new File(dir).exists()) {
			throw new IllegalStateException(new FileNotFoundException(dir));
		}

		config.projectDir = dir;
		ProjectFile project = new ProjectFile(this, config);
		RICConfig.sProject = project;
		config.trimFile = Constants.TRIM_FILE;

		File f = new File(dir, Constants.PROJECT_FILE);
		try {
			project.parse(f);
		} catch (IOException e) {
			Log.e("RIC", e.getMessage(), e);
		}
	}

	public void onStartButtonClick(View view) {

		initProject();

		Intent intent = new Intent(this, ProjectActivity.class);
		startActivity(intent);
	}

	public void onScenarioButtonClick(View view) {

		initProject();

		Intent intent = new Intent(this, ScenarioActivity.class);
		startActivity(intent);
	}

	public void onDemoKitButtonClick(View view) {

		Intent intent = new Intent(this, DemoKitActivity.class);
		startActivity(intent);
	}
}
