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

import java.util.ArrayList;

import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothServiceSample extends Activity implements LocalBluetoothDeviceListener {
	private TextView statusTextView;
	private Button button;
	private Handler handler = new Handler();
	private LocalBluetoothDevice localBluetoothDevice;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.service_sample);

		statusTextView = (TextView) findViewById(R.id.status);

		button = (Button) findViewById(R.id.bluetoothServiceButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				try {
					if (localBluetoothDevice.isEnabled()) {
						localBluetoothDevice.setEnabled(false);
						dialog = ProgressDialog.show(BluetoothServiceSample.this, "", "Disabling Bluetooth. Please wait...", true);
					} else {
						localBluetoothDevice.setEnabled(true);
						dialog = ProgressDialog.show(BluetoothServiceSample.this, "", "Enabling Bluetooth. Please wait...", true);
					}
				} catch (Exception e) {
					
				}
			}

		});

		try {
			localBluetoothDevice = LocalBluetoothDevice.initLocalDevice(this);
			localBluetoothDevice.setListener(this);

			if (localBluetoothDevice.isEnabled()) {
				setEnabled();
			} else {
				setDisabled();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BluetoothSamples.showDialog(this, R.string.open_dialog_bluetooth_service_sample);
	}

	private void setEnabled() {
		handler.post(new Runnable() {
			public void run() {
				statusTextView.setText("Bluetooth is ENABLED");
				button.setText("CLICK TO DISABLE");
			}
		});
	}

	private void setDisabled() {
		handler.post(new Runnable() {
			public void run() {
				statusTextView.setText("Bluetooth is DISABLED");
				button.setText("CLICK TO ENABLE");
			}
		});
	}

	public void disabled() {
		if (dialog != null) {
			dialog.cancel();
		}
		setDisabled();
	}

	public void enabled() {
		if (dialog != null) {
			dialog.cancel();
		}
		setEnabled();
	}

	public void scanCompleted(ArrayList<String> devices) {
	}

	public void scanStarted() {
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		localBluetoothDevice.close(this);
	}
	
	
}
