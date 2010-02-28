package it.gerdavax.easybluetooth;

import it.gerdavax.util.Logger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

class RemoteDevice2Impl extends it.gerdavax.easybluetooth.RemoteDevice {

	private BluetoothDevice bd = null;
	private int rssi = -1;
	private final Logger log = Logger.getLogger("EASYBT");

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
		log.i(this, "About to open socket to UUID " + serviceId + "...");
		BluetoothSocket bs = bd.createRfcommSocketToServiceRecord(serviceId);
		log.i(this, "Socket to UUID " + serviceId + " open!");
		BtSocket socket = new BtSocket2Impl(bs);
		return socket;
	}

	public int getRSSI() {
		return rssi;
	}

	@Override
	public BtSocket openSocket(int port) {
		try {
			log.i(this, "About to open socket to port " + port + "...");
			Method m = bd.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			// throws Method-not-present or something like that exception if method is missing
			BluetoothSocket connection = (BluetoothSocket) m.invoke(bd, port);
			log.i(this, "About to connect socket to port " + port + "...");
			connection.connect();
			log.i(this, "Socket to port " + port + " open!");
			return new BtSocket2Impl(connection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isBonded() {
		return bd.getBondState() != BluetoothDevice.BOND_NONE;
	}

	@Override
	public void ensurePaired(String pin) {
		try {
			if (!isBonded()) {
				log.i(this, "Asking to create bond, then PIN insert");
				if (pin != null) {
					byte[] a = new byte[1];
					Method createBondMethod = bd.getClass().getMethod("setPin", new Class[] { a.getClass()});
					createBondMethod.invoke(bd, new Object[] { pin.getBytes("UTF-8") });
				}
				// pin given try to direct connect
				Method createBondMethod = bd.getClass().getMethod("createBond", new Class[] {});
				createBondMethod.invoke(bd, new Object[] {});
				if (pin == null) {
					// ask to show the "insert pin" dialog
					((LocalDevice2Impl) LocalDevice.getInstance()).showDefaultPinInputActivity(bd, true);
				}
			} else {
				log.i(this, "Device is already bonded!");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
