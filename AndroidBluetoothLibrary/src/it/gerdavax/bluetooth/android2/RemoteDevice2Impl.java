package it.gerdavax.bluetooth.android2;

import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

class RemoteDevice2Impl implements it.gerdavax.bluetooth.RemoteDevice {
	private RemoteBluetoothDevice rbd = null;
	
	public RemoteDevice2Impl(RemoteBluetoothDevice _rbd) {
		super();
		this.rbd = _rbd;
	}
	
	/* (non-Javadoc)
	 * @see it.gerdavax.bluetooth.RemoteInterface#getFriendlyName()
	 */
	public String getFriendlyName() {
		return rbd.getName();
	}
	
	/* (non-Javadoc)
	 * @see it.gerdavax.bluetooth.RemoteInterface#getAddress()
	 */
	public String getAddress() {
		return rbd.getAddress();
	}
}
