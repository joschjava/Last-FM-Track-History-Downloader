package datadownloader.domain;

import datadownloader.Config;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Track {
    String name;
    String artist;
    String album;
    long playedAt;

    public String toCsv() {
        return name + Config.CSV_DELIMITER +
                artist + Config.CSV_DELIMITER +
                album + Config.CSV_DELIMITER +
                playedAt;
    }
}
