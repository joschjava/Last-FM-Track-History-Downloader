package datadownloader;

import datadownloader.domain.DataInfo;
import datadownloader.domain.Track;

import java.util.Collections;
import java.util.List;

public class Util {

    /**
     * Calculates the next page from DataInfo object
     * @param dataInfo
     * @return
     */
    public static int getNextPage(DataInfo dataInfo) {
        int currentPage = dataInfo.getCurrentPage();
        int totalPages = dataInfo.getTotalPages();
        if (currentPage == totalPages) {
            return -1;
        } else {
            return currentPage + 1;
        }
    }

    /**
     * Sorts the tracks in ascending order by unix timestamp
     * @param tracks
     */
    public static void sortTracks(List<Track> tracks) {
        Collections.sort(tracks, (t1, t2) -> {
            if (t1.getPlayedAt() > t2.getPlayedAt()) {
                return 1;
            } else {
                return -1;
            }
        });
    }

}
