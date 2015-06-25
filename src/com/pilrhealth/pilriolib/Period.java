package com.pilrhealth.pilriolib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.pilrhealth.pilriolib.util.JSONable;

public class Period implements JSONable {
	private String mPeriodName, mPeriodCode;
	private List<Epoch> mEpochs;
	// JSON Node names
	private static final String TAG_PERIOD_NAME = "name";
	private static final String TAG_PERIOD_CODE = "code";

	public Period() {
		super();
		mEpochs = new ArrayList<Epoch>();
	}

	public void setPeriodName(String period) {
		mPeriodName = period;
	}

	public String getPeriodName() {
		return mPeriodName;
	}

	public void setPeriodCode(String code) {
		mPeriodCode = code;
	}

	public String getPeriodCode() {
		return mPeriodCode;
	}

	public void setEpochs(List<Epoch> epoch) {
		mEpochs = epoch;
	}

	public List<Epoch> getEpoch() {
		return mEpochs;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		return null;
	}

	/*
	 * (non-Javadoc) Convert a JSONObject in Period object
	 * 
	 * @see
	 * com.pilrhealth.android.pilrcorelib.util.JSONable#fromJSONObject(org.json
	 * .JSONObject)
	 */
	@Override
	public void fromJSONObject(JSONObject src) throws JSONException {
		setPeriodName(src.getString(TAG_PERIOD_NAME));
		setPeriodCode(src.getString(TAG_PERIOD_CODE));
	}
}