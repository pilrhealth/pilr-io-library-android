package com.pilrhealth.pilriolib.logs;

import android.content.Context;
import android.util.Log;

public class AppLog {

	private static AppLog singleton;
	@SuppressWarnings("unused")
	private Context mContext;

	public static AppLog getLog(Context ctx) {
		if (singleton == null) {
			AppLog log = new AppLog(ctx);
			return log;
		} else {
			return singleton;
		}
	}

	public AppLog(Context ctx) {
		mContext = ctx;
	}

	public int e(String tag, String msg, Throwable tr) {
		logRecord(tag, "error", msg, null, null, Log.getStackTraceString(tr));
		return 1;
	}

	public int e(String tag, String msg, String arg0, String arg1) {
		logRecord(tag, "error", msg, arg0, arg1, null);
		return 1;
	}

	public int i(String tag, String msg, String arg0, String arg1) {
		logRecord(tag, "info", msg, arg0, arg1, null);
		return 1;
	}

	public int w(String tag, String msg, String arg0, String arg1) {
		logRecord(tag, "warn", msg, arg0, arg1, null);
		return 1;
	}

	public int d(String tag, String msg, String arg0, String arg1) {
		logRecord(tag, "debug", msg, arg0, arg1, null);
		return 1;
	}

	public int v(String tag, String msg, String arg0, String arg1) {
		logRecord(tag, "verbose", msg, arg0, arg1, null);
		return 1;
	}

	private synchronized boolean logRecord(String tag, String level, String msg, String arg0, String arg1, String stackTrace) {
		boolean success = false;
		try {
			// TODO
			// AppLogDB db = new AppLogDB(mContext);
			// String uuid = UUID.randomUUID().toString();
			// JSONObject record = new JSONObject();
			// record.put("record_uuid", uuid);
			// record.put("time", ISODateHelper.toString(new Date()));
			// record.put("tag", tag);
			// record.put("level", level);
			// record.put("msg", msg);
			// if(arg0 != null){record.put("arg0", arg0);}
			// if(arg1 != null){record.put("arg1", arg1);}
			// if(stackTrace != null){record.put("stack_trace", stackTrace);}
			// db.open();
			// success = db.storeRecord(uuid, tag, record);
			// db.close();
		} catch (Exception ex) {
			return false;
		}
		return success;
	}
}
