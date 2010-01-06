package it.gerdavax.easybluetooth;

import android.os.Handler;
import android.os.Message;

public abstract class ConnectionListener extends Handler {
	private static final int CONNECTION_WAITING = 1;
	private static final int CONNECTION_ERROR = 2;
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch(msg.what) {
		case CONNECTION_WAITING:
			this.connectionWaiting((BtSocket) msg.obj);
			break;
		case CONNECTION_ERROR:
			this.connectionError();
			break;
		}
	}
	
	public final void notifyConnectionError() {
		this.sendMessage(this.obtainMessage(CONNECTION_ERROR));
	}
	
	public final void notifyConnectionWaiting(BtSocket tobounce) {
		this.sendMessage(this.obtainMessage(CONNECTION_WAITING, tobounce));
	}
	public abstract void connectionWaiting(BtSocket socket);
	public abstract void connectionError();
}
