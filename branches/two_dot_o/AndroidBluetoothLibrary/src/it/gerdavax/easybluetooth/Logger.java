package it.gerdavax.easybluetooth;

import android.util.Log;

/**
 * This utility class encapsulates the android Log class putting the GoPayment
 * tag on each log line, adding a source string for the object that originated
 * the log call, and guarding for the log level
 * 
 * @author Emanuele Di Saverio (e.disaverio at beeweeb.com)
 * 
 */
public class Logger {
	private static final String TAG = "easyBluetooth";
	private static int LOGLEVEL = Log.VERBOSE;
	
	private static boolean isLoggable(int level) {
		return LOGLEVEL <= level && Log.isLoggable(TAG, level);
	}
	
	@SuppressWarnings("unchecked")
	private static String getSourceString(Object o) {
		if (o instanceof Class) {
			return ((Class) o).getName() + " - ";
		} else {
			return o.getClass().getName() + " - ";
		}
	}

	public static void v(String message) {
		if ( isLoggable(Log.VERBOSE) ) {
			Log.v(TAG, message);
		}
	}

	public static void v(String message, Throwable tr) {
		if ( isLoggable(Log.VERBOSE) ) {
			Log.v(TAG, message, tr);
		}
	}

	public static void v(Object src, String message) {
		if ( isLoggable(Log.VERBOSE) ) {
			Log.v(TAG, getSourceString(src) + message);
		}
	}

	public static void v(Object src, String message, Throwable tr) {
		if ( isLoggable(Log.VERBOSE) ) {
			Log.v(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void d(String message) {
		if ( isLoggable(Log.DEBUG) ) {
			Log.d(TAG, message);
		}
	}

	public static void d(String message, Throwable tr) {
		if ( isLoggable(Log.DEBUG) ) {
			Log.d(TAG, message, tr);
		}
	}

	public static void d(Object src, String message) {
		if ( isLoggable(Log.DEBUG) ) {
			Log.d(TAG, getSourceString(src) + message);
		}
	}

	public static void d(Object src, String message, Throwable tr) {
		if ( isLoggable(Log.DEBUG) ) {
			Log.d(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void i(String message) {
		if ( isLoggable(Log.INFO) ) {
			Log.i(TAG, message);
		}
	}

	public static void i(String message, Throwable tr) {
		if ( isLoggable(Log.INFO) ) {
			Log.i(TAG, message, tr);
		}
	}

	public static void i(Object src, String message) {
		if ( isLoggable(Log.INFO) ) {
			Log.i(TAG, getSourceString(src) + message);
		}
	}

	public static void i(Object src, String message, Throwable tr) {
		if ( isLoggable(Log.INFO) ) {
			Log.i(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void w(String message) {
		if ( isLoggable(Log.WARN) ) {
			Log.w(TAG, message);
		}
	}

	public static void w(String message, Throwable tr) {
		if ( isLoggable(Log.WARN) ) {
			Log.w(TAG, message, tr);
		}
	}

	public static void w(Object src, String message) {
		if ( isLoggable(Log.WARN) ) {
			Log.w(TAG, getSourceString(src) + message);
		}
	}

	public static void w(Object src, String message, Throwable tr) {
		if ( isLoggable(Log.WARN) ) {
			Log.w(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void w(Throwable tr) {
		if ( isLoggable(Log.WARN) ) {
			Log.w(TAG, tr);
		}
	}

	public static void w(Object src, Throwable tr) {
		if ( isLoggable(Log.WARN) ) {
			Log.w(TAG, getSourceString(src), tr);
		}
	}

	public static void e(String message) {
		if ( isLoggable(Log.ERROR) ) {
			Log.e(TAG, message);
		}
	}

	public static void e(String message, Throwable tr) {
		if ( isLoggable(Log.ERROR) ) {
			Log.e(TAG, message, tr);
		}
	}

	public static void e(Object src, String message) {
		if ( isLoggable(Log.ERROR) ) {
			Log.e(TAG, getSourceString(src) + message);
		}
	}

	public static void e(Object src, String message, Throwable tr) {
		if ( isLoggable(Log.ERROR) ) {
			Log.e(TAG, getSourceString(src) + message, tr);
		}
	}
}
