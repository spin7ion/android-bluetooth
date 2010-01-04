package it.gerdavax.easybluetooth.android2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

import it.gerdavax.easybluetooth.BtSocket;

class BtSocket2Impl implements BtSocket {
	private BluetoothSocket socket = null;
	
	public BtSocket2Impl(BluetoothSocket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

}
