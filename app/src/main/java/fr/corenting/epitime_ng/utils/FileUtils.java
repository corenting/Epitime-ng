package fr.corenting.epitime_ng.utils;

import android.content.Context;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.tasks.CacheLecturesTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static boolean deleteAllFiles() {
        try {
            Context curr = EpiTime.getInstance().getCurrentActivity();
            File dir = curr.getFilesDir();
            File[] subFiles = dir.listFiles();
            Boolean ret = true;

            if (subFiles != null && subFiles.length > 0) {
                for (File file : subFiles) {
                    if (!file.delete()) {
                        ret = false;
                    }
                }
            }
            return ret;
        } catch (Exception e) {
            return false;
        }
    }
	
	//Returns an InputStream to the file. Can be null if file isn't available
	public static InputStream getFromFile(String filename) throws IOException {
        Context curr = EpiTime.getInstance().getCurrentActivity();
		InputStream inputStream = curr.openFileInput(filename);
		
		if(inputStream == null || inputStream.available() == 0) { return null; }
		
		return inputStream;
		
	}
	
	public static InputStream save(InputStream is, String filename) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		 byte[] buffer = new byte[1024]; int len;
		 while ((len = is.read(buffer)) > -1 ) { baos.write(buffer, 0, len); }
		 baos.flush();

		 InputStream out  = new ByteArrayInputStream(baos.toByteArray());
		 InputStream file = new ByteArrayInputStream(baos.toByteArray());
		 
		 new CacheLecturesTask().execute(filename, file);
		 
		 return out;
	}
	
	public static String makeLecturesFilename(int week, String group) {
		return "SchedulesFor-" + sanitize(group) + "-AtWeek-" + week + ".xml";
	}
	
	private static final String ReservedChars = "|\\?*<\":>+[]/'";
	
	private static String sanitize(String s) {
		
		for(int i = 0; i < ReservedChars.length(); ++i) {
			s = s.replace(ReservedChars.charAt(i), '-');
		}
		s = s.replace(' ', '+');
		
		return s;
	}
}
