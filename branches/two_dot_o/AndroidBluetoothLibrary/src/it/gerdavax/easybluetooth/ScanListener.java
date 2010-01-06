package it.gerdavax.easybluetooth;

import android.os.Handler;
import android.os.Message;

public abstract class ScanListener extends Handler {
	private static final int DEVICE_FOUND = 1;
	private static final int SCAN_COMPLETED = 2;
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch(msg.what) {
		case DEVICE_FOUND:
			this.deviceFound((RemoteDevice) msg.obj);
			break;
		case SCAN_COMPLETED:
			this.scanCompleted();
			break;
		}
	}
	
	final void notifyScanCompleted() {
		this.sendMessage(this.obtainMessage(SCAN_COMPLETED));
	}
	
	final void notifyDeviceFound(RemoteDevice tobounce) {
		this.sendMessage(this.obtainMessage(DEVICE_FOUND, tobounce));
	}
	
	public abstract void deviceFound(RemoteDevice tobounce);
	
	public abstract void scanCompleted();
}
