package it.gerdavax.easybluetooth.android1;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.easybluetooth.ConnectionListener;
import it.gerdavax.easybluetooth.Logger;
import it.gerdavax.easybluetooth.RemoteDevice;
import it.gerdavax.easybluetooth.ScanListener;
import it.gerdavax.easybluetooth.ServerControl;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

public class LocalDevice1Impl extends it.gerdavax.easybluetooth.LocalDevice {
	private LocalBluetoothDevice local = null;
	private LocalBluetoothDeviceListener localListener = new LocalBluetoothDeviceListener() {

		@Override
		public void scanStarted() {
		}

		@Override
		public void scanCompleted(ArrayList<String> devices) {
			scanListener.scanCompleted();
		}

		@Override
		public void deviceFound(String deviceAddress) {
			RemoteBluetoothDevice rbd = local.getRemoteBluetoothDevice(deviceAddress);
			scanListener.deviceFound(new RemoteDevice1Impl(rbd));
		}

		@Override
		public void bluetoothEnabled() {
			/*try {
				local.scan();
				local.setListener(null);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
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
	public void init(Context _ctx) throws Exception {
		super.init(_ctx);
		local = LocalBluetoothDevice.initLocalDevice(ctx);
		if (!local.isEnabled()) {
			// bt is not enabled, enable then scan
			local.setEnabled(true);
		}
	}

	@Override
	public void scan(final ScanListener listener) throws Exception {
		super.scan(listener);
		local.setListener(localListener);
		local.scan();
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
				BluetoothSocket newSock = bs.accept(60 * 1000);
				Logger.d(this, "connection unlocked");
				listener.connectionWaiting(new BtSocket1Impl(newSock));
			} catch (Exception e) {
				e.printStackTrace();
				listener.connectionError();
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
