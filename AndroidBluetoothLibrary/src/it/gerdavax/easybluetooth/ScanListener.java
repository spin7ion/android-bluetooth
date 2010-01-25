package it.gerdavax.easybluetooth;

import android.os.Handler;
import android.os.Message;

/**
 * Listener for receiving the results of a Bluetooth scan activity.
 * 
 * @author Emanuele Di Saverio (emanuele.disaverio at gmail.com)
 *
 */
public abstract class ScanListener {
	private static final int DEVICE_FOUND = 1;
	private static final int SCAN_COMPLETED = 2;
	private Handler delegate = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DEVICE_FOUND:
				ScanListener.this.deviceFound((RemoteDevice) msg.obj);
				break;
			case SCAN_COMPLETED:
				ScanListener.this.scanCompleted();
				break;
			}
		}
	};

	final void notifyScanCompleted() {
		delegate.sendMessage(delegate.obtainMessage(SCAN_COMPLETED));
	}

	final void notifyDeviceFound(RemoteDevice tobounce) {
		delegate.sendMessage(delegate.obtainMessage(DEVICE_FOUND, tobounce));
	}

	public abstract void deviceFound(RemoteDevice tobounce);

	public abstract void scanCompleted();
}
