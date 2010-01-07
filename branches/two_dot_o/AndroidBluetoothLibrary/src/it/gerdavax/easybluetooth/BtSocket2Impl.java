package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

class BtSocket2Impl implements BtSocket {
	private BluetoothSocket socket = null;
	
	public BtSocket2Impl(BluetoothSocket socket) {
		super();
		this.socket = socket;
		Logger.v(this,"creating "+this);
	}

	@Override
	public void close() throws IOException {
		Logger.v(this,"about to close "+this);
		socket.close();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Logger.v(this,"getInputStream "+this);
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		Logger.v(this,"getOutputStream "+this);
		return socket.getOutputStream();
	}

}
