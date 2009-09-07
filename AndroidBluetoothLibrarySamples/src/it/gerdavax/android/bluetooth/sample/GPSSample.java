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
import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;
import it.gerdavax.android.bluetooth.RemoteBluetoothDeviceListener;
import it.gerdavax.android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;
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

public class GPSSample extends ListActivity {
	private static final String TAG = "AndroidBluetoothTest";
	private StringBuffer buffer = new StringBuffer();
	private TextView text;
	private MyThread t, t2;
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
					feedItem = new LinearLayout(GPSSample.this);
					String inflater = Context.LAYOUT_INFLATER_SERVICE;
					LayoutInflater vi = (LayoutInflater) GPSSample.this.getSystemService(inflater);
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
				// e.printStackTrace();
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

	private class MyThread extends Thread {
		boolean halt = false;
	}
	
	private class RemoteBluetoothDeviceEventHandler implements RemoteBluetoothDeviceListener {
		RemoteBluetoothDevice device;

		public void paired() {
			// connects to channel 1
			connectTo(device, 1);
		}

		public void pinRequested() {
			Log.d(TAG, "pinRequested()");
			
			// does not work as expected. To be investigated...
			//
			//Intent intent = new Intent("android.bluetooth.intent.action.PAIRING_REQUEST");
			//startActivity(intent);
		}

		public void gotServiceChannel(int serviceID, int channel) {
			// TODO Auto-generated method stub
			
		}

		public void serviceChannelNotAvailable(int serviceID) {
			// TODO Auto-generated method stub
			
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		DeviceAdapter adapter = new DeviceAdapter();
		setListAdapter(adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {

				final String address = devices.get(position);

				try {
					AlertDialog.Builder builder = new AlertDialog.Builder(GPSSample.this);
					builder.setMessage("Do you want to connect to this device?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							pair(address);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		try {
			localBluetoothDevice = LocalBluetoothDevice.initLocalDevice(this);

			if (localBluetoothDevice.isEnabled()) {
				localBluetoothDevice.setListener(adapter);

				BluetoothSamples.showDialog(this, R.string.open_dialog_gps);
				
			} else {
				BluetoothSamples.showDialog(this, R.string.bluetooth_not_enabled);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Connected to BT device");

		text = (TextView) dialog.findViewById(R.id.text);
		text.setText("(no data)");

		return dialog;
	}

	@Override
	protected void onDestroy() {
		super.onPause();
		System.out.println("PAUSE");
		
		localBluetoothDevice.close(this);
		
		/*
		if (t != null) {
			t.halt = true;
		}
		
		if (t2 != null) {
			t2.halt = true;
		}*/
	}

	private void pair(String address) {
		RemoteBluetoothDevice device = localBluetoothDevice.getRemoteBluetoothDevice(address);
		RemoteBluetoothDeviceEventHandler listener = new RemoteBluetoothDeviceEventHandler();
		listener.device = device;
		device.setListener(listener);
		device.pair();
	}

	private void connectTo(final RemoteBluetoothDevice device, final int port) {
		t = new MyThread() {
			@Override
			public void run() {
				try {
					Log.d(TAG, "Connecting...");

					BluetoothSocket socket = device.openSocket(port);

					InputStream input = socket.getInputStream();
					OutputStream output = socket.getOutputStream();
					output.write("TEST".getBytes());

					byte[] buffer = new byte[256];
					int read;
					while ((read = input.read(buffer)) != -1 && !halt) {
						String string = new String(buffer, 0, read);
						appendString(string);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

		// very bad code... just for demo purpose... :-(
		t2 = new MyThread() {
			@Override
			public void run() {
				while (true && !halt) {
					try {
						sleep(1000);
						if (text != null && buffer.length() > 0) {
							handler.post(new Runnable() {
								public void run() {
									text.setText(buffer.toString());
									if (buffer.length() > 3000) {
										buffer.delete(0, 1000);
									}
								}
							});
						} else {
							System.out.println("Text is null!");
						}
					} catch (Exception e) {

					}
				}
			}
		};
		t2.start();

		handler.post(new Runnable() {

			public void run() {
				showDialog(0);
			}
		});
	}

	private void appendString(String string) {
		buffer.append(string);
	}
	
	protected void showProgressDialog() {
		handler.post(new Runnable() {
			public void run() {
				dialog = ProgressDialog.show(GPSSample.this, "", "Scanning Bluetooth devices. Please wait...", true);
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