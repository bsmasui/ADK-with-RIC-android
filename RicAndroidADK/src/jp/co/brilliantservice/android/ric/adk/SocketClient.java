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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author masui@brilliantservice.co.jp
 * 
 */
public class SocketClient {
	private Socket mSocket;

	SocketClient(String host, int port) {
        try {
	    //アドレス情報を保持するsocketAddressを作成。
	    //ポート番号は30000
	        InetSocketAddress socketAddress =
	            new InetSocketAddress(host, port);

	    //socketAddressの値に基づいて通信に使用するソケットを作成する。

	    //
	        mSocket = new Socket();
	    //タイムアウトは10秒(10000msec)
	        mSocket.connect(socketAddress, 10000);

	    //接続先の情報を入れるInetAddress型のinadrを用意する。
	        InetAddress inadr;

	    //inadrにソケットの接続先アドレスを入れ、nullである場合には
	    //接続失敗と判断する。
	    //nullでなければ、接続確立している。
	        if ((inadr = mSocket.getInetAddress()) != null) {
	            System.out.println("Connect to " + inadr);
	        } else {
	            System.out.println("Connection failed.");
	            return;
	        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void write(String command) {
	    //メッセージの送信処理
	    //コマンドライン引数の2番目をメッセージとする。
	        String message = command;

	    //PrintWriter型のwriterに、ソケットの出力ストリームを渡す。(Auto Flush)
	        PrintWriter writer;
			try {
				writer = new PrintWriter(mSocket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
	    //ソケットの入力ストリームをBufferedReaderに渡す。
	        BufferedReader rd;
			try {
				rd = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

	        System.out.println("Send message: " + message);

	    //ソケットから出力する。
	        writer.println(message);
	    //もしPrintWriterがAutoFlushでない場合は，以下が必要。
	        writer.flush();

	    //サーバーからのメッセージ読み取り
	        String getline;
			try {
				getline = rd.readLine();
		        System.out.println("Message from Server:" + getline);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

	        //終了処理
            writer.close();
//            mSocket.close();
	}

	public void close() {
		if (mSocket!=null) {
	        try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
