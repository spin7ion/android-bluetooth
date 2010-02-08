package it.gerdavax.easybluetooth;

import it.gerdavax.util.Logger;
import android.content.Context;
import android.os.Build;

/**
 * Root class, represents the local device. Has methods for Scanning remote devices, and get remote handles on them.
 * 
 * @author Emanuele Di Saverio (emanuele.disaverio at gmail.com)
 * 
 */
public abstract class LocalDevice {
	protected Context ctx;
	protected ScanListener scanListener = null;
	private static final int SDK_NUM_2_0 = 5;
	private static LocalDevice instance = null;
	protected final static Logger log = Logger.getLogger("EASYBT");

	/**
	 * Version-wise singleton methods, provised right instantiation of implementing class related to the platform that
	 * is detected
	 * 
	 * @return the right LocalDevice implementation
	 */
	public static synchronized LocalDevice getInstance() {
		if (instance == null) {
			int vInt = LocalDevice.getVersionNumber();
			log.i(LocalDevice.class, "Parsed version number is " + vInt);
			if (vInt < SDK_NUM_2_0) {
				instance = new it.gerdavax.easybluetooth.LocalDevice1Impl();
			} else {
				instance = new it.gerdavax.easybluetooth.LocalDevice2Impl();
			}
		}
		log.i(LocalDevice.class, "Returning: " + instance);
		return instance;
	}

	private static int getVersionNumber() {
		return Integer.parseInt(Build.VERSION.SDK.trim());
	}

	/**
	 * This init implementation stores just the creating context into a local variable, and that's all
	 * 
	 * @param _ctx
	 *            the context to which we have to hook onto
	 * @param ready
	 *            the ready listener, can be null
	 */
	public void init(final Context _ctx, ReadyListener ready) {
		ctx = _ctx;
	}

	/**
	 * Cleans up the library initialization; must ALWAYS be called before the relate context is destroyed
	 */
	public void destroy() {
		ctx = null;
	}

	/**
	 * Invokes a scan task on the local adapter 
	 * @param listener the listener to which all detected devices should be notified
	 */
	public void scan(final ScanListener listener) {
		scanListener = listener;
	}

	/**
	 * aborts the scan currently in progress (if there is one)
	 */
	public abstract void stopScan();

	/**
	 * returns the Remote Device implementation for a remote device
	 * @param addr the BDADDR of the device to instantiate
	 */
	public abstract RemoteDevice getRemoteForAddr(String addr);

	/**
	 * Opens a server connection listening for incoming connections.
	 * @param listener the listener to which new connections are notified to
	 * @param port the port that will host the connection
	 * @return an object to control the server service
	 */
	public abstract ServerControl listenForConnection(ConnectionListener listener, int port);

	/**
	 * Ensures that, for a certain amount of time, the current adatper will be discoverable
	 * by remote Bluetooth scans
	 */
	public abstract void ensureDiscoverable();
}
