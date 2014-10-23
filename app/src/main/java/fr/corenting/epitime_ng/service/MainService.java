package fr.corenting.epitime_ng.service;

import android.app.IntentService;
import android.content.Intent;

class MainService extends IntentService {

	public MainService() {
		super("EpiTimeService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	}

}
