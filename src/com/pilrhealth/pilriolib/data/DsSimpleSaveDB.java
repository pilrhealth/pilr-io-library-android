package com.pilrhealth.pilriolib.data;

import java.util.List;
import java.util.ListIterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.pilrhealth.pilriolib.logs.AppLog;

public class DsSimpleSaveDB {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "ds_simple_save_db";
	private static final String TABLE_NAME = "all_records";
	public static final String KEY_UUID = "uuid";
	public static final String KEY_PARTICIPANT_ID = "participant_id";
	public static final String KEY_TIME_CREATED = "unix_time_created";
	private static final String KEY_STREAM_ID = "stream_id";
	private static final String KEY_STREAM_VERSION = "stream_version";
	public static final String KEY_RECORD = "record";
	private static final String KEY_BEEN_UPLOADED = "been_uploaded";

	private Context mContext;
	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;

	public DsSimpleSaveDB(Context ctx) {
		mContext = ctx;
	}

	/* Open the database */
	public boolean open() {
		mDbHelper = new DbHelper(mContext);

		try {
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException e) {
			AppLog.getLog(mContext).e(this.getClass().getSimpleName(), "Error opening database.", e);
			return false;
		}
		return true;
	}

	/* Close the database */
	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	protected void saveRecords(String streamId, Integer streamVersion, List<DatasetRecord> records) throws Exception {
		ListIterator<DatasetRecord> lit = records.listIterator();

		while (lit.hasNext()) {
			DatasetRecord record = lit.next();
			ContentValues values = new ContentValues();
			values.put(KEY_UUID, record.getUUID());
			values.put(KEY_PARTICIPANT_ID, record.getParticipant());
			values.put(KEY_TIME_CREATED, record.getDateCreated().getTime());
			values.put(KEY_STREAM_ID, streamId);
			values.put(KEY_STREAM_VERSION, streamVersion.toString());
			values.put(KEY_RECORD, record.toJSONObject().toString());
			values.put(KEY_BEEN_UPLOADED, false);

			mDb.insert(TABLE_NAME, null, values);
		}
	}

	/**
	 * Query the data stream records that have been stored, returning a cursor
	 * to the database.
	 * 
	 * @param streamId
	 *            Stream ID that the data records belong to.
	 * 
	 * @param schemaVersion
	 *            Version number of the schema that the data is formatted for.
	 * 
	 * @param beenUploaded
	 *            A filter on the upload status of the records stored in the
	 *            database. Pass NULL to return records of any upload status.
	 * 
	 * @return A cursor for the queried records.
	 */
	public Cursor getCursor(String streamId, Integer schemaVersion, Boolean beenUploaded) {
		Cursor c = null;
		if (beenUploaded == null) {
			// return all records, regardless of upload status
			c = mDb.query(TABLE_NAME, new String[] { KEY_UUID, KEY_PARTICIPANT_ID, KEY_RECORD, KEY_TIME_CREATED }, KEY_STREAM_ID + "=? AND "
					+ KEY_STREAM_VERSION + "=?", new String[] { streamId, schemaVersion.toString() }, null, null, KEY_TIME_CREATED);
		} else if (beenUploaded) {
			c = mDb.query(TABLE_NAME, new String[] { KEY_UUID, KEY_PARTICIPANT_ID, KEY_RECORD, KEY_TIME_CREATED }, KEY_STREAM_ID + "=? AND "
					+ KEY_STREAM_VERSION + "=? AND " + KEY_BEEN_UPLOADED + "=?", new String[] { streamId, schemaVersion.toString(), "1" }, null, null,
					KEY_TIME_CREATED);
		} else {
			c = mDb.query(TABLE_NAME, new String[] { KEY_UUID, KEY_PARTICIPANT_ID, KEY_RECORD, KEY_TIME_CREATED }, KEY_STREAM_ID + "=? AND "
					+ KEY_STREAM_VERSION + "=? AND " + KEY_BEEN_UPLOADED + "=?", new String[] { streamId, schemaVersion.toString(), "0" }, null, null,
					KEY_TIME_CREATED);
		}
		return c;
	}

	public void deleteRecords(List<String> recordUuids) {
		String inParams = "'" + TextUtils.join("','", recordUuids) + "'";
		@SuppressWarnings("unused")
		int deletedCount = mDb.delete(TABLE_NAME, KEY_UUID + " IN (" + inParams + ")", null);
		return;
	}

	private static class DbHelper extends SQLiteOpenHelper {
		public DbHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase mDb) {
			final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + KEY_UUID + " text primary key, " + KEY_PARTICIPANT_ID + " text, "
					+ KEY_TIME_CREATED + " integer, " + KEY_STREAM_ID + " text, " + KEY_STREAM_VERSION + " integer, " + KEY_RECORD + " text, "
					+ KEY_BEEN_UPLOADED + " boolean)";
			mDb.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// no upgrades possible for simple save DB

		}
	}
}
