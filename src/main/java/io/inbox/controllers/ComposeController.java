package io.inbox.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import io.inbox.email.EmailService;
import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;
import io.inbox.folders.FolderService;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @GetMapping(value = "/compose")
    public String getComposePage(
        @RequestParam(required = false) String to,
        @AuthenticationPrincipal OAuth2User principal,
        Model model){

        if(principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
    
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        model.addAttribute("userId", userId);    

        Map<String, Integer> unreadEmailStats = folderService.mapCountToLabels(userId);
        model.addAttribute("stats", unreadEmailStats);
        List<String> uniqueIds = getIdListFromDestination(to);
        model.addAttribute("toIds", String.join(", ", uniqueIds));
        
        return "compose-page";
    }

    private List<String> getIdListFromDestination(String destination) {
        if(!StringUtils.hasText(destination)){
            return new ArrayList<String>();
        }

        String [] splitIds = destination.split(",");
        List<String> uniqueIds = Arrays.asList(splitIds)
            .stream()
            .map(StringUtils::trimWhitespace)
            .filter(StringUtils::hasText)
            .distinct()
            .collect(Collectors.toList());

        return uniqueIds;
    }

    @PostMapping("/sendEmail")
    public ModelAndView sendEmail(
        @RequestBody MultiValueMap<String, String> formData,
        @AuthenticationPrincipal OAuth2User principal
    ){

        if(principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return new ModelAndView("redirect:/");
        
        String from = principal.getAttribute("login");
        List<String> toIds = getIdListFromDestination(formData.getFirst("toIds"));
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");

        emailService.sendEmail(from, toIds, subject, body);
        return new ModelAndView("redirect:/");
    }
}
