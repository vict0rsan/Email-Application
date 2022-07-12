package io.inbox.email;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.inbox.emaiList.EmailListItem;
import io.inbox.emaiList.EmailListItemKey;
import io.inbox.emaiList.EmailListItemRepository;
import io.inbox.folders.UnreadEmailStatsRepository;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> to, String subject, String body){

        Email email = new Email();
        email.setDestination(to);
        email.setSender(from);
        email.setId(Uuids.timeBased());
        email.setBody(body);
        email.setSubject(subject);

        emailRepository.save(email);

        to.forEach(toId ->{
            EmailListItem item = createEmailListItem(to, subject, email, toId, "Inbox");
            emailListItemRepository.save(item);
            unreadEmailStatsRepository.incrementUnreadCounter(toId, "Inbox");
        });

        EmailListItem sentItemEntry = createEmailListItem(to, subject, email, from, "Sent Items");
        sentItemEntry.setIsRead(true);
        emailListItemRepository.save(sentItemEntry);

    }

    private EmailListItem createEmailListItem(List<String> to, String subject,
     Email email, String itemOwner, String folder) {
        EmailListItemKey key = new EmailListItemKey();
        key.setId(itemOwner);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        EmailListItem item = new EmailListItem();
        item.setKey(key);
        item.setDestination(to);
        item.setSubject(subject);
        item.setRead(false);

        return item;
    }
    
}
