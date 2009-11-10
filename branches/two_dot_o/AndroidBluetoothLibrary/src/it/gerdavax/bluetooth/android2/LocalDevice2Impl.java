package it.gerdavax.bluetooth.android2;

import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocalDevice2Impl extends it.gerdavax.bluetooth.LocalDevice {
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				RemoteBluetoothDevice rbd = (RemoteBluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				RemoteDevice2Impl tobounce = new RemoteDevice2Impl(rbd);
				scanListener.deviceFound(tobounce);
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				scanListener.scanCompleted();
			}
		}
	};
	
	@Override
	public void doDestroy() {
		ctx = null;
	}

	@Override
	public void doInit() {
		// nop
	}

	@Override
	public void doScan() {
		ctx.registerReceiver(receiver, null);
		BluetoothAdapter.getDefaultAdapter().startDiscovery();
	}
}
