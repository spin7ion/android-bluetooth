# Initialization #

Don't forget to add following permissions to the `AndroidManifest.xml` of your application:

```
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```


The entry-point for the Bluetooth API is `LocalBluetoothDevice` class, that has to be initialized with a Context instance (for instance, current Activity):

```
LocalBluetoothDevice localBT = LocalBluetoothDevice.initLocalDevice(_context);
```


`LocalBluetoothDevice` **must** be closed before leaving the activity. The best is to close it in the `onDestroy()` callback:

```
@Override
protected void onDestroy() {
   if (localBT != null) {
   localBT.close();
   }
   super.onDestroy();
}
```


# Bluetooth enable/disable #

TO BE ADDED


# Bluetooth device discovery #

TO BE ADDED