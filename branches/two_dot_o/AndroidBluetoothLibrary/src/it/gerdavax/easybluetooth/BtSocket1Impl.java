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
		Logger.v(this,"creating "+this);
	}

	@Override
	public void close() throws IOException {
		Logger.v(this,"about to close "+this);
		socket.closeSocket();
	}

	@Override
	public InputStream getInputStream() throws Exception {
		Logger.v(this,"getInputStream "+this);
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws Exception {
		Logger.v(this,"getOutputStream "+this);
		return socket.getOutputStream();
	}

}
