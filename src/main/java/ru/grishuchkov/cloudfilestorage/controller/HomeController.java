package ru.grishuchkov.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.grishuchkov.cloudfilestorage.dto.File;
import ru.grishuchkov.cloudfilestorage.dto.FilesContainer;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final FileService fileService;

    @GetMapping({"/", "/home"})
    public String getHomePage(Model model,
                              @RequestParam(value = "path", defaultValue = "") String path,
                              @AuthenticationPrincipal UserDetails userDetails) {

        model.addAttribute("UploadFiles", new UploadFiles());

        if(userDetails != null){
            FilesContainer filesContainer = fileService
                    .getUserFiles(path, userDetails.getUsername());
            model.addAttribute("FilesContainer", filesContainer);
        }

        return "home";
    }
}
