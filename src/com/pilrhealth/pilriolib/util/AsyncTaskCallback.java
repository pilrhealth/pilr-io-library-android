package com.pilrhealth.pilriolib.util;

import java.util.Map;

public interface AsyncTaskCallback {
	public void onStart();

	public void onUpdate(Map<?, ?> something);

	public void onFinish(Map<?, ?> something);
}
