package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFiles {
    private List<MultipartFile> files;
    private String ownerUsername;
}
