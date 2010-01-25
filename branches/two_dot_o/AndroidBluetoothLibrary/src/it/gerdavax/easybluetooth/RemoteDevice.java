package it.gerdavax.easybluetooth;

import java.util.UUID;

/**
 * Gives access to methods for accessing data about remote devices.
 * Provides means for opening a connection to a specific port or service 
 * (through UUID), and for pairing with the device.
 * 
 * @author Emanuele Di Saverio (emanuele.disaverio at gmail.com)
 *
 */
public abstract class RemoteDevice {

	public abstract String getFriendlyName();

	public abstract String getAddress();

	public abstract BtSocket openSocket(UUID serviceId ) throws Exception;
	
	public abstract BtSocket openSocket(int port) throws Exception;
	
	public abstract int getRSSI();
	
	public abstract void ensurePaired();
	
	@Override
	public String toString() {
		return getFriendlyName()+"@"+getAddress();
	}
}