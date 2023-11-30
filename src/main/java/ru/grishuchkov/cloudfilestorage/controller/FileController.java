package ru.grishuchkov.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.grishuchkov.cloudfilestorage.dto.FileDetails;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/download")
    public ResponseEntity<Resource> uploadFile(@RequestParam(value = "filename") String filename,
                                               @RequestParam(value = "path", defaultValue = "") String path,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("userDetails == null");
        }
        byte[] data = fileService.downloadFile(path + filename, userDetails.getUsername());

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(new ByteArrayResource(data));
    }

    @DeleteMapping(path = "/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @ModelAttribute("fileDetails") FileDetails fileDetails) {
        String path = fileDetails.getPath().getPathString();

        if (userDetails == null) {
            throw new RuntimeException("userDetails == null");
        }

        fileDetails.setOwnerUsername(userDetails.getUsername());
        fileService.delete(fileDetails);

        if (path.isEmpty()) {
            return "redirect:/home";
        }
        return "redirect:/home?path=" + UriUtils.encodePath(path, "UTF-8");
    }

    @PostMapping("/upload")
    public String upload(@ModelAttribute("UploadFiles") UploadFiles uploadFiles,
                         @AuthenticationPrincipal UserDetails userDetails) {

        uploadFiles.setOwnerUsername(userDetails.getUsername());
        fileService.save(uploadFiles);

        return "redirect:/home";
    }
}
