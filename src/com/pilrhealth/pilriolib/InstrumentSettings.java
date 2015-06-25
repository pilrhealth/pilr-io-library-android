package com.pilrhealth.pilriolib;

import org.json.JSONException;
import org.json.JSONObject;

import com.pilrhealth.pilriolib.util.JSONable;

/**
 * Instrument Settings will be created using as parameter the instrument
 * configured at InstrumentConfig class. Settings for other instruments than the
 * one configured at InstrumentConfig will be not considered.
 */
public class InstrumentSettings implements JSONable {
	private String mSettingName, mSettingCode, mPeriodCode, mEpochCode, mSettingDesc, mSettingType, mSettingValue;
	// JSON Node names
	private static final String TAG_SETTING_NAME = "name";
	private static final String TAG_SETTING_CODE = "code";
	private static final String TAG_PERIOD_CODE = "period_code";
	private static final String TAG_EPOCH_CODE = "epoch_code";
	private static final String TAG_SETTING_DESC = "description";
	private static final String TAG_SETTING_TYPE = "type";
	private static final String TAG_SETTING_VALUE = "value";

	public InstrumentSettings() {
		super();
	}

	public void setSettingName(String name) {
		mSettingName = name;
	}

	public String getSettingName() {
		return mSettingName;
	}

	public void setSettingCode(String setCode) {
		mSettingCode = setCode;
	}

	public String getSettingCode() {
		return mSettingCode;
	}

	public void setPeriodCode(String periodCode) {
		mPeriodCode = periodCode;
	}

	public String getPeriodCode() {
		return mPeriodCode;
	}

	public void setEpochCode(String epochCode) {
		mEpochCode = epochCode;
	}

	public String getEpochCode() {
		return mEpochCode;
	}

	public void setSettingDesc(String setDesc) {
		mSettingDesc = setDesc;
	}

	public String getSettingDesc() {
		return mSettingDesc;
	}

	public void setSettingType(String setType) {
		mSettingType = setType;
	}

	public String getSettingType() {
		return mSettingType;
	}

	public void setSettingValue(String setValue) {
		mSettingValue = setValue;
	}

	public String getSettingValue() {
		return mSettingValue;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc) Convert a JSONObject in InstrumentSettings object
	 * 
	 * @see
	 * com.pilrhealth.android.pilrcorelib.util.JSONable#fromJSONObject(org.json
	 * .JSONObject)
	 */
	@Override
	public void fromJSONObject(JSONObject src) throws JSONException {
		setSettingName(src.getString(TAG_SETTING_NAME));
		setSettingCode(src.getString(TAG_SETTING_CODE));
		setPeriodCode(src.getString(TAG_PERIOD_CODE));
		setEpochCode(src.getString(TAG_EPOCH_CODE));
		setSettingDesc(src.getString(TAG_SETTING_DESC));
		setSettingType(src.getString(TAG_SETTING_TYPE));
		setSettingValue(src.getString(TAG_SETTING_VALUE));
	}
}
