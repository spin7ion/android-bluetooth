package it.gerdavax.easybluetooth.android1;

import java.util.ArrayList;

import android.content.Context;

import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.easybluetooth.RemoteDevice;
import it.gerdavax.easybluetooth.ScanListener;

public class LocalDevice1Impl extends it.gerdavax.easybluetooth.LocalDevice {
	private LocalBluetoothDevice local = null;
	private LocalBluetoothDeviceListener localListener = new LocalBluetoothDeviceListener() {

		@Override
		public void scanStarted() {}

		@Override
		public void scanCompleted(ArrayList<String> devices) {
			scanListener.scanCompleted();
		}

		public void deviceFound(String deviceAddress) {
			RemoteBluetoothDevice rbd = local.getRemoteBluetoothDevice(deviceAddress);
			scanListener.deviceFound(new RemoteDevice1Impl(rbd));
		}

		public void bluetoothEnabled() {
			try {
				local.scan();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void enabled() {
			try {
				local.scan();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void bluetoothDisabled() {
		}

		@Override
		public void disabled() {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public void destroy() {
		local.close(ctx);
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
	
	@Override
	public RemoteDevice getRemoteForAddr(String addr) {
		return new RemoteDevice1Impl(local.getRemoteBluetoothDevice(addr));
	}
}
