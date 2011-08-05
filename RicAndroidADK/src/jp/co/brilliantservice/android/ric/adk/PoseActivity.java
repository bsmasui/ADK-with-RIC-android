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

import jp.co.brilliantservice.android.ric.project.MotionModel;
import jp.co.brilliantservice.android.ric.project.PoseModel;
import jp.co.brilliantservice.android.ric.project.ProjectFile;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class PoseActivity extends ADKActivity implements OnItemClickListener {

	private final class PoseEntryAdapter extends ArrayAdapter<PoseModel.Entry> {

		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private int mTextViewResourceId;

		public PoseEntryAdapter(Context context, int textViewResourceId) {
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
			PoseModel.Entry en = getItem(position);
			StringBuilder b = new StringBuilder();
			b.append("id=");
			b.append(en.servo.id);
			b.append(", name=");
			b.append(en.servo.name);
			b.append(", angle=");
			b.append(en.angle);
			b.append(", trim=");
			b.append(en.servo.trim);
			((TextView) resultView.findViewById(R.id.name)).setText(b
					.toString());
			return resultView;
		}
	}

	private ArrayAdapter<PoseModel.Entry> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		adapter = new PoseEntryAdapter(this, R.layout.list_item);
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		ProjectFile project = RICConfig.sProject;

		String name = getIntent().getStringExtra("name");
		int index = getIntent().getIntExtra("index", 0);
		MotionModel motion = project.motionModels.get(name);
		MotionModel.Entry entry = motion.entries.get(index);
		for (PoseModel.Entry e : entry.pose.entries) {
			// System.out.println(e.getKey());
			adapter.add(e);
		}
	}

	public void onItemClick(AdapterView<?> view, View arg1, int arg2, long arg3) {
		PoseModel.Entry en = adapter.getItem(arg2);
		Toast toast = Toast.makeText(this, en.toString(), Toast.LENGTH_SHORT);
		toast.show();
	}
}