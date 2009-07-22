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
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceDiscoverySample extends ListActivity {
	private static final String TAG = "DeviceDiscoverySample";
	protected LocalBluetoothDevice localBluetoothDevice;
	protected static ProgressDialog dialog;
	protected Handler handler = new Handler();
	protected ArrayList<String> devices;

	protected class DeviceAdapter extends BaseAdapter implements LocalBluetoothDeviceListener {

		public int getCount() {
			if (devices != null) {
				return devices.size();
			}
			return 0;
		}

		public Object getItem(int position) {
			return devices.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout feedItem = null;

			try {
				if (convertView == null) {
					feedItem = new LinearLayout(DeviceDiscoverySample.this);
					String inflater = Context.LAYOUT_INFLATER_SERVICE;
					LayoutInflater vi = (LayoutInflater) DeviceDiscoverySample.this.getSystemService(inflater);
					vi.inflate(R.layout.item, feedItem, true);
				} else {
					feedItem = (LinearLayout) convertView;
				}

				TextView feedTitle = (TextView) feedItem.findViewById(R.id.address);
				TextView deviceNameAndClass = (TextView) feedItem.findViewById(R.id.name);

				String address = devices.get(position);
				String name = "null";
				String deviceClass = "null";
				try {
					deviceClass = "" + localBluetoothDevice.getRemoteClass(address);
					name = localBluetoothDevice.getRemoteName(address);
				} catch (Exception e) {
					e.printStackTrace();
					name = "ERROR";
				}

				if (name != null) {
					deviceClass = name + " - " + deviceClass;
				}

				feedTitle.setText(address);
				deviceNameAndClass.setText(deviceClass);

			} catch (Exception e) {
			}

			return feedItem;
		}

		public void scanCompleted(ArrayList<String> devs) {
			devices = devs;
			notifyDataSetChanged();
			hideProgressDialog();
		}

		public void scanStarted() {
			showProgressDialog();
		}

		public void disabled() {

		}

		public void enabled() {

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		DeviceAdapter adapter = new DeviceAdapter();
		setListAdapter(adapter);

		try {
			localBluetoothDevice = LocalBluetoothDevice.initLocalDevice(this);

			if (localBluetoothDevice.isEnabled()) {
				Log.d(TAG, "Bluetooth is enabled");
				System.out.println("My address: " + localBluetoothDevice.getAddress());

				localBluetoothDevice.setListener(adapter);
				
				BluetoothSamples.showDialog(this, R.string.open_dialog_device_discovery);
				
			} else {
				BluetoothSamples.showDialog(this, R.string.bluetooth_not_enabled);
			}
		} catch (Exception e) {
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		localBluetoothDevice.close(this);
	}

	protected void showProgressDialog() {
		handler.post(new Runnable() {
			public void run() {
				dialog = ProgressDialog.show(DeviceDiscoverySample.this, "", "Scanning Bluetooth devices. Please wait...", true);

				System.out.println(dialog);
			}
		});
	}

	protected void hideProgressDialog() {
		handler.post(new Runnable() {
			public void run() {
				if (dialog != null) {
					dialog.dismiss();
				} 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "Scan").setIcon(android.R.drawable.ic_menu_search);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			try {
				localBluetoothDevice.scan();
			} catch (Exception e) {

			}
		}
		return super.onOptionsItemSelected(item);
	}

}