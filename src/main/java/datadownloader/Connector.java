package datadownloader;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@Slf4j
class Connector {
    /**
     * Connects to last fm and returns the reponse in JSON format
     * @param page The page for the request
     * @param from Unix timestamp from which tracks are read
     * @return
     */
    String getRawDataFromServer(int page, long from) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://ws.audioscrobbler.com/2.0/";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("method", "user.getrecenttracks");
        httpBuilder.addQueryParameter("user", Config.USERNAME);
        httpBuilder.addQueryParameter("api_key", Config.API_KEY);
        httpBuilder.addQueryParameter("format", "json");
        httpBuilder.addQueryParameter("limit", "1000");
        if (page != -1) {
            httpBuilder.addQueryParameter("page", String.valueOf(page));
        }
        if (from > 0) {
            httpBuilder.addQueryParameter("from", String.valueOf(from));
        }

        HttpUrl urlWithParams = httpBuilder.build();
        log.info("Sending request to Last.fm:");
        log.info(urlWithParams.toString().replace(Config.API_KEY, "*****"));
        Request request = new Request.Builder().url(urlWithParams)
                .get()
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Host", "ws.audioscrobbler.com")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
