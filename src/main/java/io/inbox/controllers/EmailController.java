package io.inbox.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.inbox.email.Email;
import io.inbox.email.EmailRepository;
import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;
import io.inbox.folders.FolderService;

@Controller
public class EmailController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailRepository emailRepository;


    @GetMapping(value = "/emails/{id}")    
    public String emailView(@PathVariable UUID id,
        @AuthenticationPrincipal OAuth2User principal,
        Model model){
        
        if(principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
        
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        Map<String, Integer> unreadEmailStats = folderService.mapCountToLabels(userId);
        model.addAttribute("stats", unreadEmailStats);

        Optional<Email> optionalEmail = emailRepository.findById(id);

        if(!optionalEmail.isPresent()){
            return "inbox-page";
        }

        Email email = optionalEmail.get();

        String destinations = String.join(", ", email.getDestination());

        model.addAttribute("email", email);
        model.addAttribute("destinations", destinations);


            
        return "email-page";
        
    }
    
}
