package fr.corenting.epitime_ng.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetUtils {

    public static InputStream getFromInternet(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        connection.connect();

        if (connection.getResponseCode() == -1) {
            throw new IOException("Could not fetch the lectures (invalid response code).");
        }

        return connection.getInputStream();
    }
}
