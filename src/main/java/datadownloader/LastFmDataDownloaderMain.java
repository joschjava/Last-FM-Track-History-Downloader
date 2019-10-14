package datadownloader;

import datadownloader.domain.DataInfo;
import datadownloader.domain.Track;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class LastFmDataDownloaderMain {

    public static void main(String[] args) {
        Config.loadConfig();
        Connector connection = new Connector();
        JsonParser parser = new JsonParser();
        FileHandler fileHandler = new FileHandler();
        List<Track> tracks = new ArrayList<>();
        int nextPage = 1;
        long newestTimestamp = fileHandler.getNewestTimestamp() + 1; //+1: Don't involve newestTimestamp itself
        int totalDatasets;
        do {
            String rawDataFromServer = connection.getRawDataFromServer(nextPage, newestTimestamp);
            fileHandler.saveServerResponse(rawDataFromServer);
            JSONObject obj = new JSONObject(rawDataFromServer);
            DataInfo dataInfo = parser.getDataInfo(obj);
            totalDatasets = dataInfo.getTotalDatasets();
            if (dataInfo.getTotalPages() != 0) {
                List<Track> pageTracks = parser.getTracks(obj);
                tracks.addAll(pageTracks);
                int scrapedPage = nextPage;
                nextPage = Util.getNextPage(dataInfo);
                log.info("Scraped page " + scrapedPage + "/" + dataInfo.getTotalPages());
            } else {
                log.info("No new tracks found");
                nextPage = -1;
            }
        } while (nextPage != -1);
        Util.sortTracks(tracks);
        int numExtractedTracks = tracks.size();
        if (totalDatasets != numExtractedTracks) {
            throw new RuntimeException("It seems like not all datasets have been scraped: \n" +
                    "Total Datasets: " + totalDatasets + "\n" +
                    "Extracted Datasets: " + numExtractedTracks);
        }
        if (numExtractedTracks > 0) {
            fileHandler.writeToCsvFile(tracks);
        }
        log.info("Scraping finished. Added " + totalDatasets + " tracks");
    }
}
