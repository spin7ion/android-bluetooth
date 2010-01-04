package it.gerdavax.easybluetooth.android2;

import java.io.IOException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import it.gerdavax.easybluetooth.ConnectionListener;
import it.gerdavax.easybluetooth.Logger;
import it.gerdavax.easybluetooth.RemoteDevice;
import it.gerdavax.easybluetooth.ScanListener;
import it.gerdavax.easybluetooth.ServerControl;

public class LocalDevice2Impl extends it.gerdavax.easybluetooth.LocalDevice {

	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice rbd = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int rssi = intent.getIntExtra(BluetoothDevice.EXTRA_RSSI, Integer.MIN_VALUE);
				RemoteDevice2Impl tobounce = new RemoteDevice2Impl(rbd, rssi);
				scanListener.deviceFound(tobounce);
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				scanListener.scanCompleted();
			}
		}
	};

	@Override
	public void destroy() {
		ctx = null;
		super.destroy();
	}
	
	public void init(Context _ctx) throws Exception {
		super.init(_ctx);
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	@Override
	public void scan(ScanListener listener) throws Exception {
		super.scan(listener);
		ctx.registerReceiver(receiver, null);
		//if (adapter.isEnabled()) {
			adapter.startDiscovery();
		/*} else {
			ctx.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context ctx, Intent i) {
					// filter with just one
					int newState = i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
					if (newState == BluetoothAdapter.STATE_ON) {
						ctx.unregisterReceiver(this);
						adapter.startDiscovery();
					}
				}
			}, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
			adapter.enable();
		}*/
	}

	@Override
	public RemoteDevice getRemoteForAddr(String addr) {
		return new RemoteDevice2Impl(adapter.getRemoteDevice(addr));
	}

	@Override
	public ServerControl listenForConnection(ConnectionListener listener, int port) {
		try {
			Method m = adapter.getClass().getMethod("listenUsingRfcommOn", new Class[] { int.class });
			BluetoothServerSocket connection = (BluetoothServerSocket) m.invoke(adapter, port);
			ConnectionAlert t = new ConnectionAlert(connection, listener);
			t.start();
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private class ConnectionAlert extends Thread implements ServerControl {
		private BluetoothServerSocket connection = null;
		private ConnectionListener listener = null;

		public ConnectionAlert(BluetoothServerSocket connection, ConnectionListener listener) {
			super();
			this.connection = connection;
			this.listener = listener;
		}

		@Override
		public void run() {
			try {
				if (!adapter.isEnabled()) {
					final Object lock = new Object();
					ctx.registerReceiver(new BroadcastReceiver() {
						@Override
						public void onReceive(Context ctx, Intent i) {
							// filter with just one
							int newState = i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
							if (newState == BluetoothAdapter.STATE_ON) {
								ctx.unregisterReceiver(this);
								lock.notify();
							}
						}
					}, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
					adapter.enable();
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// lock until socket arrives
				BluetoothSocket bts = connection.accept(60 * 1000);
				Logger.d(this, "connection unlocked");
				listener.connectionWaiting(new BtSocket2Impl(bts));
			} catch (IOException e) {
				e.printStackTrace();
				listener.connectionError();
			}
		}

		@Override
		public void halt() {
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.interrupt();
		}

	}

	@Override
	public void ensureDiscoverable() {
		// default duration for discoverable action
		Intent act = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		ctx.startActivity(act);
	}
}
