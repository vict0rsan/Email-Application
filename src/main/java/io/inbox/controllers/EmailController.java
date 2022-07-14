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
import org.springframework.web.bind.annotation.RequestParam;

import io.inbox.emaiList.EmailListItem;
import io.inbox.emaiList.EmailListItemKey;
import io.inbox.emaiList.EmailListItemRepository;
import io.inbox.email.Email;
import io.inbox.email.EmailRepository;
import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;
import io.inbox.folders.FolderService;
import io.inbox.folders.UnreadEmailStatsRepository;

@Controller
public class EmailController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;


    @GetMapping(value = "/emails/{id}")    
    public String emailView(
        @RequestParam String folder,
        @PathVariable UUID id,
        @AuthenticationPrincipal OAuth2User principal,
        Model model
        ){
        
        if(principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
        
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        model.addAttribute("userId", userId);

        Optional<Email> optionalEmail = emailRepository.findById(id);

        if(!optionalEmail.isPresent()){
            return "inbox-page";
        }

        Email email = optionalEmail.get();
        String destinations = String.join(", ", email.getDestination());


        if(!(userId.equals(email.getSender())) && !(email.getDestination().contains(userId))){
            System.out.println("Sender: " + email.getSender());
            System.out.println("Destination: " + email.getDestination());
            System.out.println("UserId: " + userId);
            System.out.println("UserId contained in destination: " + email.getDestination().contains(userId));
            System.out.println("Redirecting...");
            return "redirect:/";
        }


        model.addAttribute("email", email);
        model.addAttribute("destinations", destinations);

        EmailListItemKey key = new EmailListItemKey();
        key.setId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());
        Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);

        if(optionalEmailListItem.isPresent()){
            EmailListItem emailListItem = optionalEmailListItem.get();
            if(emailListItem.getIsRead() == false){
                emailListItem.setRead(true);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCounter(userId, folder);
            }
        }

        Map<String, Integer> unreadEmailStats = folderService.mapCountToLabels(userId);
        model.addAttribute("stats", unreadEmailStats);

        return "email-page";
    }
    
}
