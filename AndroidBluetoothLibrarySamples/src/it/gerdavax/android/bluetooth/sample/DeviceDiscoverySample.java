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

import it.gerdavax.android.bluetooth.BluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDevice;
import it.gerdavax.android.bluetooth.LocalBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceDiscoverySample extends ListActivity {
	private static final String TAG = "DeviceDiscoverySample";
	protected LocalBluetoothDevice localBluetoothDevice;
	protected static ProgressDialog dialog;
	protected Handler handler = new Handler();
	protected ArrayList<String> devices;
	protected AlertDialog deviceInfoDialog;

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
					name = localBluetoothDevice.getRemoteName(address);
					System.out.println("Device: " + name);
					RemoteBluetoothDevice remoteBluetoothDevice = localBluetoothDevice.getRemoteBluetoothDevice(address);

					switch (remoteBluetoothDevice.getDeviceMajorClass()) {
						case BluetoothDevice.BluetoothClasses.DEVICE_MAJOR_COMPUTER:
							System.out.println("- This is a computer");
							break;
						case BluetoothDevice.BluetoothClasses.DEVICE_MAJOR_PHONE:
							System.out.println("- This is a phone");
							break;
						case BluetoothDevice.BluetoothClasses.DEVICE_MAJOR_IMAGING:
							System.out.println("- This is an imaging device");
							break;
						case BluetoothDevice.BluetoothClasses.DEVICE_MAJOR_PERIPHERAL:
							System.out.println("- This is an generic peripheral");
							break;
						case BluetoothDevice.BluetoothClasses.DEVICE_MAJOR_AV:
							System.out.println("- This is an AV device");
							break;
						default:
							System.out.println("- This is other");
							break;
					}

					switch (remoteBluetoothDevice.getServiceMajorClass()) {
						case BluetoothDevice.BluetoothClasses.SERVICE_MAJOR_CLASS_POSITION:
							System.out.println("-- This is a GPS");
							break;
						case BluetoothDevice.BluetoothClasses.SERVICE_MAJOR_CLASS_INFORMATION:
							System.out.println("-- This is an information device");
							break;
					}
					
					deviceClass = "" + remoteBluetoothDevice.getDeviceClass();

				} catch (Exception e) {
					e.printStackTrace();
					name = "ERROR";
				}

				if (name != null) {
					deviceClass = name + " - " + deviceClass;
				}

				feedTitle.setText(address);
				deviceNameAndClass.setText(name);

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

		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					// System.out.println("Features: " +
					// localBluetoothDevice.getRemoteFeatures(devices.get(position)));

					// System.out.println("Channel: " +
					// localBluetoothDevice.getRemoteServiceChannel(devices.get(position),
					// RemoteBluetoothDevice.UUID_SERIAL_PORT_PROFILE));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

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
				e.printStackTrace();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?").setCancelable(false).setPositiveButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		deviceInfoDialog= builder.create();

		return deviceInfoDialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (dialog == deviceInfoDialog) {
			deviceInfoDialog.setMessage("");
		} else {
			super.onPrepareDialog(id, dialog);
		}
	}
}