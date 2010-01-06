package it.gerdavax.easybluetooth;

import android.content.Context;
import android.os.Build;

public abstract class LocalDevice {
	protected Context ctx;
	protected ScanListener scanListener = null;
	private static final int SDK_NUM_2_0 = 5;
	public static LocalDevice getInstance() {
		LocalDevice toRet = null;
		int vInt = LocalDevice.getVersionNumber();
		Logger.i(LocalDevice.class, "Parsed version number is "+vInt);
		if (vInt < SDK_NUM_2_0) {
			toRet = new it.gerdavax.easybluetooth.LocalDevice1Impl();
		} else {
			toRet = new it.gerdavax.easybluetooth.LocalDevice2Impl();
		}
		Logger.i(LocalDevice.class, "Returning: "+toRet);
		return toRet;
	}
	
	private static int getVersionNumber() {
		return Integer.parseInt(Build.VERSION.SDK.trim());
	}

	public void init(final Context _ctx, ReadyListener ready)  throws Exception {
		ctx = _ctx;
	}

	public void destroy() {
		ctx = null;
	}


	public void scan(final ScanListener listener)  throws Exception{
		scanListener = listener;
	}
	
	public abstract void stopScan();
	
	public abstract RemoteDevice getRemoteForAddr(String addr);
	
	public abstract ServerControl listenForConnection(ConnectionListener listener, int port);
	
	public abstract void ensureDiscoverable();
}
