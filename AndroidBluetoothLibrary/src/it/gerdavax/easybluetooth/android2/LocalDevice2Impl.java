package it.gerdavax.easybluetooth.android2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import it.gerdavax.easybluetooth.RemoteDevice;
import it.gerdavax.easybluetooth.ScanListener;

public class LocalDevice2Impl extends it.gerdavax.easybluetooth.LocalDevice {
	
	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice rbd = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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

	@Override
	public void init(Context _ctx) throws Exception {
		super.init(_ctx);
	}

	@Override
	public void scan(ScanListener listener) throws Exception {
		super.scan(listener);
		ctx.registerReceiver(receiver, null);
		if ( adapter.isEnabled() ) {
			adapter.startDiscovery();
		} else {
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
		}
	}

	
	@Override
	public RemoteDevice getRemoteForAddr(String addr) {
		return new RemoteDevice2Impl(adapter.getRemoteDevice(addr));
	}
}
