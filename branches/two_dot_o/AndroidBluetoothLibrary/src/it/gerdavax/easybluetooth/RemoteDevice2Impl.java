package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class RemoteDevice2Impl extends it.gerdavax.easybluetooth.RemoteDevice {
	private BluetoothDevice bd = null;
	private int rssi = -1;
	private static final int BONDED = 10;

	RemoteDevice2Impl(BluetoothDevice _rbd) {
		this(_rbd, Integer.MIN_VALUE);
	}

	RemoteDevice2Impl(BluetoothDevice _rbd, int _rssi) {
		super();
		this.bd = _rbd;
		rssi = _rssi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.gerdavax.bluetooth.RemoteInterface#getFriendlyName()
	 */
	public String getFriendlyName() {
		return bd.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.gerdavax.bluetooth.RemoteInterface#getAddress()
	 */
	public String getAddress() {
		return bd.getAddress();
	}

	@Override
	public BtSocket openSocket(UUID serviceId) throws IOException {
		BtSocket socket = new BtSocket2Impl(bd.createRfcommSocketToServiceRecord(serviceId));
		return socket;
	}

	public int getRSSI() {
		return rssi;
	}

	@Override
	public BtSocket openSocket(int port) {
		try {
			// connection = delegate.createRfcommSocketToServiceRecord(defaultProfile);
			Method m = bd.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			BluetoothSocket connection = (BluetoothSocket) m.invoke(bd, port);
			connection.connect();
			return new BtSocket2Impl(connection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isBonded() {
		try {
			Method createBondMethod = bd.getClass().getMethod("getBondState", new Class[] {});
			Integer result = (Integer) createBondMethod.invoke(bd, new Object[] {});
			return result != BONDED;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void ensurePaired() {
		try {
			if (!isBonded()) {
				Method createBondMethod = bd.getClass().getMethod("createBond", new Class[] {});
				createBondMethod.invoke(bd, new Object[] {});
				((LocalDevice2Impl)LocalDevice.getInstance()).showDefaultPinInputActivity(bd, true);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
