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

import it.gerdavax.android.bluetooth.util.ReflectionUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 * This class is the entry point of the Android Bluetooth Library. It implements
 * a singleton pattern.
 * 
 * <br/>
 * <b>WARNING: do not forget to invoke close(Context) when your application
 * leaves, otherwise the LocalBluetoothDevice may not work on next
 * invocations.</b><br/>
 * 
 * 
 * First step for using the Bluetooth services is to initialize the instance of
 * the LocalBluetoothDevice:
 * 
 * <pre>
 * LocalBluetoothDevice localBluetoothDevice = LocalBluetoothDevice.init(context);
 * </pre>
 * 
 * where <code>context</code> is the context of Activity which manages the
 * LocalBluetoothDevice instance. Bluetooth service status (enabled/disabled)
 * can be retrieved using method
 * 
 * <pre>
 * localBluetoothDevice.isEnabled();
 * </pre>
 * 
 * and the service can be enabled and disabled using method
 * 
 * <pre>
 * localBluetoothDevice.setEnabled(boolean);
 * </pre>
 * 
 * To start scanning (discovery) of surrounding Bluetooth devices just invoke
 * the
 * 
 * <pre>
 * localBluetoothDevice.scan();
 * </pre>
 * 
 * method. Both service on/off and device scanning are asynchronous processes,
 * therefore to receive notification about that, the application must provide a
 * LocalBluetoothDeviceListener and set it to the LocalBluetoothDevice instance.
 * 
 * @author Stefano Sanna - gerdavax@gmail.com - http://www.gerdavax.it
 * 
 */
public final class LocalBluetoothDevice {
	private static final String TAG = "LocalBluetoothDevice";
	private static LocalBluetoothDevice _localDevice;
	private static Object bluetoothService;
	private static Class bluetoothServiceClass;
	private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
	private LocalBluetoothDeviceListener listener;
	private ArrayList<String> devices = new ArrayList<String>();
	private Hashtable<String, RemoteBluetoothDeviceImpl> remoteDevices = new Hashtable<String, RemoteBluetoothDeviceImpl>();

	private class RemoteBluetoothDeviceImpl implements RemoteBluetoothDevice {
		private String address;
		private int deviceClass;
		private String name;
		private String rssi;
		private RemoteBluetoothDeviceListener listener;
		private Hashtable<Integer, BluetoothSocketImpl> sockets = new Hashtable<Integer, BluetoothSocketImpl>();

		RemoteBluetoothDeviceImpl(String address, int deviceClass, String rssi) {
			this.address = address;
			this.deviceClass = deviceClass;
			this.rssi = rssi;
		}

		RemoteBluetoothDeviceImpl(String address) {
			this(address, 0, null);
		}

		public String getAddress() {
			return address;
		}

		public String getName() {
			if (name == null) {
				try {
					name = getRemoteName(address);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return name;
		}

		public String getRSSI() {
			return rssi;
		}

		public int getDeviceClass() {
			if (deviceClass != 0) {
				try {
					deviceClass = getRemoteClass(address);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return deviceClass;
		}

		public void setPin(String pin) {
			try {
				LocalBluetoothDevice.this.setPin(address, pin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void pair() {
			try {
				if (isPaired()) {
					Log.d(TAG, address + " is already paired");
					listener.paired();
				} else {
					createBond(address);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public boolean isPaired() {
			try {
				int bondState = getBondState(address);

				switch (bondState) {
					case BluetoothBroadcastReceiver.BOND_BONDED:
						return true;
					case BluetoothBroadcastReceiver.BOND_BONDING:
					case BluetoothBroadcastReceiver.BOND_NOT_BONDED:
						return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		public void pair(String pin) {
			throw new UnsupportedOperationException();
		}

		public BluetoothSocket openSocket(int port) throws Exception {
			Integer portKey = new Integer(port);

			BluetoothSocketImpl socket;

			if (sockets.containsKey(portKey)) {
				socket = sockets.get(portKey);
			} else {
				socket = new BluetoothSocketImpl(this, port);
				sockets.put(portKey, socket);
			}

			return socket;
		}

		public void setListener(RemoteBluetoothDeviceListener listener) {
			this.listener = listener;
		}

		void dispose() {
			Enumeration<Integer> keys = sockets.keys();

			while (keys.hasMoreElements()) {
				try {
					sockets.get(keys.nextElement()).close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		void notifyPairingRequested() {
			if (listener != null) {
				listener.pinRequested();
			}
		}
	}

	private class BluetoothSocketImpl implements BluetoothSocket {
		private RemoteBluetoothDeviceImpl remoteBluetoothDevice;
		private int port;
		private Object bluetoothSocketObject;
		private Class bluetoothSocketClass;
		private InputStream inputStream;
		private OutputStream outputStream;

		BluetoothSocketImpl(RemoteBluetoothDeviceImpl remoteBluetoothDevice, int port) throws Exception {
			Log.d(TAG, "creating new BluetoothSocket for " + remoteBluetoothDevice.address + " on port " + port);
			this.remoteBluetoothDevice = remoteBluetoothDevice;
			this.port = port;
			connect();
		}

		void connect() throws Exception {
			// quite strange: new package name with old class name...!!!
			bluetoothSocketClass = Class.forName("android.bluetooth.RfcommSocket");

			bluetoothSocketObject = bluetoothSocketClass.newInstance();

			Method createMethod = bluetoothSocketClass.getMethod("create", new Class[] {});
			createMethod.invoke(bluetoothSocketObject, new Object[] {});

			Method connectMethod = bluetoothSocketClass.getMethod("connect", new Class[] { String.class, int.class });
			connectMethod.invoke(bluetoothSocketObject, new Object[] { remoteBluetoothDevice.address, port });
		}

		public InputStream getInputStream() throws Exception {
			if (inputStream == null) {
				Method getInputStreamMethod = bluetoothSocketClass.getMethod("getInputStream", new Class[] {});
				Object returnValue = getInputStreamMethod.invoke(bluetoothSocketObject, new Object[] {});
				inputStream = (InputStream) returnValue;
			}
			return inputStream;
		}

		public OutputStream getOutputStream() throws Exception {
			if (outputStream == null) {
				Method getOutputStreamMethod = bluetoothSocketClass.getMethod("getOutputStream", new Class[] {});
				Object returnValue = getOutputStreamMethod.invoke(bluetoothSocketObject, new Object[] {});
				outputStream = (OutputStream) returnValue;
			}
			return outputStream;
		}

		public void close() throws Exception {
			Log.d("BluetoothSocket", "Closing socket to " + remoteBluetoothDevice.address + " on port " + port);
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {

				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {

				}
			}

			Method destroyMethod = bluetoothSocketClass.getMethod("destroy", new Class[] {});
			destroyMethod.invoke(bluetoothSocketObject, new Object[] {});
			remoteBluetoothDevice.sockets.remove(new Integer(port));
		}

		public int getPort() throws Exception {
			return port;
		}

		public RemoteBluetoothDevice getRemoteBluetoothDevice() {
			return remoteBluetoothDevice;
		}
	}

	private static class BluetoothBroadcastReceiver extends BroadcastReceiver {
		private static final String TAG_RECEIVER = "BluetoothBroadcastReceiver";
		public static final String SCAN_MODE = "android.bluetooth.intent.SCAN_MODE";
		public static final String ADDRESS = "android.bluetooth.intent.ADDRESS";
		public static final String NAME = "android.bluetooth.intent.NAME";
		public static final String ALIAS = "android.bluetooth.intent.ALIAS";
		public static final String RSSI = "android.bluetooth.intent.RSSI";
		public static final String CLASS = "android.bluetooth.intent.CLASS";
		public static final String BLUETOOTH_STATE = "android.bluetooth.intent.BLUETOOTH_STATE";
		public static final String BLUETOOTH_PREVIOUS_STATE = "android.bluetooth.intent.BLUETOOTH_PREVIOUS_STATE";
		public static final String HEADSET_STATE = "android.bluetooth.intent.HEADSET_STATE";
		public static final String HEADSET_PREVIOUS_STATE = "android.bluetooth.intent.HEADSET_PREVIOUS_STATE";
		public static final String HEADSET_AUDIO_STATE = "android.bluetooth.intent.HEADSET_AUDIO_STATE";
		public static final String BOND_STATE = "android.bluetooth.intent.BOND_STATE";
		public static final String BOND_PREVIOUS_STATE = "android.bluetooth.intent.BOND_PREVIOUS_STATE";
		public static final String REASON = "android.bluetooth.intent.REASON";
		public static final String REMOTE_DEVICE_FOUND_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_FOUND";
		public static final String DISCOVERY_COMPLETED_ACTION = "android.bluetooth.intent.action.DISCOVERY_COMPLETED";
		public static final String DISCOVERY_STARTED_ACTION = "android.bluetooth.intent.action.DISCOVERY_STARTED";
		public static final String BLUETOOTH_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.BLUETOOTH_STATE_CHANGED";
		public static final String NAME_CHANGED_ACTION = "android.bluetooth.intent.action.NAME_CHANGED";
		public static final String SCAN_MODE_CHANGED_ACTION = "android.bluetooth.intent.action.SCAN_MODE_CHANGED";
		public static final String PAIRING_REQUEST_ACTION = "android.bluetooth.intent.action.PAIRING_REQUEST";
		public static final String PAIRING_CANCEL_ACTION = "android.bluetooth.intent.action.PAIRING_CANCEL";
		public static final String REMOTE_DEVICE_DISAPPEARED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISAPPEARED";
		public static final String REMOTE_DEVICE_CLASS_UPDATED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISAPPEARED";
		public static final String REMOTE_DEVICE_CONNECTED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_CONNECTED";
		public static final String REMOTE_DEVICE_DISCONNECT_REQUESTED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECT_REQUESTED";
		public static final String REMOTE_DEVICE_DISCONNECTED_ACTION = "android.bluetooth.intent.action.REMOTE_DEVICE_DISCONNECTED";
		public static final String REMOTE_NAME_UPDATED_ACTION = "android.bluetooth.intent.action.REMOTE_NAME_UPDATED";
		public static final String REMOTE_NAME_FAILED_ACTION = "android.bluetooth.intent.action.REMOTE_NAME_FAILED";
		public static final String BOND_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.BOND_STATE_CHANGED_ACTION";
		public static final String HEADSET_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.HEADSET_STATE_CHANGED";
		public static final String HEADSET_AUDIO_STATE_CHANGED_ACTION = "android.bluetooth.intent.action.HEADSET_ADUIO_STATE_CHANGED";
		public static final int BOND_NOT_BONDED = 0;
		public static final int BOND_BONDED = 1;
		public static final int BOND_BONDING = 2;
		public static final int BLUETOOTH_STATE_OFF = 0;
		public static final int BLUETOOTH_STATE_TURNING_ON = 1;
		public static final int BLUETOOTH_STATE_ON = 2;
		public static final int BLUETOOTH_STATE_TURNING_OFF = 3;
		private boolean registered = false;

		BluetoothBroadcastReceiver(Context context) {
			register(context);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(DISCOVERY_STARTED_ACTION)) {
				processDiscoveryStarted();
			} else if (action.equals(REMOTE_DEVICE_FOUND_ACTION)) {
				processRemoteDeviceFound(intent);
			} else if (action.equals(DISCOVERY_COMPLETED_ACTION)) {
				processDiscoveryCompleted();
			} else if (action.equals(PAIRING_REQUEST_ACTION)) {
				processPairingRequested(intent);
				//System.out.println("Pairing requested");
				/*
				 * try {_localDevice.setPin(intent.getStringExtra(
				 * "android.bluetooth.intent.ADDRESS"), "0000"); } catch
				 * (Exception e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
			} else if (action.equals(BOND_STATE_CHANGED_ACTION)) {
				processBondStateChanged(intent);
			} else if (action.equals(BLUETOOTH_STATE_CHANGED_ACTION)) {
				processBluetoothStateChanged(intent);
			}

		}

		private void processDiscoveryStarted() {
			System.out.println("Discovery started");
			_localDevice.listener.scanStarted();
		}
		
		private void processPairingRequested(Intent intent) {
			String address = intent.getStringExtra("android.bluetooth.intent.ADDRESS");
			
			Log.d(TAG, "Pairing requested for " + address);
			
			RemoteBluetoothDeviceImpl remoteBluetoothDevice = _localDevice.remoteDevices.get(address);
			
			if (remoteBluetoothDevice != null) {
				remoteBluetoothDevice.notifyPairingRequested();
			}
		}

		private void processRemoteDeviceFound(Intent intent) {
			String address = intent.getStringExtra("android.bluetooth.intent.ADDRESS");

			//System.out.println("Device found! " + address);

			_localDevice.devices.add(address);

			/*
			Bundle extras = intent.getExtras();
			Set<String> keys = extras.keySet();
			Iterator<String> iterator = keys.iterator();
			String key;
			while (iterator.hasNext()) {
				key = iterator.next();
				System.out.println("Extra kay: " + key);
			}
			*/
		}

		private void processDiscoveryCompleted() {
			if (_localDevice.listener != null) {
				//System.out.println("Discovery completed");
				_localDevice.listener.scanCompleted(_localDevice.devices);
			}
		}

		private void processBondStateChanged(Intent intent) {
			String address = intent.getStringExtra(ADDRESS);
			int previousBondState = intent.getIntExtra(BOND_PREVIOUS_STATE, -1);
			int bondState = intent.getIntExtra(BOND_STATE, -1);
			Log.d(TAG_RECEIVER, "processBondStateChanged() for device " + address);
		}

		private void processBluetoothStateChanged(Intent intent) {
			int previousBluetoothState = intent.getIntExtra(BLUETOOTH_PREVIOUS_STATE, -1);
			int bluetoothState = intent.getIntExtra(BLUETOOTH_STATE, -1);
			Log.d(TAG_RECEIVER, "processBluetoothStateChanged(): " + bluetoothState);

			if (_localDevice.listener != null) {
				switch (bluetoothState) {
					case BLUETOOTH_STATE_ON:
						_localDevice.listener.enabled();
						break;
					case BLUETOOTH_STATE_OFF:
						_localDevice.listener.disabled();
						break;
				}
			}
		}

		public void register(Context context) {
			if (!registered) {
				Log.d(TAG_RECEIVER, "Registering");
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(BLUETOOTH_STATE_CHANGED_ACTION);
				intentFilter.addAction(REMOTE_DEVICE_FOUND_ACTION);
				intentFilter.addAction(DISCOVERY_COMPLETED_ACTION);
				intentFilter.addAction(DISCOVERY_STARTED_ACTION);
				intentFilter.addAction(PAIRING_REQUEST_ACTION);
				intentFilter.addAction(BOND_STATE_CHANGED_ACTION);
				context.registerReceiver(this, intentFilter);
				registered = true;
			} else {
				Log.d(TAG_RECEIVER, "Already registered");
			}
		}

		public void close(Context context) {
			try {
				Log.d(TAG_RECEIVER, "Unregistering");
				context.unregisterReceiver(this);
				registered = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private LocalBluetoothDevice() {

	}

	/**
	 * 
	 * @param context
	 *            the context of the Activity which uses the
	 *            LocalBluetoothDevice
	 * @return the unique instance of LocalBluetoothDevice
	 * @throws Exception
	 *             if system's Bluetooth service can't be addressed
	 */
	public static LocalBluetoothDevice initLocalDevice(Context context) throws Exception {
		//Log.d(TAG, "getLocalDevice");
		if (_localDevice == null) {
			Log.d(TAG, "_localDevice is null");
			bluetoothService = context.getSystemService("bluetooth");
			bluetoothServiceClass = bluetoothService.getClass();
			//ReflectionUtils.printMethods(bluetoothServiceClass);
			_localDevice = new LocalBluetoothDevice();
			if (bluetoothBroadcastReceiver != null) {
				System.out.println("Broadcast receiver already exists!");
				context.unregisterReceiver(bluetoothBroadcastReceiver);
			}
			bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(context);
		} else {
			Log.d(TAG, "_localDevice is NOT null");
			bluetoothBroadcastReceiver.register(context);
		}
		return _localDevice;
	}

	/**
	 * Gets unique instance of LocalBluetoothDevice. 
	 * @return
	 * @throws IllegalStateException
	 */
	public static LocalBluetoothDevice getLocalDevice() throws IllegalStateException {
		if (_localDevice == null) {
			throw new IllegalStateException("LocalBluetoothDevice has not been initialized. Call init() with a valid context.");
		}
		return _localDevice;
	}

	/**
	 * Gets the BD Address of local device
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAddress() throws Exception {
		Method getAddressMethod = bluetoothServiceClass.getMethod("getAddress", new Class[] {});
		return getAddressMethod.invoke(bluetoothService, new Object[] {}).toString();
	}

	/**
	 * Gets friendly name assigned to local device
	 * 
	 * @return The friendly name of this device
	 * @throws Exception
	 */
	public String getName() throws Exception {
		Method getNameMethod = bluetoothServiceClass.getMethod("getName", new Class[] {});
		return getNameMethod.invoke(bluetoothService, new Object[] {}).toString();
	}

	public String getManufacturer() throws Exception {
		Method getManufacturerMethod = bluetoothServiceClass.getMethod("getManufacturer", new Class[] {});
		return getManufacturerMethod.invoke(bluetoothService, new Object[] {}).toString();
	}

	public String getCompany() throws Exception {
		Method getCompanyMethod = bluetoothServiceClass.getMethod("getCompany", new Class[] {});
		return getCompanyMethod.invoke(bluetoothService, new Object[] {}).toString();
	}

	/**
	 * Starts discovery process for remote Bluetooth devices 
	 * 
	 * @throws Exception
	 */
	public void scan() throws Exception {
		Method startDiscoveryMethod = bluetoothServiceClass.getMethod("startDiscovery", new Class[] {});
		startDiscoveryMethod.invoke(bluetoothService, new Object[] {});
		devices.clear();
	}

	/**
	 * 
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public String getRemoteName(String address) throws Exception {
		Method getRemoteNameMethod = bluetoothServiceClass.getMethod("getRemoteName", new Class[] { String.class });
		return getRemoteNameMethod.invoke(bluetoothService, new Object[] { address }).toString();
	}

	/**
	 * Sets the LocalBluetoothDeviceListener which will receive events about
	 * Bluetooth activation and device discovery progress
	 * 
	 * WARNING: all invocations to the listener are synchronous, therefore
	 * listener's method implementation must avoid long operations.
	 * 
	 * @param listener
	 */
	public void setListener(LocalBluetoothDeviceListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public int getRemoteClass(String address) throws Exception {
		Method getRemoteClassMethod = bluetoothServiceClass.getMethod("getRemoteClass", new Class[] { String.class });
		Integer returnValue = (Integer) getRemoteClassMethod.invoke(bluetoothService, new Object[] { address });
		return returnValue.intValue();
	}

	/**
	 * Closes the LocalBluetoothDevice and unregisters any Bluetooth service
	 * broadcast listener. All opened BluetoothSocket will be closed.
	 * 
	 * @param context
	 */
	public void close(Context context) {
		// close all sockets
		Enumeration<String> keys = remoteDevices.keys();
		while (keys.hasMoreElements()) {
			try {
				remoteDevices.get(keys.nextElement()).dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// unregister broadcast receiver
		bluetoothBroadcastReceiver.close(context);
	}

	private boolean createBond(String address) throws Exception {
		Method createBondMethod = bluetoothServiceClass.getMethod("createBond", new Class[] { String.class });
		Boolean returnValue = (Boolean) createBondMethod.invoke(bluetoothService, new Object[] { address });
		return returnValue.booleanValue();
	}

	private int getBondState(String address) throws Exception {
		Method getBondStateMethod = bluetoothServiceClass.getMethod("getBondState", new Class[] { String.class });
		Integer returnValue = (Integer) getBondStateMethod.invoke(bluetoothService, new Object[] { address });
		return returnValue.intValue();
	}

	private boolean setPin(String address, String pin) throws Exception {
		Method setPinMethod = bluetoothServiceClass.getMethod("setPin", new Class[] { String.class, byte[].class });
		Boolean returnValue = (Boolean) setPinMethod.invoke(bluetoothService, new Object[] { address, pin.getBytes() });
		return returnValue.booleanValue();
	}

	/**
	 * 
	 * @return true is Bluetooth is enabled
	 * @throws Exception
	 */
	public boolean isEnabled() throws Exception {
		Method isEnabledMethod = bluetoothServiceClass.getMethod("isEnabled", new Class[] {});
		Boolean returnValue = (Boolean) isEnabledMethod.invoke(bluetoothService, new Object[] {});
		return returnValue.booleanValue();
	}

	/**
	 * 
	 * @param enabled
	 * @return
	 * @throws Exception
	 */
	public boolean setEnabled(boolean enabled) throws Exception {
		String methodName = "enable";
		if (!enabled) {
			methodName = "disable";
		}

		Method enableDisableMethod = bluetoothServiceClass.getMethod(methodName, new Class[] {});
		Boolean returnValue = (Boolean) enableDisableMethod.invoke(bluetoothService, new Object[] {});
		return returnValue.booleanValue();
	}

	/**
	 * 
	 * @param address
	 *            the BDADDR (address) of required device
	 * @return the RemoteBluetoothDevice instance associated with assigned
	 *         address
	 */
	public RemoteBluetoothDevice getRemoteBluetoothDevice(String address) {
		RemoteBluetoothDeviceImpl remoteBluetoothDevice;
		if (remoteDevices.containsKey(address)) {
			remoteBluetoothDevice = remoteDevices.get(address);
		} else {
			remoteBluetoothDevice = createRemoteBluetoothDevice(address);
			remoteDevices.put(address, remoteBluetoothDevice);
		}
		return remoteBluetoothDevice;
	}

	private RemoteBluetoothDeviceImpl createRemoteBluetoothDevice(String address, int deviceClass, String rssi) {
		RemoteBluetoothDeviceImpl impl = new RemoteBluetoothDeviceImpl(address, deviceClass, rssi);
		return impl;
	}

	private RemoteBluetoothDeviceImpl createRemoteBluetoothDevice(String address) {
		RemoteBluetoothDeviceImpl impl = new RemoteBluetoothDeviceImpl(address);
		return impl;
	}

}
