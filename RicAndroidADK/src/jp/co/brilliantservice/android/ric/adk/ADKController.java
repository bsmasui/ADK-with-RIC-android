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

import java.io.OutputStream;

import jp.co.brilliantservice.android.ric.command.Controller;
import jp.co.brilliantservice.android.ric.command.LongPacket;
import jp.co.brilliantservice.android.ric.project.ProjectFile;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class ADKController extends Controller {

	//private static final int UNLIMITED_LONG_PACKET_BYTES = 1024;
	private static final int UNLIMITED_LONG_PACKET_BYTES = 127;
	
	//private static final int UNIT_POSE = 99;
	private static final int UNIT_POSE = 12;

	public ADKController(ProjectFile project, OutputStream out) {
		super(project, out, UNIT_POSE);
	}

	@Override
	protected LongPacket obtainLongPacket() {
		return new LongPacket(UNLIMITED_LONG_PACKET_BYTES);
	}
}
