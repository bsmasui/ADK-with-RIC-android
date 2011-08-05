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

import java.util.Map.Entry;

import jp.co.brilliantservice.android.ric.command.Controller;
import jp.co.brilliantservice.android.ric.project.MotionModel;
import jp.co.brilliantservice.android.ric.project.ProjectFile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class ProjectActivity extends ADKActivity implements
		OnItemClickListener, OnItemLongClickListener {

	private final class MotionAdapter extends ArrayAdapter<MotionModel> {

		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private int mTextViewResourceId;

		public MotionAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			mContext = context;
			mTextViewResourceId = textViewResourceId;
			mLayoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View resultView = convertView;
			if (resultView == null) {
				resultView = mLayoutInflater.inflate(mTextViewResourceId, null);
			}
			MotionModel motion = getItem(position);
			String name = omitSuffix(motion.name);

			((TextView) resultView.findViewById(R.id.name)).setText(name);
			return resultView;
		}
	}

	private ArrayAdapter<MotionModel> adapter;

	private Handler handler;

	public ProjectActivity() {
		handler = new Handler();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		adapter = new MotionAdapter(this, R.layout.list_item);
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		ProjectFile project = RICConfig.sProject;
		// System.out.println(project);
		// for (Entry<String, PoseModel> e : project.poseModels.entrySet()) {
		// PoseModel v = e.getValue();
		// System.out.println(e.getKey());
		// }
		for (Entry<String, MotionModel> e : project.motionModels.entrySet()) {
			MotionModel v = e.getValue();
			// System.out.println(e.getKey());
			adapter.add(v);
		}
	}

	public boolean onItemLongClick(AdapterView<?> view, View arg1, int arg2,
			long arg3) {
		MotionModel motion = adapter.getItem(arg2);
		Intent intent = new Intent(this, MotionActivity.class);
		intent.putExtra("name", motion.name);
		startActivity(intent);
		return true;
	}

	public void onItemClick(AdapterView<?> view, View arg1, int arg2, long arg3) {
		MotionModel motion = adapter.getItem(arg2);
		Controller controller = ControllerLocator.getController(
				RICConfig.sProject, this);
		controller.move(motion, handler);
	}

	@Override
	public void onPause() {

		Controller controller = ControllerLocator.getController(
				RICConfig.sProject, this);
		controller.stopScenario();

		super.onPause();
	}

}