package com.vir2alwhale.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseAnalytics;

public class RibbitApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		  Parse.initialize(this, "bFMrEz6O2WEoxdanzpYK5YG92NIfhaPlrITMPMjo", "9un2SNN6zjWrZ3rm63sYfVzidpSLHMREdRMKqb4B");
		  
		}

}
