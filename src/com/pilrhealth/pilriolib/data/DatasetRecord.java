package com.pilrhealth.pilriolib.data;

import java.util.Date;
import java.util.UUID;

import com.pilrhealth.pilriolib.util.JSONable;

public abstract class DatasetRecord implements JSONable {
	private String mUuid;
	private String mParticipant;
	private Date mDateCreated;


	public DatasetRecord() {
		mUuid = UUID.randomUUID().toString();
	}

	public final String getUUID() {
		return mUuid;
	}

	public final void setUUID(String uuid) {
		this.mUuid = uuid;
	}

	public void setParticipant(String pt) {
		this.mParticipant = pt;
	}

	public String getParticipant() {
		return mParticipant;
	}

	public void setDateCreated(Date date) {
		this.mDateCreated = date;
	}

	public Date getDateCreated() {
		return mDateCreated;
	}
}
