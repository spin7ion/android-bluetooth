package it.gerdavax.bluetooth.android2;

import android.bluetooth.BluetoothDevice;


class RemoteDevice2Impl implements it.gerdavax.bluetooth.RemoteDevice {
	private BluetoothDevice bd = null;
	
	public RemoteDevice2Impl(BluetoothDevice _rbd) {
		super();
		this.bd = _rbd;
	}
	
	/* (non-Javadoc)
	 * @see it.gerdavax.bluetooth.RemoteInterface#getFriendlyName()
	 */
	public String getFriendlyName() {
		return bd.getName();
	}
	
	/* (non-Javadoc)
	 * @see it.gerdavax.bluetooth.RemoteInterface#getAddress()
	 */
	public String getAddress() {
		return bd.getAddress();
	}
}
