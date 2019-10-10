package datadownloader;

import datadownloader.domain.Track;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class FileHandler {

    /**
     * Reads the newest timestamp from file in generated csv file
     * @return
     */
    long getNewestTimestamp() {
        String csvFileContent = getLastLineFromCsvFile();
        if (csvFileContent != null) {
            String[] fields = csvFileContent.split(Config.CSV_DELIMITER);
            long newestTimestamp = Long.valueOf(fields[3]);
            return newestTimestamp;
        }
        return 0;
    }

    void writeToCsvFile(List<Track> tracks) {
        StringBuilder sb = new StringBuilder();
        int trackSize = tracks.size();
        for (int i = 0; i < trackSize; i++) {
            Track track = tracks.get(i);
            sb.append(track.toCsv()).append("\n");
        }
        saveToFile(sb.toString(), Config.SAVE_FILE, true);
    }

    private void saveToFile(String text) {
        saveToFile(text, null, false);
    }

    void saveToFile(String text, String filename, boolean append) {
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

    void saveServerResponse(String responseString){
        long currentTimeMs = System.currentTimeMillis();
        String rawDataFilePath = Config.RESPONSE_LOG_FOLDER + "/" + currentTimeMs + ".json";
        saveToFile(responseString, rawDataFilePath, false);
    }

}
