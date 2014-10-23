package fr.corenting.epitime_ng.tasks;

import android.content.Context;
import android.os.AsyncTask;

import fr.corenting.epitime_ng.EpiTime;

import java.io.FileOutputStream;
import java.io.InputStream;

public class CacheLecturesTask extends AsyncTask<Object, Void, Boolean> {

	@Override
	protected Boolean doInBackground(Object... params) {
		String filename   = (String)      params[0];
		InputStream input = (InputStream) params[1];

		int read;
		byte[] bytes = new byte[1024];

		FileOutputStream outputStream;
		try {
            Context curr = EpiTime.getInstance().getCurrentActivity();
			outputStream = curr.openFileOutput(filename, Context.MODE_PRIVATE);
			 
			while ((read = input.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			outputStream.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}