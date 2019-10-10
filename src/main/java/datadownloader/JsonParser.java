package datadownloader;

import datadownloader.domain.DataInfo;
import datadownloader.domain.Track;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    DataInfo getDataInfo(JSONObject obj) {
        JSONObject attr = obj.getJSONObject("recenttracks").getJSONObject("@attr");
        int currentPage = attr.getInt("page");
        int totalPages = attr.getInt("totalPages");
        int totalDatasets = attr.getInt("total");
        DataInfo dataInfo = new DataInfo(currentPage, totalPages, totalDatasets);
        return dataInfo;
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



    List<Track> getTracks(JSONObject obj) {
        List<Track> tracks = new ArrayList<>();
        JSONArray jsonArray = obj.getJSONObject("recenttracks").getJSONArray("track");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject trackJson = jsonArray.getJSONObject(i);
            Track track = getTrack(trackJson);
            if (track != null) {
                tracks.add(track);
            }
        }
        return tracks;
    }

}
