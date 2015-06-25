package com.pilrhealth.pilriolib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.pilrhealth.pilriolib.util.JSONable;

public class Project implements JSONable {
	public static String sProjectId, sProjectName;
	public static List<Period> sPeriods = null;

	public Project() {
		super();
		sPeriods = new ArrayList<Period>();
	}

	public static String getProjectId() {
		return sProjectId;
	}

	public static void setProjectId(String projectId) {
		sProjectId = projectId;
	}

	public static String getProjectName() {
		return sProjectName;
	}

	public static void setProjectName(String projectName) {
		sProjectName = projectName;
	}

	public static void setPeriods(List<Period> period) {
		sPeriods = period;
	}

	public static List<Period> getPeriods() {
		return sPeriods;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		return null;
	}

	@Override
	public void fromJSONObject(JSONObject src) throws JSONException {

	}

}
