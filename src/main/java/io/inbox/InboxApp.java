package io.inbox;

import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.inbox.emaiList.EmailListItem;
import io.inbox.emaiList.EmailListItemKey;
import io.inbox.emaiList.EmailListItemRepository;
import io.inbox.email.Email;
import io.inbox.email.EmailRepository;
import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;

@SpringBootApplication
@RestController
@EnableCassandraRepositories
public class InboxApp {

    @Autowired
    FolderRepository folderRespository;

    @Autowired
    EmailListItemRepository emailListItemRepository;

    @Autowired
    EmailRepository emailRepository;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

    @PostConstruct
    public void init(){
        folderRespository.save(new Folder("vict0rsan", "Inbox", "blue"));
        folderRespository.save(new Folder("vict0rsan", "Sent", "green"));
        folderRespository.save(new Folder("vict0rsan", "Important", "yellow"));

        for(int i = 0; i < 10; i++){
            EmailListItemKey key = new EmailListItemKey();
            key.setId("vict0rsan");
            key.setLabel("Inbox");
            key.setTimeUUID(Uuids.timeBased());

            EmailListItem item = new EmailListItem();
            item.setKey(key);
            item.setDestination(List.of("vict0rsan", "abc", "testingUser"));
            item.setSubject("Subject: " + i);
            item.setIsRead(false);
            emailListItemRepository.save(item);

            Email email = new Email();
            email.setId(key.getTimeUUID());
            email.setSender("vict0rsan");
            email.setSubject("Subject:" + i);
            email.setBody("Body: " + i);
            email.setDestination(item.getDestination());
            emailRepository.save(email);
        }
    }

    
	

}
