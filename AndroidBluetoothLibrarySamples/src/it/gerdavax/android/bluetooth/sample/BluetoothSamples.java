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

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BluetoothSamples extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.main);

		Button button = (Button) findViewById(R.id.bluetoothButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("it.gerdavax.android.bluetooth.sample.SERVICE");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.localDeviceButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("it.gerdavax.android.bluetooth.sample.LOCAL");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.deviceScanButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("it.gerdavax.android.bluetooth.sample.DISCOVERY");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.arduinoButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(BluetoothSamples.this, R.string.open_dialog_arduino_sample);
			}

		});
		
		button = (Button) findViewById(R.id.gpsButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent("it.gerdavax.android.bluetooth.sample.GPS");
				startActivity(intent);
			}

		});
		
		button = (Button) findViewById(R.id.aboutButton);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				showDialog(BluetoothSamples.this, R.string.about_dialog);
			}

		});
		
		showDialog(this, R.string.splash);
		
	}

	public static void showDialog(Context context, int text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}

		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
