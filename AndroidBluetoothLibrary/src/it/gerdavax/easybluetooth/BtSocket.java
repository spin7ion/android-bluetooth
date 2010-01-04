package it.gerdavax.easybluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BtSocket {
	
	public InputStream getInputStream() throws Exception;
	
	public OutputStream getOutputStream() throws Exception;
	
	public void close() throws IOException;
}	
