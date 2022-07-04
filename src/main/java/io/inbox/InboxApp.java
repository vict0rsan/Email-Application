package io.inbox;

import java.nio.file.Path;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.web.bind.annotation.RestController;

import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;

@SpringBootApplication
@RestController
@EnableCassandraRepositories
public class InboxApp {

    @Autowired
    FolderRepository folderRespository;

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
    }

    
	

}
