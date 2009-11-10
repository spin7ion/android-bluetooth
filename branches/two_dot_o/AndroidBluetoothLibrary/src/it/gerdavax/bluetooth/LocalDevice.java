package it.gerdavax.bluetooth;

import android.content.Context;

public abstract class LocalDevice {
	protected Context ctx;
	protected ScanListener scanListener = null;
	private static final int SDK_NUM_2_0 = 5;
	public static LocalDevice getInstance() {
		String vName = android.os.Build.VERSION.SDK;
		LocalDevice toRet = null;
		int vInt = SDK_NUM_2_0;// 2.0 by default
		try {
			vInt = Integer.parseInt(vName.trim());
		} catch (RuntimeException e) {
			//no op
		}
		if (vInt < SDK_NUM_2_0) {
			toRet = new it.gerdavax.bluetooth.android1.LocalDevice1Impl();
		} else {
			toRet = new it.gerdavax.bluetooth.android2.LocalDevice2Impl();
		}
		return toRet;
	}

	public final void init(final Context _ctx) {
		ctx = _ctx;
		doInit();
	}

	public final void destroy() {
		ctx = null;
		doDestroy();
	}


	public final void scan(final ScanListener listener) {
		scanListener = listener;
		doScan();
	}

	public abstract void doInit();

	public abstract void doScan();
	
	public abstract void doDestroy();
}
