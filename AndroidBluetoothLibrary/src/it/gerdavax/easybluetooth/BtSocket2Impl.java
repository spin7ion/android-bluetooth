package it.gerdavax.easybluetooth;

import it.gerdavax.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

class BtSocket2Impl implements BtSocket {
	private BluetoothSocket socket = null;
	private final Logger log = Logger.getLogger("EASYBT");
	
	public BtSocket2Impl(BluetoothSocket socket) {
		super();
		this.socket = socket;
		log.v(this,"creating "+this);
	}

	@Override
	public void close() throws IOException {
		log.v(this,"about to close "+this);
		socket.close();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		log.v(this,"getInputStream "+this);
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		log.v(this,"getOutputStream "+this);
		return socket.getOutputStream();
	}

}
