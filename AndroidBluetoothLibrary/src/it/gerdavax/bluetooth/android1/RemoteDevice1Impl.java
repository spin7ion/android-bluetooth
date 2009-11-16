package it.gerdavax.bluetooth.android1;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDeviceListener;
import it.gerdavax.bluetooth.BtSocket;
import it.gerdavax.bluetooth.RemoteDevice;

import java.util.UUID;

class RemoteDevice1Impl implements RemoteDevice {
	private RemoteBluetoothDevice rbd = null;

	RemoteDevice1Impl(RemoteBluetoothDevice _rbd) {
		super();
		this.rbd = _rbd;
	}

	/*
	 * 
	 * 
	 * @see it.gerdavax.bluetooth.RemoteInterface#getFriendlyName()
	 */
	public String getFriendlyName() {
		try {
			return rbd.getName();
		} catch (BluetoothException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.gerdavax.bluetooth.RemoteInterface#getAddress()
	 */
	public String getAddress() {
		return rbd.getAddress();
	}

	@Override
	public BtSocket openSocket(UUID serviceId) throws Exception {
		int uuid16 = (int) serviceId.getLeastSignificantBits();
		final Object lock = new Object();
		class PortDiscoverer implements RemoteBluetoothDeviceListener {
			int port;

			@Override
			public void serviceChannelNotAvailable(int serviceID) {
				port = 1;
				lock.notify();
			}

			@Override
			public void pinRequested() {
				LocalBluetoothDevice.getLocalDevice().showDefaultPinInputActivity(getAddress(), true);
			}

			@Override
			public void paired() {
				// TODO Auto-generated method stub

			}

			@Override
			public void gotServiceChannel(int serviceID, int channel) {
				port = channel;
				lock.notify();
			}
		};
		PortDiscoverer discoverer = new PortDiscoverer();
		rbd.setListener(discoverer);
		rbd.getRemoteServiceChannel(uuid16);
		try {
			lock.wait();
		} catch (InterruptedException ie) {
		}
		return new BtSocket1Impl(rbd.openSocket(discoverer.port));
	}

	public int getRSSI() {
		return (int)rbd.getRSSI();
	}
}
