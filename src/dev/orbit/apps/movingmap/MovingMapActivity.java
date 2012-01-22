package dev.orbit.apps.movingmap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import dev.orbit.apps.movingmap.R;
import dev.orbit.apps.movingmap.net.*;

public class MovingMapActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String portString = getResources().getString(R.string.ip_socket_port);
		mHandler = new Handler();
		new Thread(new SocketServerHandler(Integer.parseInt(portString)))
				.start();
	}

	// private void append(String message) {
	// EditText socketServerLog = (EditText) findViewById(R.id.socketServerLog);
	// socketServerLog.append(message + "\r\n");
	// }

	class SocketServerHandler implements Runnable {
		public SocketServerHandler(int port) {
			mPort = port;
			mActive = true;
		}

		public void run() {
			MulticastSocket serverSocket = null;
			try {
				serverSocket = new MulticastSocket(mPort);
				serverSocket.joinGroup(InetAddress.getByName("224.0.0.0"));
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(),
						"IO exception creating server socket:", e);
				return;
			}
			while (mActive) {

				try {
					byte[] buffer = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buffer,
							buffer.length);
					while (true) {
						serverSocket.receive(packet);
						Map<Integer, float[]> sentenceMap = DatagramDecoder.DecodePacket(packet);
					}
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(),
							"IO exception processing socket communication:", e);
					mActive = false;
				}
			}
		}

		private boolean mActive;
		private int mPort;
	}

	private Handler mHandler;

	// @Override
	// protected boolean isRouteDisplayed() {
	// // TODO Auto-generated method stub
	// return false;
	// }
}