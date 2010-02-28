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

	/**
	 * @return Bluetooth Friendly name of the remote device. Could be updated in a deferred way
	 */
	public abstract String getFriendlyName();

	/**
	 * @return the BDADDR of the remote device
	 */
	public abstract String getAddress();

	/**
	 * Opens a socket towards this remote device, given an UUID
	 * WARNING call this only from a separate thread, since it is blocking.
	 * Moreover, the UUID could be truncated to most significand 16 bit on 1.x platforms
	 * @param serviceId UUID of the remote device
	 * @return the socket just opened
	 * @throws Exception if something goes wrong in the connection
	 * 
	 */
	public abstract BtSocket openSocket(UUID serviceId ) throws Exception;
	
	/**
	 * Opens a socket towards this remote device, given a network port
	 * WARNING call this only from a separate thread, since it is blocking.
	 * @param port the port to connect to
	 * @return the socket just opened
	 * @throws Exception if something goes wrong in the connection
	 */
	public abstract BtSocket openSocket(int port) throws Exception;
	
	/**
	 * @return the RSSI level of the remote device signal. BEWARE it expressed in decibels, so logarithmic scale from -128 to 0!
	 */
	public abstract int getRSSI();
	
	public void ensurePaired() {
		ensurePaired(null);
	}
	/**
	 * ensures that this device is paired with the local device with the given pin.
	 * If called with null and is not paired, attempts the pairing and show the PIN input dialog screen
	 */
	public abstract void ensurePaired(String pin);
	/**
	 * base implementation: name@bdaddr of the device
	 */
	@Override
	public String toString() {
		return getFriendlyName()+"@"+getAddress();
	}
}