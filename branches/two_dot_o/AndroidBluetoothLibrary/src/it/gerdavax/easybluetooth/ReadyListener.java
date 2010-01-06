package it.gerdavax.easybluetooth;

import android.os.Handler;
import android.os.Message;

public abstract class ReadyListener extends Handler{
	private static final int READY = 1;
	@Override
	public final void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg.what == READY) {
			this.ready();
		}
	}
	
	public final void notifyReady() {
		this.sendMessage( this.obtainMessage(READY) );
	}
	
	public abstract void ready();
}
