package it.gerdavax.easybluetooth;

import android.os.Handler;
import android.os.Message;

/**
 * Listener for acceptance of incoming connection.
 * All calls are synchronized with the main thread handler (no need to use an <pre>android.os.Handler</pre>)
 * 
 * @author Emanuele Di Saverio (emanuele.disaverio at gmail.com)
 *
 */
public abstract class ConnectionListener {
	private static final int CONNECTION_WAITING = 1;
	private static final int CONNECTION_ERROR = 2;
	
	private Handler delegate = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case CONNECTION_WAITING:
				ConnectionListener.this.connectionWaiting((BtSocket) msg.obj);
				break;
			case CONNECTION_ERROR:
				ConnectionListener.this.connectionError();
				break;
			}
		}
	};
	
	final void notifyConnectionError() {
		delegate.sendMessage(delegate.obtainMessage(CONNECTION_ERROR));
	}
	
	final void notifyConnectionWaiting(BtSocket tobounce) {
		delegate.sendMessage(delegate.obtainMessage(CONNECTION_WAITING, tobounce));
	}
	
	/**
	 * There is a new connection waiting on the server
	 * @param socket the socket to the connecting client
	 */
	public abstract void connectionWaiting(BtSocket socket);
	
	/**
	 * called when an error from server side connection is raised
	 */
	public abstract void connectionError();
}
