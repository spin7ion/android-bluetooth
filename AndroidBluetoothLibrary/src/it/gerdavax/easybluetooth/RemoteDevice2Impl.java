package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

class RemoteDevice2Impl extends it.gerdavax.easybluetooth.RemoteDevice {
	
	private BluetoothDevice bd = null;
	private int rssi = -1;

	RemoteDevice2Impl(BluetoothDevice _rbd) {
		this(_rbd, Integer.MIN_VALUE);
	}

	RemoteDevice2Impl(BluetoothDevice _rbd, int _rssi) {
		super();
		this.bd = _rbd;
		rssi = _rssi;
	}
	
	public String getFriendlyName() {
		return bd.getName();
	}
	
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
			return bd.getBondState() != BluetoothDevice.BOND_NONE;
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
