package it.gerdavax.easybluetooth;

public interface ConnectionListener {
	public void connectionWaiting(BtSocket socket);
	public void connectionError();
}
