package it.gerdavax.easybluetooth;

public interface ScanListener {
	
	public void deviceFound(RemoteDevice tobounce);
	
	public void scanCompleted();
}
