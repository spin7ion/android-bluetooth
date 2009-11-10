package it.gerdavax.bluetooth.android1;

import java.util.ArrayList;

import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

public class LocalDevice1Impl extends it.gerdavax.bluetooth.LocalDevice {
	private LocalBluetoothDevice local = null;
	private LocalBluetoothDeviceListener localListener = new LocalBluetoothDeviceListener() {
		
		@Override
		public void scanStarted() {}
		
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
		public void bluetoothEnabled() {}
		
		@Override
		public void bluetoothDisabled() {}
	};
	@Override
	public void doDestroy() {
		local.close();
	}

	@Override
	public void doInit() throws Exception {
		local = LocalBluetoothDevice.initLocalDevice(ctx);
	}

	@Override
	public void doScan() throws Exception {
		local.setListener(localListener);
		local.scan();
	}
}
