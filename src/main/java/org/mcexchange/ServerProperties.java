package org.mcexchange;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A special version of Properties that has a private "override" Properties object that has
 * all of the properties in this object + all of the overwritten properties (specified in the
 * constructor via an array of strings). The advantage of this is that an overwritten property
 * is not saved, so it allows for one-time arguments, while still supporting dynamic changes
 * since setProperty() sets a saved property.
 * 
 * @author Pamelloes
 */
public class ServerProperties extends Properties {
	private static final long serialVersionUID = -4988854801923077184L;
	@SuppressWarnings("serial")
	private static final Properties defaults = new Properties() {{
		//set all default properties here.
	}};
	private final Properties override;
	
	public ServerProperties(String[] override) {
		super(defaults);
		File f = new File("exchange.yml");
		if(f.exists()) {
			try {
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				load(is);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
				this.store(os, "");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		this.override = new Properties(this);
		
		for(String s : override) {
			int divide = s.indexOf("=");
			if(divide<0) return;
			String key = s.substring(0,divide);
			String value = s.substring(divide+1);
			this.override.setProperty(key, value);
		}
	}
	
	@Override
	public Object put(Object key, Object value) {
		super.put(key, value);
		return override.put(key, value);
	}
	
	@Override
	public String getProperty(String key) {
		return override.getProperty(key);
	}
}