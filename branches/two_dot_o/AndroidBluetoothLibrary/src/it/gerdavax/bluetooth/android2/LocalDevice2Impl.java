package it.gerdavax.bluetooth.android2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import it.gerdavax.bluetooth.ScanListener;

public class LocalDevice2Impl extends it.gerdavax.bluetooth.LocalDevice {
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice rbd = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				RemoteDevice2Impl tobounce = new RemoteDevice2Impl(rbd);
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

	@Override
	public void init(Context _ctx) throws Exception {
		super.init(_ctx);
	}

	@Override
	public void scan(ScanListener listener) throws Exception {
		super.scan(listener);
		ctx.registerReceiver(receiver, null);
		BluetoothAdapter.getDefaultAdapter().startDiscovery();
	}
}
