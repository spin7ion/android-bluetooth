package it.gerdavax.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * This utility class encapsulates the android Log class putting the same tag on
 * each log line, adding a source string for the object that originated the log
 * call, and guarding for the log level
 * 
 * @author Emanuele Di Saverio (e.disaverio at beeweeb.com)
 * 
 */
public class Logger {
	private String TAG = "Logger";
	private static int LOGLEVEL = Log.VERBOSE;
	private static Map<String,Logger> loggers = new HashMap<String,Logger>();
	private Logger(String tag) {
		TAG = tag;
	}
	
	public synchronized static Logger getLogger(Object forTag) {
		String tag = forTag.toString();
		Logger toRet = loggers.get(tag);
		if (toRet == null) {
			toRet = new Logger(tag);
			loggers.put(tag, toRet);
		}
		return toRet;
	}
	
	public synchronized static Logger getLogger() {
		return getLogger("LOG");
	} 
	
	public static void setLogLevel(int newLev) {
		if (Log.VERBOSE <= newLev && newLev <= Log.ASSERT) {
			LOGLEVEL = newLev;
		}
	}

	private static boolean isLoggable(int level) {
		return (LOGLEVEL <= level);
	}

	@SuppressWarnings("unchecked")
	private static String getSourceString(Object o) {
		if (o instanceof Class) {
			return ((Class) o).getName() + " - ";
		} else {
			return o.getClass().getName() + " - ";
		}
	}

	public void v(String message) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, message);
		}
	}

	public void v(String message, Throwable tr) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, message, tr);
		}
	}

	public void v(Object src, String message) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, getSourceString(src) + message);
		}
	}

	public void v(Object src, String message, Throwable tr) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, getSourceString(src) + message, tr);
		}
	}

	public void d(String message) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, message);
		}
	}

	public void d(String message, Throwable tr) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, message, tr);
		}
	}

	public void d(Object src, String message) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, getSourceString(src) + message);
		}
	}

	public void d(Object src, String message, Throwable tr) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, getSourceString(src) + message, tr);
		}
	}

	public void i(String message) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, message);
		}
	}

	public void i(String message, Throwable tr) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, message, tr);
		}
	}

	public void i(Object src, String message) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, getSourceString(src) + message);
		}
	}

	public void i(Object src, String message, Throwable tr) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, getSourceString(src) + message, tr);
		}
	}

	public void w(String message) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, message);
		}
	}

	public void w(String message, Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, message, tr);
		}
	}

	public void w(Object src, String message) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, getSourceString(src) + message);
		}
	}

	public void w(Object src, String message, Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, getSourceString(src) + message, tr);
		}
	}

	public void w(Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, tr);
		}
	}

	public void w(Object src, Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, getSourceString(src), tr);
		}
	}

	public void e(String message) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, message);
		}
	}

	public void e(String message, Throwable tr) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, message, tr);
		}
	}

	public void e(Object src, String message) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, getSourceString(src) + message);
		}
	}

	public void e(Object src, String message, Throwable tr) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, getSourceString(src) + message, tr);
		}
	}
}
