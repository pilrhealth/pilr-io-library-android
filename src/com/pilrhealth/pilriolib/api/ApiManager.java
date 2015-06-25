package com.pilrhealth.pilriolib.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.pilrhealth.pilriolib.Epoch;
import com.pilrhealth.pilriolib.InstrumentConfig;
import com.pilrhealth.pilriolib.InstrumentSettings;
import com.pilrhealth.pilriolib.Period;
import com.pilrhealth.pilriolib.Project;
import com.pilrhealth.pilriolib.data.Dataset;
import com.pilrhealth.pilriolib.data.DatasetRecord;
import com.pilrhealth.pilriolib.data.DsSimpleSaveDB;
import com.pilrhealth.pilriolib.logs.AppLog;
import com.pilrhealth.pilriolib.util.AsyncTaskCallback;
import com.pilrhealth.pilriolib.util.ISODateHelper;

public class ApiManager {

	private String mAuthorization = "Authorization", mBearer = "Bearer";
	private boolean mHeader = false;
	private Context mContext;
	private List<Dataset> mStreams;
	static InputStream mIs = null;
	private String mKey, mUrl, mResult;
	public boolean uploadSuccess = false;
	/*
	 * Possible values for Map: 
	 * <"status", "logged"> 
	 * <"status", "notLogged">
	 * <"status", "uploadUnauthorized"> 
	 * <"status", "uploadResponseError">
	 * <"status", "retrieveUnauthorized"> 
	 * <"status", "retrieveResponseError"> 
	 * <"action", "uploadFailed"> 
	 * <"action", "login"> 
	 * <"action", "partInfo"> 
	 * <"action", "upload"> 
	 * <"action", "retrievePeriods"> 
	 * <"action", "retrieveInstrumentSettings">
	 */
	public static Map<Object, Object> sMap = new HashMap<Object, Object>();
	// JSON Node names
	private static final String TAG_KEY_PARTICIPANT = "pt";
	private static final String TAG_TIMESTAMP = "timestamp";
	private static final String TAG_UUID = "id";
	private static final String TAG_METADATA = "metadata";
	private static final String TAG_DATA = "data";
	private static final String TAG_URI = "info_uri";
	private static final String TAG_ACCESS_CODE = "access_code";
	private static final String TAG_PARTICIPANT = "code";
	private static final String TAG_PROJECT = "project";
	private static final String TAG_PROJECT_CODE = "code";
	private static final String TAG_PROJECT_NAME = "name";
	private static final String TAG_RESULT = "success";
	private static final String TAG_EPOCHS = "epochs";
	private static final String TAG_START_DT = "start_date";
	private static final String TAG_VALUE = "value";

	public ApiManager(Context ctx) {
		mContext = ctx;
	}

	public ApiManager(Context ctx, String key, String url, boolean header) {
		mContext = ctx;
		mKey = key;
		mUrl = url;
		mHeader = header;
	}
	
	public void startUploadInAsyncTask(boolean deleteAfterUpload, JSONObject json, List<DatasetRecord> records, AsyncTaskCallback callback, List<Dataset> streams) {
		mStreams = streams;
		UploadAsyncTask task = new UploadAsyncTask(callback, json, records);
		task.execute(deleteAfterUpload);
	}
	
	public void startUploadInAsyncTask(boolean deleteAfterUpload, JSONObject json, List<DatasetRecord> records, AsyncTaskCallback callback) {
		UploadAsyncTask task = new UploadAsyncTask(callback, json, records);
		task.execute(deleteAfterUpload);
	}

	public void startUploadInAsyncTask(boolean deleteAfterUpload, JSONObject json, List<DatasetRecord> records) {
		startUploadInAsyncTask(true, json, records, null);
	}

	public void startUpload(boolean deleteAfterUpload, JSONObject json, List<DatasetRecord> dSRecords) {
		if (dSRecords == null) {
			uploadSuccess = false;
			// Upload Queue
			DsSimpleSaveDB ssDB = new DsSimpleSaveDB(mContext);

			ListIterator<Dataset> lit = mStreams.listIterator();
			while (lit.hasNext()) {
				Dataset stream = lit.next();
				if (stream.usesSimpleSave()) {
					ssDB.open();
					Cursor c = ssDB.getCursor(stream.getStreamId(), stream.getSchemaVersion(), false);
					if (c.getCount() != 0) {
						int batches = (c.getCount() / getBatchUploadMaxCount()) + 1;
						List<String> uploadedRecordUuids = new ArrayList<String>();
						for (int i = 1; i <= batches; i++) {
							// Build data array
							JSONArray records = new JSONArray();
							while (c.moveToNext()) {
								try {
									JSONObject metadata = new JSONObject();
									metadata.put(TAG_KEY_PARTICIPANT, c.getString(c.getColumnIndex(DsSimpleSaveDB.KEY_PARTICIPANT_ID)));
									metadata.put(TAG_TIMESTAMP, ISODateHelper.toString(new Date(c.getLong(c.getColumnIndex(DsSimpleSaveDB.KEY_TIME_CREATED)))));
									metadata.put(TAG_UUID, c.getString(c.getColumnIndex(DsSimpleSaveDB.KEY_UUID)));

									JSONObject data = new JSONObject(c.getString(c.getColumnIndex(DsSimpleSaveDB.KEY_RECORD)));

									JSONObject obj = new JSONObject();
									obj.put(TAG_METADATA, metadata);
									obj.put(TAG_DATA, data);
									records.put(obj);

									uploadedRecordUuids.add(c.getString(c.getColumnIndex(DsSimpleSaveDB.KEY_UUID)));
								} catch (JSONException ex) {
									Log.d("ApiMAnager", "JSONException Queue: " + ex);
								}
							}
							// Upload data
							uploadData(stream, records);
							if ((deleteAfterUpload) && (uploadSuccess)) {
								ssDB.deleteRecords(uploadedRecordUuids);
								uploadedRecordUuids.clear();
							}
						}
					} else {
						mapManager(sMap, "action", "uploadFailed");
						Log.d("ApiManager", "No data to be uploaded");
					}
					ssDB.close();
				}
			}
		} else {
			// Upload last location
			// Build request
			ListIterator<DatasetRecord> lit = dSRecords.listIterator();
			DatasetRecord record = lit.next();
			JSONArray jsRecords = new JSONArray();
			try {
				JSONObject metadata = new JSONObject();
				metadata.put(TAG_KEY_PARTICIPANT, record.getParticipant());
				metadata.put(TAG_TIMESTAMP, ISODateHelper.toString(new Date(record.getDateCreated().getTime())));
				metadata.put(TAG_UUID, record.getUUID());

				JSONObject obj = new JSONObject();
				obj.put(TAG_METADATA, metadata);
				obj.put(TAG_DATA, json);
				jsRecords.put(obj);
			} catch (JSONException ex) {
				Log.d("ApiMAnager", "JSONException Single: " + ex);
			}
			// upload data
			ListIterator<Dataset> litDS = mStreams.listIterator();
			Dataset stream = litDS.next();
			uploadData(stream, jsRecords);
		}
	}

	public void uploadData(Dataset stream, JSONArray records) {
		String url = AuthCredentials.getUrl() + "/api/" + AuthCredentials.getApiVersion() + "/" + Project.getProjectId() + "/instrument/"
				+ InstrumentConfig.getName() + "/participant/" + AuthCredentials.getParticipantId() + "/dataset/" + stream.getStreamId() + "/"
				+ stream.getSchemaVersion() + "/data";
		// Call Post Method
		postData(url, records);

		if (mResult != null) {
			mapManager(sMap, "action", "upload");
			if (mResult.equals("Unauthorized\n")) {
				Log.d("ApiManager", "Upload Unauthorized");
				AppLog.getLog(mContext).e("ApiMAnager", "Upload Unauthorized", null);
				mapManager(sMap, "status", "uploadUnauthorized");
			} else {
				if ((mResult.contains("html")) || (mResult.contains("error"))) {
					Log.d("ApiManager", "Upload Response Error");
					AppLog.getLog(mContext).e("ApiMAnager", "Upload Response Error", null);
					mapManager(sMap, "status", "uploadResponseError");
				} else {
					JSONObject jsonResponse = null;
					try {
						jsonResponse = new JSONObject(mResult);
					} catch (JSONException e) {
						e.printStackTrace();
						Log.d("ApiManager", "JSONException Upload:" + e);
						AppLog.getLog(mContext).e("ApiMAnager", "JSONException Upload:", e);
					}
					if (jsonResponse.has(TAG_RESULT)) {
						String success = (String) jsonResponse.opt(TAG_RESULT);
						if (success.equals("Saved")) {
							uploadSuccess = true;
//							mapManager(sMap, "action", "upload");
						}
					}
				}
			}
		} else {
			Log.d("ApiManager", "JsonUpload empty");
			AppLog.getLog(mContext).e("ApiMAnager", "JsonUpload empty", null);
		}
	}

	public void startRetrieveInAsyncTask(boolean deleteAfterUpload, AsyncTaskCallback callback) {
		RetrieveAsyncTask task = new RetrieveAsyncTask(callback);
		task.execute(deleteAfterUpload);
	}

	public void startRetrieveInAsyncTask(boolean deleteAfterUpload) {
		startRetrieveInAsyncTask(true, null);
	}

	public void startRetrieve(boolean deleteAfterUpload) {
		String url = mUrl + mKey;
		// Get data
		getData(url);

		if (mResult != null) {
			if (mResult.equals("Unauthorized\n")) {
				Log.d("ApiManager", "Retrieve Unauthorized");
				AppLog.getLog(mContext).e("ApiMAnager", "Retrieve Unauthorized", null);
				mapManager(sMap, "status", "retrieveUnauthorized");
			} else {
				if (mResult.contains("html")) {
					Log.d("ApiManager", "Retrieve Response Error");
					AppLog.getLog(mContext).e("ApiMAnager", "Retrieve Response Error", null);
					mapManager(sMap, "status", "retrieveResponseError");
				} else {
					Log.d("ApiManager", "JsonRetrieve:" + mResult);
					try {
						Object json = new JSONTokener(mResult).nextValue();
						if (json instanceof JSONObject) {
							JSONObject jsonResponse = new JSONObject(mResult);
							// Get access code and uri
							if (jsonResponse.has(TAG_ACCESS_CODE)) {
								if (jsonResponse.has(TAG_URI) )
									AuthCredentials.setUri(jsonResponse.getString(TAG_URI));
								AuthCredentials.setAccessCode(jsonResponse.getString(TAG_ACCESS_CODE));
								mapManager(sMap, "status", "logged");
								mapManager(sMap, "action", "login");
							}
							// Get participant information
							if (jsonResponse.has(TAG_PROJECT)) {
								JSONObject project = jsonResponse.getJSONObject(TAG_PROJECT);
								Project.setProjectId(project.getString(TAG_PROJECT_CODE));
								Project.setProjectName(project.getString(TAG_PROJECT_NAME));
								AuthCredentials.setParticipantId(jsonResponse.getString(TAG_PARTICIPANT));
								mapManager(sMap, "action", "partInfo");
								mapManager(sMap, "status", "logged");
							}
						}
						if (json instanceof JSONArray) {
							JSONArray jsonResponse = new JSONArray(mResult);
							// Get period information
							if (jsonResponse.toString().contains(TAG_START_DT)) {
								List<Period> periods = new ArrayList<Period>();
								for (int i = 0; i < jsonResponse.length(); i++) {
									JSONObject object = jsonResponse.getJSONObject(i);
									if (object.has(TAG_EPOCHS)) {
										Period period = new Period();
										period.fromJSONObject(object);
										periods.add(period);

										List<Epoch> epochs = new ArrayList<Epoch>();
										JSONArray epochArray = object.getJSONArray(TAG_EPOCHS);
										for (int j = 0; j < epochArray.length(); j++) {
											Epoch epoch = new Epoch();
											JSONObject epochObj = epochArray.getJSONObject(j);
											epoch.fromJSONObject(epochObj);
											epochs.add(epoch);
										}
										period.setEpochs(epochs);
									}
								}
								Project.setPeriods(periods);
								mapManager(sMap, "action", "retrievePeriods");
							}
							// Get instrument setting information
							if (jsonResponse.toString().contains(TAG_VALUE)) {
								List<InstrumentSettings> listISettings = new ArrayList<InstrumentSettings>();
								for (int i = 0; i < jsonResponse.length(); i++) {
									JSONObject object = jsonResponse.getJSONObject(i);
									InstrumentSettings setting = new InstrumentSettings();
									setting.fromJSONObject(object);
									listISettings.add(setting);
								}
								InstrumentConfig.setInstrumentSettings(listISettings);
								mapManager(sMap, "action", "retrieveInstrumentSettings");
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						Log.d("ApiManager", "Exception StartRetrieve/JSonResponse:" + ex);
						AppLog.getLog(mContext).e("ApiMAnager", "Exception StartRetrieve/JSonResponse:", ex);
					}
				}
			}
		} else {
			if (mKey != "") {
				mapManager(sMap, "status", "notLogged");
				mapManager(sMap, "action", "login");
			} 
			Log.d("ApiManager", "JsonUpload empty");
			AppLog.getLog(mContext).e("ApiMAnager", "JsonUpload empty", null);
		}
	}

	private Integer getBatchUploadMaxCount() {
		return 50; // hardcoded for now
	}

	private void mapManager(Map<Object, Object> map, String key, String value) {
		if (map.containsKey(key)) {
			map.remove(key);
			map.put(key, value);
		} else {
			map.put(key, value);
		}
	}

	private String postData(String url, JSONArray json) {
		try {
			HttpClient mHttpClient = new DefaultHttpClient();
			HttpContext mHttpContext = new BasicHttpContext();
			HttpPost post = new HttpPost(url);
			post.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
			post.setHeader(mAuthorization, mBearer + " " + AuthCredentials.getAccessCode());

			HttpResponse response = mHttpClient.execute(post, mHttpContext);
			mIs = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(mIs, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			mIs.close();
			mResult = sb.toString();
			Log.d("ApiManager", "Upload result:" + mResult);
			if (mResult.length() == 0) {
				mResult = null;
			}
		} catch (UnsupportedEncodingException e) {
			Log.d("ApiMAnager", "UnsupportedEncodingException: " + e);
			AppLog.getLog(mContext).e("ApiMAnager", "UnsupportedEncodingException: ", e);
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.d("ApiMAnager", "ClientProtocolException: " + e);
			AppLog.getLog(mContext).e("ApiMAnager", "ClientProtocolException: ", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("ApiMAnager", "IOException: " + e);
			AppLog.getLog(mContext).e("ApiMAnager", "IOException: ", e);
			e.printStackTrace();
		}
		return mResult;
	}

	private String getData(String url) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext httpContext = new BasicHttpContext();
			HttpGet get = new HttpGet(url);
			if (mHeader) {
				get.setHeader(mAuthorization, mBearer + " " + AuthCredentials.getAccessCode());
			}

			HttpResponse response = httpClient.execute(get, httpContext);
			mIs = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(mIs, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			mIs.close();
			mResult = sb.toString();
			if (mResult.length() == 0) {
				mResult = null;
			}
		} catch (Exception e) {
			mResult = null;
			Log.d("ApiMAnager", "GetData Exception" + e);
			AppLog.getLog(mContext).e("ApiMAnager", "GetData Exception:", e);
		}
		return mResult;
	}

	private class UploadAsyncTask extends AsyncTask<Boolean, Void, Void> {

		private AsyncTaskCallback listener;
		private JSONObject jsonObject;
		private List<DatasetRecord> listDS;

		public UploadAsyncTask(AsyncTaskCallback callback, JSONObject json, List<DatasetRecord> records) {
			listener = callback;
			jsonObject = json;
			listDS = records;
		}

		@Override
		protected void onPreExecute() {
			if (listener != null) {
				listener.onStart();
			}
		}

		@Override
		protected Void doInBackground(Boolean... delete) {
			startUpload(true, jsonObject, listDS);
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
			if (listener != null) {
				listener.onFinish(sMap);
			}
		}

	}

	private class RetrieveAsyncTask extends AsyncTask<Boolean, Void, Void> {

		private AsyncTaskCallback listener;

		public RetrieveAsyncTask(AsyncTaskCallback callback) {
			listener = callback;
		}

		@Override
		protected void onPreExecute() {
			if (listener != null) {
				listener.onStart();
			}
		}

		@Override
		protected Void doInBackground(Boolean... delete) {
			startRetrieve(delete[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
			if (listener != null) {
				listener.onFinish(sMap);
			}
		}
	}
}
