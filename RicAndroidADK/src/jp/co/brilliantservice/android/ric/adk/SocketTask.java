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

import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
class SocketTask extends AsyncTask<String, String, Integer> {
	private ProgressDialog mDialog;
	private onPostExecuteListener mOnPostExecuteListener;
	private String mResponse;
	private Map<String, List<String>> mResponseHeader;
	private SocketClient mClient;

	SocketTask(Context context, SocketClient client) {
		mDialog = new ProgressDialog(context);
		mClient = client;
	}

	@Override
	protected void onPreExecute() {
		mDialog.setMessage("Now in progress...");
		mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancel(true);
			}
		});
		mDialog.show();
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... arg0) {
		Integer responseCode = null;
		try {
			mClient.write(arg0[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseCode;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Integer result) {
		mDialog.dismiss();
		super.onPostExecute(result);
		Log.e("******","response=["+mResponse+"]");
		if (mOnPostExecuteListener != null) {
			mOnPostExecuteListener.onPostExecute(result, mResponseHeader, mResponse);
		}
	}

	public void setOnPostExecuteListener(onPostExecuteListener listener) {
		mOnPostExecuteListener = listener;
	}

	public interface onPostExecuteListener {
		void onPostExecute(Integer responseCode, Map<String, List<String>> responseHeader, String response);
	}
}
