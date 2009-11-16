package it.gerdavax.bluetooth.android1;

import java.util.ArrayList;

import android.content.Context;

import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.bluetooth.ScanListener;

public class LocalDevice1Impl extends it.gerdavax.bluetooth.LocalDevice {
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
			try {
				local.scan();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	}

	@Override
	public void scan(final ScanListener listener) throws Exception {
		super.scan(listener);
		local.setListener(localListener);
		if (local.isEnabled()) {
			local.scan();
		} else {
			//bt is not enabled, enable then scan
			local.setEnabled(true);
		}
	}
}
