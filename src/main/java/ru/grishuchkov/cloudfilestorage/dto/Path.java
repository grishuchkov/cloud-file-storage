package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Path {
    private String pathString;
    private List<String> partsOfPath;

    public Path(String path) {
        this.pathString = path;
        this.partsOfPath = getPartsOfPath(path);
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
        this.partsOfPath = getPartsOfPath(pathString);
    }

    private List<String> getPartsOfPath(String path) {
        return Arrays.stream(path.split("/")).toList();
    }

}
