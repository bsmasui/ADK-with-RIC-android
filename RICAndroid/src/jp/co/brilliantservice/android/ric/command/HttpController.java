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
package jp.co.brilliantservice.android.ric.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.co.brilliantservice.android.ric.project.ProjectFile;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class HttpController extends Controller {

  private String server;

  private Activity context;

  private ExecutorService exec;

  private static final class Buffer {
    Buffer(byte[] buffer, int offset, int count) {
      this.buffer = buffer;
      this.offset = offset;
      this.count = count;
    }

    byte[] buffer;
    int offset;
    int count;
  }

  private OutputStream adapter = new OutputStream() {

    private List<Buffer> buffers = new ArrayList<Buffer>();

    @Override
    public void write(int oneByte) throws IOException {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void write(byte[] buffer) throws IOException {
      write(buffer, 0, buffer.length);
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
      buffers.add(new Buffer(buffer, offset, count));
    }

    public void flush() throws IOException {
      doPost(buffers);
      buffers.clear();
    };
  };

  public HttpController(ProjectFile project, Activity context, String server) {
    super(project, null);
    this.context = context;
    this.exec = Executors.newSingleThreadExecutor();
    this.out = adapter;
    this.server = server;
  }

  private void doPost(List<Buffer> buffers) {

    StringBuilder b = new StringBuilder();

    for (int i = 0; i < buffers.size(); ++i) {
      Buffer e = buffers.get(i);
      if (i > 0)
        b.append('Z');
      for (int j = e.offset; j < e.count; ++j) {
        b.append(String.format("%02X", e.buffer[j]));
      }
    }

    final String command = b.toString();

    context.runOnUiThread(new Runnable() {

      public void run() {
        Toast toast = Toast.makeText(context, command.toString(),
            Toast.LENGTH_SHORT);
        toast.show();
      }
    });

    Runnable task = new Runnable() {

      public void run() {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost req = new HttpPost(server);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Log.i("RIC", command);
        nvps.add(new BasicNameValuePair("c", command));
        try {
          req.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
        }

        try {
          final HttpResponse res = client.execute(req);
          context.runOnUiThread(new Runnable() {
            public void run() {
              Toast toast = Toast.makeText(context, res.getStatusLine()
                  .toString(), Toast.LENGTH_SHORT);
              toast.show();
            }
          });
        } catch (final Exception e) {
          context.runOnUiThread(new Runnable() {
            public void run() {
              Toast toast = Toast.makeText(context, e.getLocalizedMessage(),
                  Toast.LENGTH_SHORT);
              toast.show();
            }
          });
          return;
        }
      }
    };

    exec.execute(task);
  }
}
