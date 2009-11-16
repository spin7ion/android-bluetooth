package it.gerdavax.bluetooth;

import java.util.UUID;

public interface RemoteDevice {

	public abstract String getFriendlyName();

	public abstract String getAddress();

	public abstract BtSocket openSocket(UUID serviceId ) throws Exception;
	
	public abstract int getRSSI();
}