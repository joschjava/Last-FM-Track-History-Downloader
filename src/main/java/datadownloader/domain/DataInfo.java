package datadownloader.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class DataInfo {
    private int currentPage;
    private int totalPages;
    private int totalDatasets;
}