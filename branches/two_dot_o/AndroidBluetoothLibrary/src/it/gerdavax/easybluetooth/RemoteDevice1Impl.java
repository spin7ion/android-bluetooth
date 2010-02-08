package it.gerdavax.easybluetooth;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDeviceListener;
import it.gerdavax.util.Logger;

import java.util.UUID;

class RemoteDevice1Impl extends RemoteDevice {
	
	private RemoteBluetoothDevice rbd = null;
	private final Logger log = Logger.getLogger("EASYBT");

	RemoteDevice1Impl(RemoteBluetoothDevice _rbd) {
		super();
		this.rbd = _rbd;
	}

	public String getFriendlyName() {
		try {
			return rbd.getName();
		} catch (Exception e) {
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
		}
		PortDiscoverer discoverer = new PortDiscoverer();
		rbd.setListener(discoverer);
		rbd.getRemoteServiceChannel(uuid16);
		try {
			lock.wait();
		} catch (InterruptedException ie) {
		}
		return openSocket(discoverer.port);
	}

	public int getRSSI() {
		return (int) rbd.getRSSI();
	}

	@Override
	public BtSocket openSocket(int port) throws BluetoothException {
		return new BtSocket1Impl(rbd.openSocket(port));
	}

	/*
	 On a certain release of a 1.6 firmware for G1, the device needed a workaround
	 just after pairing to open-then-close connection immediately - very strange 
	  private class G1Workaround implements Runnable {
		public void run() {
			try {
				log.v("G1 workaround opening socket...");
				BluetoothSocket bts = rbd.openSocket(1);
				log.v("G1 workaround closing socket...");
				bts.closeSocket();
			} catch (BluetoothException e) {
				log.w("G1 workaround error "+e);
			}
		}
	}*/
	
	@Override
	public void ensurePaired() {
		if (!rbd.isPaired()) {
			//HACK TODO solves problem on HTC G1 fomr T-mobile, 1.6 firmware
			//new Thread(new G1Workaround()).start();
			rbd.setListener(new RemoteBluetoothDeviceListener() {
				
				
				@Override
				public void pinRequested() {
					LocalBluetoothDevice.getLocalDevice().showDefaultPinInputActivity(getAddress(), true);
				}
				
				@Override
				public void paired() {
					log.i("paired()");
				}
				
				@Override
				public void serviceChannelNotAvailable(int serviceID) {}
				
				
				@Override
				public void gotServiceChannel(int serviceID, int channel) {}
			});
			rbd.pair();
		}
	}
}
