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

import java.util.ArrayList;

/**
 * Interface definition for a callback to be invoked when an event happens on
 * the LocalBluetoothDevice.
 * 
 * @author Stefano Sanna - gerdavax@gmail.com
 * 
 */
public interface LocalBluetoothDeviceListener {

	/**
	 * Called when Bluetooth stack has been enabled
	 */
	public void enabled();

	/**
	 * Called when Bluetooth stack has been disabled
	 */

	public void disabled();

	/**
	 * Called when remote device discovery has started
	 */
	public void scanStarted();

	/**
	 * Called when remote device discovery has been completed
	 * 
	 * @param devices the list of BD Addresses discovered
	 */
	public void scanCompleted(ArrayList<String> devices);
}
