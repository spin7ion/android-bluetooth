/*
 * Copyright (C) 2009 Stefano Sanna
 * 
 * gerdavax@gmail.com - http://www.gerdavax.it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.gerdavax.android.bluetooth;

/**
 * <p>
 * Interface for managing a remote Bluetooth device and RFComm connections.
 * RemoteBluetoothDevice instances are created and managed by the unique
 * LocalBluetoothDevice instance.</p>
 * 
 * <p>
 * Device name, address and class can be retrieved without connecting to the
 * device. Before opening a BluetoothSocket for RFComm serial connection over
 * Bluetooth, the RemoteBluetoothDevice has to be paired by invoking the
 * <code>pair()</code> method. Pairing is an asynchronous process, therefore
 * this method returns immediately. To get notifications on pairing process, a
 * class implementing the RemoteBluetoothDeviceListener interface has to be
 * registered as listener to this instance. Pairing may require to type a PIN
 * number: if remote device asks for that, a message will appear on the
 * notification area of the handset (the listener will receive an event on the
 * method <code>pinRequested()</code> : once the user has typed the PIN, the
 * listener will get a <code>paired()</code> event that notifies that device is
 * connected and that BluetoothSocket instances can be created.
 * </p>
 * @author Stefano Sanna - gerdavax@gmail.com - http://www.gerdavax.it
 * 
 */
public interface RemoteBluetoothDevice {

	/**
	 * Gets the friendly name associated to this Bluetooth device. Friendly name
	 * could be not immediately retrieved by the Bluetooth service when the
	 * remote device is discovered
	 * 
	 * @return the friendly name of the device
	 */
	public String getName();

	/**
	 * Gets the BD Address of this device.
	 * 
	 * @return
	 */
	public String getAddress();

	/**
	 * Gets the signal strenght (Received Signal Strength Indicator) of this
	 * device
	 * 
	 * @return
	 */
	public String getRSSI();

	/**
	 * Gets the device class (see Bluetooth Protocol Specification)
	 * 
	 * @return the device class, as bit-masked integer
	 */
	public int getDeviceClass();

	/**
	 * Sets a default PIN for the pairing
	 * 
	 * @param pin
	 */
	public void setPin(String pin);

	/**
	 * Attempts to pair this remote device.
	 */
	public void pair();

	/**
	 * Attempts to pair this remote device with preassigned PIN.
	 * 
	 * @return
	 */
	public void pair(String pin);

	/**
	 * Gets the paired status of this device
	 * 
	 * @return true if this device is paired
	 */
	public boolean isPaired();

	/**
	 * Opens a BluetoothSocket (RFComm) to this device on assigned port. If the
	 * socket has been already opened, returns the existing instance.
	 * 
	 * @param port
	 * @return BluetoothSocket instance associated to given port
	 * @throws Exception
	 */
	public BluetoothSocket openSocket(int port) throws Exception;

	/**
	 * Sets the listener for events coming from this RemoteBluetoothDevice
	 * 
	 * @param listener
	 * @see RemoteBluetoothDeviceListener
	 */
	public void setListener(RemoteBluetoothDeviceListener listener);
}
