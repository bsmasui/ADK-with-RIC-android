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

import java.io.IOException;

import jp.co.brilliantservice.android.ric.command.Controller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class PrefsActivity extends ADKActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);
    }

    public void onTorqueOnButtonClick(View view) {
        Controller controller = ControllerLocator.getController(RICConfig.sProject, this);

        try {
            controller.switchTorque(true);
        } catch (IOException e) {
            Log.e("RIC", e.getMessage(), e);
        }
    }

    public void onInitDirectionButtonClick(View view) {
        Controller controller = ControllerLocator.getController(RICConfig.sProject, this);

        try {
            controller.initDirection();
        } catch (IOException e) {
            Log.e("RIC", e.getMessage(), e);
        }
    }

    public void onTorqueOffButtonClick(View view) {
        Controller controller = ControllerLocator.getController(RICConfig.sProject, this);

        try {
            controller.switchTorque(false);
        } catch (IOException e) {
            Log.e("RIC", e.getMessage(), e);
        }
    }

    public void onStartButtonClick(View view) {
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivity(intent);
    }
    
}
