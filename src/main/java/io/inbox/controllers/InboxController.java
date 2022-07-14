package io.inbox.controllers;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.inbox.emaiList.EmailListItem;
import io.inbox.emaiList.EmailListItemRepository;
import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;
import io.inbox.folders.FolderService;
import io.inbox.folders.UnreadEmailStatsRepository;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping(value = "/")    
    public String homePage(@RequestParam(required = false) String folder,
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

        if(!StringUtils.hasText(folder)){
            folder = "Inbox";
        }

        List<EmailListItem> emailList = 
            emailListItemRepository.findAllByKey_IdAndKey_Label(userId, folder);

        PrettyTime prettyTime = new PrettyTime();
        prettyTime.setLocale(Locale.US);
        emailList.forEach(emailItem -> {
            UUID timeUuid = emailItem.getKey().getTimeUUID();
            Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
            emailItem.setTimeAgo(prettyTime.format(emailDateTime));
        });

        model.addAttribute("emailList", emailList);
        model.addAttribute("folderName", folder);
        model.addAttribute("userId", userId);


            
        return "inbox-page";
        
    }

}
