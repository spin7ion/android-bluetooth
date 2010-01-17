package it.gerdavax.easybluetooth;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

class LocalDevice1Impl extends it.gerdavax.easybluetooth.LocalDevice {
	private LocalBluetoothDevice local = null;
	private LocalBluetoothDeviceListener localListener = new LocalBluetoothDeviceListener() {

		@Override
		public void scanStarted() {
		}

		@Override
		public void scanCompleted(ArrayList<String> devices) {
			scanListener.notifyScanCompleted();
		}

		@Override
		public void deviceFound(String deviceAddress) {
			RemoteBluetoothDevice rbd = local.getRemoteBluetoothDevice(deviceAddress);
			scanListener.notifyDeviceFound(new RemoteDevice1Impl(rbd));
		}

		@Override
		public void bluetoothEnabled() {
			/*
			 * try { local.scan(); local.setListener(null); } catch (Exception e) { e.printStackTrace(); }
			 */
		}

		@Override
		public void bluetoothDisabled() {
		}
	};

	@Override
	public void destroy() {
		local.close();
		super.destroy();
	}

	@Override
	public void init(Context _ctx, final ReadyListener ready) {
		super.init(_ctx, ready);
		try {
			local = LocalBluetoothDevice.initLocalDevice(ctx);
			if (!local.isEnabled()) { // bt is not enabled, enable
				local.setListener(new LocalBluetoothDeviceListener() {

					@Override
					public void scanStarted() {
						// TODO Auto-generated method stub

					}

					@Override
					public void scanCompleted(ArrayList<String> devices) {
						// TODO Auto-generated method stub

					}

					@Override
					public void deviceFound(String deviceAddress) {
						// TODO Auto-generated method stub

					}

					@Override
					public void bluetoothEnabled() {
						if (ready != null) {
							ready.notifyReady();
						}
						local.setListener(null);
					}

					@Override
					public void bluetoothDisabled() {
						// TODO Auto-generated method stub

					}
				});
				local.setEnabled(true);
			} else {
				ready.ready();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void scan(final ScanListener listener) {
		super.scan(listener);
		local.setListener(localListener);
		try {
			local.scan();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stopScan() {
		try {
			local.stopScanning();
		} catch (Exception e) {
			Logger.e(this, "stopScan error!", e);
		}
	}

	@Override
	public RemoteDevice getRemoteForAddr(String addr) {
		return new RemoteDevice1Impl(local.getRemoteBluetoothDevice(addr));
	}

	@Override
	public ServerControl listenForConnection(ConnectionListener listener, int port) {
		try {
			it.gerdavax.android.bluetooth.BluetoothSocket bs = local.openServerSocket(port);
			ConnectionAlert t = new ConnectionAlert(bs, listener);
			t.start();
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private class ConnectionAlert extends Thread implements ServerControl {
		private BluetoothSocket bs = null;
		private ConnectionListener listener = null;

		public ConnectionAlert(BluetoothSocket bs, ConnectionListener listener) {
			super();
			this.bs = bs;
			this.listener = listener;
		}

		public void halt() {
			bs.closeSocket();
			this.interrupt();
		}

		@Override
		public void run() {
			try {
				// lock until socket arrives
				BluetoothSocket newSock = bs.accept(Integer.MAX_VALUE);
				Logger.d(this, "connection unlocked");
				listener.notifyConnectionWaiting(new BtSocket1Impl(newSock));
			} catch (Exception e) {
				e.printStackTrace();
				listener.notifyConnectionError();
			}
		}
	}

	@Override
	public void ensureDiscoverable() {
		try {
			local.setScanMode(LocalBluetoothDevice.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
			Toast toast = Toast.makeText(ctx, "discoverable mode for 120 seconds", Toast.LENGTH_SHORT);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
