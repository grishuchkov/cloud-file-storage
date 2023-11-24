package ru.grishuchkov.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping(path = "/uploadPage")
    public String uploadPage(Model model){
        model.addAttribute("UploadFiles", new UploadFiles());
        return "file-upload";
    }

    @GetMapping(path = "/download")
    public ResponseEntity<Resource> uploadFile(@RequestParam(value = "filename") String filename) {
        byte[] data = fileService.get(filename);

        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@ModelAttribute("UploadFiles") UploadFiles uploadFiles,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        uploadFiles.setOwnerUsername(userDetails.getUsername());
        fileService.save(uploadFiles);

        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/scan")
    public ResponseEntity<String> scan(@RequestParam(value = "path", defaultValue = "") String path){
        fileService.getFilesMetadata(path);

        return ResponseEntity.ok().body("ok");
    }
}
