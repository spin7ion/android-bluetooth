package it.gerdavax.easybluetooth;

import android.os.Handler;
import android.os.Message;

public abstract class ReadyListener {
	private static final int READY = 1;
	
	private Handler delegate = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == READY) {
				ReadyListener.this.ready();
			}
		}
	};
	
	final void notifyReady() {
		delegate.sendMessage( delegate.obtainMessage(READY) );
	}
	
	public abstract void ready();
}
