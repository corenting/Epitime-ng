package fr.corenting.epitime_ng.utils;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

public class UrlUtils {
	
	private static String formatSpecialUrlChars(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) { //TODO log the error
			return url;
		}		
	}
	
	
	public static String makeLectureStringUrl(int week, String group) {
		String url = EpiTime.getInstance().getResources().getString(R.string.api_url);
		String key = EpiTime.getInstance().getResources().getString(R.string.api_key);
		
		url = MessageFormat.format(url, key, formatSpecialUrlChars(group), week, 1);
		return url;
	}

	public static String makeGroupStringUrl(String group) {
		String url = EpiTime.getInstance().getResources().getString(R.string.api_groups);
		String key = EpiTime.getInstance().getResources().getString(R.string.api_key);
		
		return MessageFormat.format(url, key, formatSpecialUrlChars(group));
	}

}
