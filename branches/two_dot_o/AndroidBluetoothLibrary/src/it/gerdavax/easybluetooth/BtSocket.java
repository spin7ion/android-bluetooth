package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Bluetooth Socket. The usual set of java socket methods.
 * Keep in mind that closing a socket can destroy the currently open streams.
 * 
 * @author Emanuele Di Saverio (emanuele.disaverio at gmail.com)
 */
public interface BtSocket {
	
	public InputStream getInputStream() throws Exception;
	
	public OutputStream getOutputStream() throws Exception;
	
	public void close() throws IOException;
}	
