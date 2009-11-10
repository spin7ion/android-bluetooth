package it.gerdavax.bluetooth.android1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.bluetooth.BtSocket;

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
	public void connect() throws IOException {
		//nop
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
