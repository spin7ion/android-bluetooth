package it.gerdavax.bluetooth;

public interface ScanListener {
	public void deviceFound(RemoteDevice tobounce);
	
	public void scanCompleted();
}
