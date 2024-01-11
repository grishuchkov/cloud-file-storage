package ru.grishuchkov.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.service.FileSearchService;

import java.util.List;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchPageController {

    private final FileSearchService fileSearchService;

    @GetMapping
    public String getSearchPage(@RequestParam(name = "query") String nameForSearch,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {

        List<FileMetadata> files = fileSearchService
                .searchFiles(userDetails.getUsername(), nameForSearch);

        model.addAttribute("FoundFiles", files);

        return "search-page";
    }
}
