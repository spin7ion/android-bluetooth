package it.gerdavax.easybluetooth;

import it.gerdavax.android.bluetooth.BluetoothException;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDeviceListener;

import java.util.UUID;

class RemoteDevice1Impl extends RemoteDevice {
	
	private RemoteBluetoothDevice rbd = null;

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

	@Override
	public void ensurePaired() {
		if (!rbd.isPaired()) {
			Thread t = new Thread() {
				@Override
				public void run() {
					Logger.e("openedClose");
					try {
						rbd.openSocket(1).closeSocket();
					} catch (BluetoothException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Logger.e("openClosed");
				}
			};
			t.start();
			rbd.setListener(new RemoteBluetoothDeviceListener() {
				
				@Override
				public void serviceChannelNotAvailable(int serviceID) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void pinRequested() {
					LocalBluetoothDevice.getLocalDevice().showDefaultPinInputActivity(getAddress(), true);
				}
				
				@Override
				public void paired() {
					Logger.e("paired()");
				}
				
				@Override
				public void gotServiceChannel(int serviceID, int channel) {
					// TODO Auto-generated method stub
					
				}
			});
			rbd.pair();
			
		}
	}
}
