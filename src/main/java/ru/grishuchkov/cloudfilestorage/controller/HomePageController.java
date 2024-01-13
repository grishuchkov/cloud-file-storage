package ru.grishuchkov.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadataForRename;
import ru.grishuchkov.cloudfilestorage.dto.FilesContainer;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final FileService fileService;

    @GetMapping({"/", "/home"})
    public String getHomePage(Model model,
                              @RequestParam(value = "path", defaultValue = "") String path,
                              @AuthenticationPrincipal UserDetails userDetails) {

        model.addAttribute("UploadFiles", new UploadFiles());
        model.addAttribute("FileMetadata", new FileMetadata());
        model.addAttribute("FileMetadataForRename", new FileMetadataForRename());

        if(isAuthenticated(userDetails)){
            FilesContainer filesContainer = fileService
                    .getFilesInfoOfUser(path, userDetails.getUsername());

            model.addAttribute("FilesContainer", filesContainer);
        }

        return "home";
    }

    private boolean isAuthenticated(UserDetails userDetails) {
        return userDetails != null;
    }
}
