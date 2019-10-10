package datadownloader;

import datadownloader.domain.DataInfo;
import datadownloader.domain.Track;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class JsonParser {


    /**
     * Reads current page, total pages and total number of tracks from JSON string
     * @param fullResponseJson The direct response from last fm
     * @return DataInfo object with said attributes
     */
    DataInfo getDataInfo(JSONObject fullResponseJson) {
        JSONObject attr = fullResponseJson.getJSONObject("recenttracks").getJSONObject("@attr");
        int currentPage = attr.getInt("page");
        int totalPages = attr.getInt("totalPages");
        int totalDatasets = attr.getInt("total");
        DataInfo dataInfo = new DataInfo(currentPage, totalPages, totalDatasets);
        return dataInfo;
    }

    /**
     * Converts JSON string into Track object
     * @param trackJson
     * @return Track object with name, artist, album and date information
     */
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

    /**
     * Converts JSON object to Track List
     * @param allTracksJson
     * @return
     */
    List<Track> getTracks(JSONObject allTracksJson) {
        List<Track> tracks = new ArrayList<>();
        JSONArray jsonArray = allTracksJson.getJSONObject("recenttracks").getJSONArray("track");
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
