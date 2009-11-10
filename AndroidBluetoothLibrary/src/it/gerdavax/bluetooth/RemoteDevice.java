package it.gerdavax.bluetooth;

import java.io.IOException;
import java.util.UUID;

public interface RemoteDevice {

	public abstract String getFriendlyName();

	public abstract String getAddress();

	public abstract BtSocket openSocket(UUID serviceId ) throws Exception;
}