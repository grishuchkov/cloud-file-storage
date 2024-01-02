package ru.grishuchkov.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.grishuchkov.cloudfilestorage.dto.*;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;
import ru.grishuchkov.cloudfilestorage.util.validate.FileValidator;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public final class FileController {

    private final FileService fileService;
    private final FileValidator fileValidator;

    @GetMapping("/download")
    public ResponseEntity<Resource> uploadFile(@RequestParam(value = "path", defaultValue = "") String path,
                                               @RequestParam(value = "filename") String filename,
                                               @RequestParam(value = "extension") String extension,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        if (!isAuthenticated(userDetails)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        FileMetadata fileMetadata = buildFileMetadata(path, filename, extension, getUserDetailsUsername(userDetails));

        byte[] fileInBytes = fileService.downloadFile(fileMetadata);
        String filenameWithExtension = fileMetadata.getFileInfo().getFilenameWithExtension();

        ContentDisposition contentDisposition = ContentDisposition
                .builder("attachment")
                .filename(filenameWithExtension, StandardCharsets.UTF_8)
                .build();

        HttpHeaders header = new HttpHeaders();
        header.setContentDisposition(contentDisposition);

        return ResponseEntity
                .ok()
                .contentLength(fileInBytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(header)
                .body(new ByteArrayResource(fileInBytes));
    }

    @DeleteMapping(path = "/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @ModelAttribute("FileMetadata") FileMetadata fileMetadata) {

        if (!isAuthenticated(userDetails)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        fileMetadata.setOwnerUsername(getUserDetailsUsername(userDetails));

        fileService.delete(fileMetadata);

        String path = fileMetadata.getFilePath().getPathString();
        if (path.isEmpty()) {
            return "redirect:/home";
        }
        return "redirect:/home?path=" + UriUtils.encodePath(path, "UTF-8");
    }

    @PostMapping("/rename")
    public String renameFile(@ModelAttribute("FileMetadataForRename")FileMetadataForRename fileMetadata,
                             @AuthenticationPrincipal UserDetails userDetails){
        if (!isAuthenticated(userDetails)) {
            throw new AccessDeniedException("User is not authenticated");
        }
        fileMetadata.setOwnerUsername(getUserDetailsUsername(userDetails));

        fileValidator.validate(fileMetadata);
        fileService.rename(fileMetadata);

        String path = fileMetadata.getFilePath().getPathString();
        if (path.isEmpty()) {
            return "redirect:/home";
        }
        return "redirect:/home?path=" + UriUtils.encodePath(path, "UTF-8");
    }


    @PostMapping("/upload")
    public String upload(@ModelAttribute("UploadFiles") UploadFiles uploadFiles,
                         @AuthenticationPrincipal UserDetails userDetails) {

        if (!isAuthenticated(userDetails)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        uploadFiles.setOwnerUsername(getUserDetailsUsername(userDetails));
        fileService.save(uploadFiles);

        return "redirect:/home";
    }

    private boolean isAuthenticated(UserDetails userDetails) {
        return userDetails != null;
    }

    private static String getUserDetailsUsername(UserDetails userDetails) {
        return userDetails.getUsername();
    }

    private FileMetadata buildFileMetadata(String path, String filename, String extension, String username) {
        return FileMetadata.builder()
                .filePath(new FilePath(path))
                .fileInfo(new FileInfo(filename, extension))
                .ownerUsername(username)
                .build();
    }
}
