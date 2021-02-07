package com.spket.demo.draw2d;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.skija.Data;
import org.jetbrains.skija.svg.DOM;

public class Images {
	public static final String ANALYTICS_LAPTOP = "analytics-laptop-svgrepo-com.svg";
	public static final String FOLDER = "folder-svgrepo-com.svg";
	public static final String GPS = "gps-geolocalization-svgrepo-com.svg";
	public static final String INTERNET_SHIELD = "internet-shield-svgrepo-com.svg";
	public static final String INTERNET = "internet-svgrepo-com.svg";
	public static final String IPAD = "ipad-svgrepo-com.svg";
	public static final String SEARCH = "search-svgrepo-com.svg";
	public static final String SETTINGS_GEAR = "settings-gear-svgrepo-com.svg";
	//public static final String STRATEGY = "strategy-svgrepo-com.svg";
	
	private static Map<String, DOM> images = new HashMap<>();
	
	public static void load() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			load(ANALYTICS_LAPTOP, out);
			load(FOLDER, out);
			load(GPS, out);
			load(INTERNET_SHIELD, out);
			load(INTERNET, out);
			load(IPAD, out);
			load(SEARCH, out);
			load(SETTINGS_GEAR, out);
			//load(STRATEGY, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
				
	private static void load(String name, ByteArrayOutputStream out) throws IOException {
		DOM dom = null;
		try (InputStream in = Images.class.getResourceAsStream("/images/" + name)) {
			out.reset();
			in.transferTo(out);
			try (Data data = Data.makeFromBytes(out.toByteArray())) {
				dom = new DOM(data);
				dom.setContainerSize(64, 64);
			}
		}
		
		if (dom != null)
			images.put(name, dom);
	}
	
	public static void dispose() {
		for (DOM dom : images.values()) {
			dom.close();
		}
		images.clear();
	}
	
	public static DOM getImage(String name) {
		return images.get(name);
	}
}
