package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.lang.reflect.Method;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class LocalDevice2Impl extends it.gerdavax.easybluetooth.LocalDevice {

	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			final String action = intent.getAction();
			Logger.v("received "+action);
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice rbd = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				RemoteDevice2Impl tobounce = new RemoteDevice2Impl(rbd, rssi);
				scanListener.notifyDeviceFound(tobounce);
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				scanListener.notifyScanCompleted();
				ctx.unregisterReceiver(receiver);
			}
		}
	};

	@Override
	public void destroy() {
		try {
			ctx.unregisterReceiver(receiver);
		} catch (IllegalArgumentException iae) {
			//receiver was not really registered
		}
		ctx = null;
		super.destroy();
	}

	public void init(Context _ctx, final ReadyListener ready) {
		super.init(_ctx, ready);
		if (!adapter.isEnabled()) {
			ctx.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context ctx, Intent i) {
					// filter with just one
					int newState = i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
					if (newState == BluetoothAdapter.STATE_ON) {
						ctx.unregisterReceiver(this);
						if (ready != null) {
							ready.notifyReady();
						}
					}
				}
			}, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
			adapter.enable();
		} else {
			ready.ready();
		}
	}

	@Override
	public void scan(ScanListener listener) {
		super.scan(listener);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		ctx.registerReceiver(receiver, filter);
		adapter.startDiscovery();
	}

	public void stopScan() {
		adapter.cancelDiscovery();
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
				// lock until socket arrives
				BluetoothSocket bts = connection.accept(Integer.MAX_VALUE);
				Logger.d(this, "connection unlocked");
				listener.notifyConnectionWaiting(new BtSocket2Impl(bts));
			} catch (IOException e) {
				e.printStackTrace();
				listener.notifyConnectionError();
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
	/**
	 * Shows system activity to input PIN for assigned device
	 * 
	 * Thanks to Emanuele Di Saverio
	 * 
	 * @since 0.3
	 */
	public void showDefaultPinInputActivity(BluetoothDevice address, boolean clearSystemNotification) {
		if (clearSystemNotification) {
			clearSystemNotification();
		}
		String ACTION_PAIRING_REQUEST =
            "android.bluetooth.device.action.PAIRING_REQUEST";
		Intent intent = new Intent(ACTION_PAIRING_REQUEST);
		String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
		intent.putExtra(EXTRA_DEVICE, address);
		String EXTRA_PAIRING_VARIANT =
	            "android.bluetooth.device.extra.PAIRING_VARIANT";
		int PAIRING_VARIANT_PIN = 0;
		intent.putExtra(EXTRA_PAIRING_VARIANT,PAIRING_VARIANT_PIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	private NotificationManager notificationManager = null;
	public void clearSystemNotification() {
		try {
			if (notificationManager == null) {
				notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
			}
			notificationManager.cancel(android.R.drawable.stat_sys_data_bluetooth);
		} catch (Exception e) {
		}
	}
}
