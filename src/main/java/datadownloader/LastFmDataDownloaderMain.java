package datadownloader;

import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LastFmDataDownloaderMain {

    public static void main(String[] args) {
        LastFmDataDownloaderMain downloader = new LastFmDataDownloaderMain();
        List<Track> tracks = new ArrayList<>();
        int currentPage = 1;
        long newestTimestamp = downloader.getNewestTimestamp() + 1; //+1: Don't involve newestTimestamp itself
        int totalDatasets = -1;
        do {
            String rawDataFromServer = downloader.getRawDataFromServer(currentPage, newestTimestamp);
//            String rawDataFromServer = loadFromFile();
            JSONObject obj = new JSONObject(rawDataFromServer);
            List<Track> pageTracks = downloader.getTracks(obj);
            tracks.addAll(pageTracks);
            DataInfo dataInfo = downloader.getDataInfo(obj);
            currentPage = downloader.nextPage(dataInfo);
            totalDatasets = dataInfo.getTotalDatasets();
            System.out.println(currentPage);
        } while (currentPage != -1);
        downloader.sortTracks(tracks);
        int numExtractedTracks = tracks.size();
        if (totalDatasets != numExtractedTracks) {
            throw new RuntimeException("It seems like not all datasets have been scraped: \n" +
                    "Total Datasets: " + totalDatasets + "\n" +
                    "Extracted Datasets: " + numExtractedTracks);
        }
        downloader.writeToCsvFile(tracks);
    }

    private long getNewestTimestamp() {
        String csvFileContent = getLastLineFromCsvFile();
        if (csvFileContent != null) {
            String[] fields = csvFileContent.split(Config.CSV_DELIMITER);
            long newestTimestamp = Long.valueOf(fields[3]);
            return newestTimestamp;
        }
        return 0;
    }

    private DataInfo getDataInfo(JSONObject obj) {
        JSONObject attr = obj.getJSONObject("recenttracks").getJSONObject("@attr");
        int currentPage = attr.getInt("page");
        int totalPages = attr.getInt("totalPages");
        int totalDatasets = attr.getInt("total");
        DataInfo dataInfo = new DataInfo(currentPage, totalPages, totalDatasets);
        return dataInfo;
    }


    private int nextPage(DataInfo dataInfo) {
        int currentPage = dataInfo.getCurrentPage();
        int totalPages = dataInfo.getTotalPages();
        if (currentPage == totalPages) {
            return -1;
        } else {
            return currentPage + 1;
        }
    }

    private void writeToCsvFile(List<Track> tracks) {
        StringBuilder sb = new StringBuilder();
        int trackSize = tracks.size();
        for (int i = 0; i < trackSize; i++) {
            Track track = tracks.get(i);
            sb.append(track.toCsv()).append("\n");
        }
        saveToFile(sb.toString(), Config.SAVE_FILE, true);
    }

    private String getRawDataFromServer(int page, long from) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://ws.audioscrobbler.com/2.0/";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("method", "user.getrecenttracks");
        httpBuilder.addQueryParameter("user", Config.USERNAME);
        httpBuilder.addQueryParameter("api_key", Config.API_KEY);
        httpBuilder.addQueryParameter("format", "json");
        httpBuilder.addQueryParameter("limit", "10");
        if (page != -1) {
            httpBuilder.addQueryParameter("page", String.valueOf(page));
        }
        if (from > 0) {
            httpBuilder.addQueryParameter("from", String.valueOf(from));
        }

        Request request = new Request.Builder().url(httpBuilder.build())
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
            String responseString = response.body().string();
            long currentTimeMs = System.currentTimeMillis();
            String rawDataFilePath = Config.BACKUP_FOLDER + "/" + Config.RAW_DATA + "/" + currentTimeMs + ".json";
            saveToFile(responseString, rawDataFilePath, false);
            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveToFile(String text) {
        saveToFile(text, null, false);
    }

    private void saveToFile(String text, String filename, boolean append) {
        if (filename == null) {
            filename = Config.SAVE_FILE;
        }
        try {
            FileUtils.writeStringToFile(new File(filename), text, "UTF-8", append);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLastLineFromCsvFile() {
        try {
            File csvFile = new File(Config.SAVE_FILE);
            if (csvFile.exists()) {
                ReversedLinesFileReader object = new ReversedLinesFileReader(csvFile, Charset.defaultCharset());
                String lastLine = object.readLine();
                return lastLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<Track> getTracks(JSONObject obj) {
        List<Track> tracks = new ArrayList<>();
        JSONArray jsonArray = obj.getJSONObject("recenttracks").getJSONArray("track");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject trackJson = jsonArray.getJSONObject(i);
            Track track = getTrack(trackJson);
            if (track != null) {
                tracks.add(track);
            }
        }
        sortTracks(tracks);
        return tracks;
    }

    private void sortTracks(List<Track> tracks) {
        Collections.sort(tracks, (t1, t2) -> {
            if (t1.getPlayedAt() > t2.getPlayedAt()) {
                return 1;
            } else {
                return -1;
            }
        });
    }

    private Track getTrack(JSONObject trackJson) {
        if (trackJson.has("@attr")) {
            JSONObject attr = trackJson.getJSONObject("@attr");
            if (attr.has("nowplaying")) {
                boolean nowplaying = attr.getBoolean("nowplaying");
                if (nowplaying) {
                    return null;
                }
            }
        }
        String name = trackJson.getString("name");
        String artist = trackJson.getJSONObject("artist").getString("#text");
        String album = trackJson.getJSONObject("album").getString("#text");
        JSONObject date = trackJson.getJSONObject("date");
        long playedAt = date.getLong("uts");
//        String playedAtDate = date.getString("#text");

        Track track = new Track(name, artist, album, playedAt);
        return track;
    }

    @Data
    @AllArgsConstructor
    class DataInfo {
        private int currentPage;
        private int totalPages;
        private int totalDatasets;
    }

}
