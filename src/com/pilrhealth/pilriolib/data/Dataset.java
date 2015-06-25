package com.pilrhealth.pilriolib.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public abstract class Dataset {

	private Context mContext;

	/**
	 * This class represents a data stream that has been configured on the PILR
	 * server, including a specific version number.<br/>
	 * <br/>
	 * 
	 * NOTE: the server allows multiple versions per data stream, but here each
	 * version will have its own data stream class.
	 * 
	 */
	public Dataset(Context ctx) {
		mContext = ctx;
	}

	/**
	 * Stream ID that must match an active data stream on the PILR server, for
	 * the project the app is uploading to.
	 * 
	 * @return Stream ID
	 */
	public abstract String getStreamId();

	/**
	 * Version number that must match the stream ID on the PILR server, for the
	 * project the app is uploading to.
	 * 
	 * @return
	 */
	public abstract Integer getSchemaVersion();

	private List<DatasetRecord> stashedRecords;

	/**
	 * Holds the record in a list, prior to saving to a database.
	 * 
	 * @param record
	 *            DataStreamRecord to store in the list.
	 */
	public void stashRecord(DatasetRecord record) {
		if (stashedRecords == null) {
			stashedRecords = new ArrayList<DatasetRecord>();
		}
		stashedRecords.add(record);
	}

	/**
	 * The number of records stashed in a list.
	 * 
	 * @return Number of records.
	 */
	public final Integer getStashedRecordCount() {
		if (stashedRecords != null) {
			return stashedRecords.size();
		} else {
			return 0;
		}
	}

	/**
	 * Simple Saving provides a framework for storing data stream data as JSON
	 * objects, without any advanced query or update capabilities.<br/>
	 * <br/>
	 * 
	 * Set this boolean to indicate if the data stream uses the Simple Storage
	 * mechanism in this application. If it does, it will create a database and
	 * store data records in it.
	 */
	public abstract boolean usesSimpleSave();

	/**
	 * Saves the stashed records to a database.<br/>
	 * <br/>
	 * 
	 * OVERRIDE this method if you want custom, advanced storage for your data
	 * stream so it can be queried by fields.
	 */
	public void saveStashedRecords() throws Exception {
		if (this.usesSimpleSave()) {
			DsSimpleSaveDB db = new DsSimpleSaveDB(mContext);
			db.open();
			db.saveRecords(this.getStreamId(), this.getSchemaVersion(), this.stashedRecords);
			db.close();
		} else {
			// TODO throw exception, they must override this method if not using
			// simple save
		}
	}

	/**
	 * Saves the stashed records using {@link #saveStashedRecords()}, and
	 * additionally clears the list of stashed records afterwards.
	 */
	public final void saveStashedRecordsAndFlush() throws Exception {
		this.saveStashedRecords();
		stashedRecords.clear();
	}
}
