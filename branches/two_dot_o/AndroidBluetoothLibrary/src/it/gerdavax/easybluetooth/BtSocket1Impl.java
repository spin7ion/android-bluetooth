package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.gerdavax.android.bluetooth.BluetoothSocket;

class BtSocket1Impl implements BtSocket {
	private BluetoothSocket socket = null;
	
	public BtSocket1Impl(BluetoothSocket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void close() throws IOException {
		socket.closeSocket();
	}

	@Override
	public InputStream getInputStream() throws Exception {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws Exception {
		return socket.getOutputStream();
	}

}