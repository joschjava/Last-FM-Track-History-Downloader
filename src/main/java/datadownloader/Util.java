package datadownloader;

import datadownloader.domain.DataInfo;
import datadownloader.domain.Track;

import java.util.Collections;
import java.util.List;

public class Util {

    public static int getNextPage(DataInfo dataInfo) {
        int currentPage = dataInfo.getCurrentPage();
        int totalPages = dataInfo.getTotalPages();
        if (currentPage == totalPages) {
            return -1;
        } else {
            return currentPage + 1;
        }
    }

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
