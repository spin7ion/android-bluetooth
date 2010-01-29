package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.gerdavax.android.bluetooth.BluetoothSocket;
import it.gerdavax.util.Logger;

class BtSocket1Impl implements BtSocket {
	private BluetoothSocket socket = null;
	private final Logger log = Logger.getLogger("EASYBT");
	public BtSocket1Impl(BluetoothSocket socket) {
		super();
		this.socket = socket;
		log.v(this,"creating "+this);
	}

	@Override
	public void close() throws IOException {
		log.v(this,"about to close "+this);
		socket.closeSocket();
	}

	@Override
	public InputStream getInputStream() throws Exception {
		log.v(this,"getInputStream "+this);
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws Exception {
		log.v(this,"getOutputStream "+this);
		return socket.getOutputStream();
	}

}
