package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Path {
    private final String pathString;
    private final List<String> partsOfPath;

    public Path(String path) {
        this.pathString = path;
        this.partsOfPath = getPartsOfPath(path);
    }

    private List<String> getPartsOfPath(String path) {
        List<String> list = Arrays.stream(path.split("/")).toList();

        return list;
    }
}
