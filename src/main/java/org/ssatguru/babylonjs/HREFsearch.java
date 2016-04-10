package org.ssatguru.babylonjs;

import static jsweet.dom.Globals.alert;
import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.window;

import jsweet.dom.Location;
import jsweet.lang.Array;

public class HREFsearch {
	
	Array<String> names = new Array<String>();
	Array<String> values = new Array<String>();
	
	public HREFsearch() {

		String search = window.location.search;
		
		// drop the "?" in the begining
		search = search.substring(1);
		String[] parms = search.split("&");
		for (String parm : parms) {
			String[] nameValues = parm.split("=");
			if (nameValues.length == 2) {
				String name = nameValues[0];
				String value = nameValues[1];
				names.push(name);
				values.push(value);
			}
		}
	}
	
	public String getParm(String parm){
		double i = names.indexOf(parm);
		if (i != -1){
			return (String) values.$get(i);
		}
		return null;
	}
	
	

}
