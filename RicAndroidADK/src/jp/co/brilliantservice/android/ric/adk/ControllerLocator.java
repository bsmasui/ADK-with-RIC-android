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
import java.io.OutputStream;

import jp.co.brilliantservice.android.ric.command.Controller;
import jp.co.brilliantservice.android.ric.project.ProjectFile;
import android.app.Activity;
import android.widget.Toast;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class ControllerLocator {

	private static ADKController SINGLETON;

	public static final synchronized Controller getController(
			ProjectFile project, Activity context) {
		ADKActivity adk = (ADKActivity) context;

		OutputStream out = adk.getOutputStream();

		if (out == null) {
			Toast.makeText(adk, "No longer connected USB Accessory",
					Toast.LENGTH_SHORT).show();

			out = new OutputStream() {

				@Override
				public void write(int oneByte) throws IOException {
					String string = String.format("%02x", oneByte & 0xff);
					System.out.print(string + ":");
				}

				@Override
				public void flush() throws IOException {

					super.flush();

					System.out.println();
				}
			};
		}

		if (SINGLETON != null) {
			SINGLETON.setOut(out);
			return SINGLETON;
		}

		SINGLETON = new ADKController(project, out);
		return SINGLETON;
	}
}
