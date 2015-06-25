package com.pilrhealth.pilriolib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

	/**
	 * Instrument configuration specification
	 * 
	 * For every app that uses the pilrcorelib, it must specify configuration
	 * information for the Instrument that it corresponds to on the PILR server.
	 * This class is used by the library components to retrieve that
	 * information.
	 * 
	 * Each method below MUST BE UPDATED specific to the application.
	 * 
	 */
public class InstrumentConfig {

	private static String sInstrumentName;
	public static List<InstrumentSettings> sInstrumentSettings = null;

	public InstrumentConfig(Context ctx) {
		sInstrumentSettings = new ArrayList<InstrumentSettings>();
	}

	public static String getName() {
		return sInstrumentName;
	}
	
	public static void setName(String name) {
		sInstrumentName = name;
	}
	
	public static void setInstrumentSettings(List<InstrumentSettings> iSettings) {
		sInstrumentSettings = iSettings;
	}

	public static List<InstrumentSettings> getInstrumentSettings() {
		return sInstrumentSettings;
	}
}
