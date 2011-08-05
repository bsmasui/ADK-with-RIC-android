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
import jp.co.brilliantservice.android.ric.project.MotionModel;
import jp.co.brilliantservice.android.ric.project.ProjectFile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
public class MotionActivity extends ADKActivity implements OnItemClickListener,
		OnItemLongClickListener {
	private final class MotionEntryAdapter extends
			ArrayAdapter<MotionModel.Entry> {

		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private int mTextViewResourceId;

		public MotionEntryAdapter(Context context, int textViewResourceId) {
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
			MotionModel.Entry en = getItem(position);
			StringBuilder b = new StringBuilder();
			b.append(en.keyFrame);
			b.append(':');
			b.append(en.pose.name);
			((TextView) resultView.findViewById(R.id.name)).setText(b
					.toString());
			return resultView;
		}
	}

	private ArrayAdapter<MotionModel.Entry> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		adapter = new MotionEntryAdapter(this, R.layout.list_item);
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		ProjectFile project = RICConfig.sProject;

		String name = getIntent().getStringExtra("name");
		MotionModel motion = project.motionModels.get(name);
		for (MotionModel.Entry e : motion.entries) {
			adapter.add(e);
		}
	}

	public void onItemClick(AdapterView<?> view, View arg1, int arg2, long arg3) {

		ProjectFile project = RICConfig.sProject;

		String name = getIntent().getStringExtra("name");
		int index = arg2;
		MotionModel motion = project.motionModels.get(name);
		MotionModel.Entry entry = motion.entries.get(index);
		Controller controller = ControllerLocator.getController(project, this);
		try {
			controller.pose(entry.pose);
		} catch (IOException e) {
			Log.e("RIC", e.getMessage(), e);
		}
	}

	public boolean onItemLongClick(AdapterView<?> view, View arg1, int arg2,
			long arg3) {
		ProjectFile project = RICConfig.sProject;

		String name = getIntent().getStringExtra("name");
		int index = arg2;
		MotionModel motion = project.motionModels.get(name);

		Intent intent = new Intent(this, PoseActivity.class);
		intent.putExtra("name", motion.name);
		intent.putExtra("index", index);
		startActivity(intent);

		return true;
	}

	@Override
	public void onPause() {

		Controller controller = ControllerLocator.getController(
				RICConfig.sProject, this);
		controller.stopScenario();

		super.onPause();
	}

}