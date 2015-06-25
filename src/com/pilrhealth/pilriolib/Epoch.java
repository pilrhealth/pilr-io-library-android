package com.pilrhealth.pilriolib;

import org.json.JSONException;
import org.json.JSONObject;

import com.pilrhealth.pilriolib.util.JSONable;

public class Epoch implements JSONable {
	private String mEpochName, mEpochCode, mStartDate, mEndDate;
	// JSON Node names
	private static final String TAG_ST_DT = "start_date";
	private static final String TAG_EPOCH_NAME = "name";
	private static final String TAG_EPOCH_CODE = "code";
	private static final String TAG_END_DT = "end_date";

	public Epoch() {
		super();
	}

	public void setEpochName(String name) {
		mEpochName = name;
	}

	public String getEpochName() {
		return mEpochName;
	}

	public void setEpochCode(String code) {
		mEpochCode = code;
	}

	public String getEpochCode() {
		return mEpochCode;
	}

	public void setEpochStartDate(String date) {
		mStartDate = date;
	}

	public String getEpochStartDate() {
		return mStartDate;
	}

	public void setEpochEndDate(String date) {
		mEndDate = date;
	}

	public String getEpochEndDate() {
		return mEndDate;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc) Convert a JSONObject in Epoch object
	 * 
	 * @see
	 * com.pilrhealth.android.pilrcorelib.util.JSONable#fromJSONObject(org.json
	 * .JSONObject)
	 */
	@Override
	public void fromJSONObject(JSONObject src) throws JSONException {
		setEpochName(src.getString(TAG_EPOCH_NAME));
		setEpochCode(src.getString(TAG_EPOCH_CODE));
		setEpochStartDate(src.getString(TAG_ST_DT));
		setEpochEndDate(src.getString(TAG_END_DT));
	}
}
