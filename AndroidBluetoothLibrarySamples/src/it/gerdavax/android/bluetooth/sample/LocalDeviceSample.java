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
package it.gerdavax.android.bluetooth.sample;

import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LocalDeviceSample extends Activity {
	private LocalBluetoothDevice localBluetoothDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.local_device);
		
		TextView bluetoothStatusTextView = (TextView) findViewById(R.id.localDeviceBluetoothStatus);
		TextView addressTextView = (TextView) findViewById(R.id.localDeviceAddress);
		TextView friendlyNameTextView = (TextView) findViewById(R.id.localDeviceFriendlyName);
		TextView manufacturerTextView = (TextView) findViewById(R.id.localDeviceManufacturer);
		TextView companyTextView = (TextView) findViewById(R.id.localDeviceCompany);
		
		try {
			localBluetoothDevice = LocalBluetoothDevice.initLocalDevice(this);

			if (localBluetoothDevice.isEnabled()) {
				bluetoothStatusTextView.setText(R.string.bluetooth_status_enabled);
				
				String address = localBluetoothDevice.getAddress();
				if (address != null) {
					addressTextView.setText("Address: " + address);
				}
				
				String name = localBluetoothDevice.getName();
				if (name != null) {
					friendlyNameTextView.setText("Name: " + name);
				}
				
				String manufacturer = localBluetoothDevice.getManufacturer();
				if (manufacturer != null) {
					manufacturerTextView.setText("Manufacturer: " + manufacturer);
				}
				
				String company = localBluetoothDevice.getCompany();
				if (company != null) {
					companyTextView.setText("Company: " + company);
				}
			} else {
				bluetoothStatusTextView.setText(R.string.bluetooth_status_not_enabled);
				BluetoothSamples.showDialog(this, R.string.bluetooth_not_enabled);
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BluetoothSamples.showDialog(this, R.string.open_dialog_local_device_sample);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		localBluetoothDevice.close(this);
	}
}
