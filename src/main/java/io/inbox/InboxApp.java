package io.inbox;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.web.bind.annotation.RestController;

import io.inbox.email.EmailService;
import io.inbox.folders.Folder;
import io.inbox.folders.FolderRepository;

@SpringBootApplication
@EnableCassandraRepositories
@RestController
public class InboxApp {

    @Autowired
    private FolderRepository folderRespository;

    @Autowired
    private EmailService emailService;


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
        folderRespository.save(new Folder("vict0rsan", "Work", "blue"));
        folderRespository.save(new Folder("vict0rsan", "University", "green"));
        folderRespository.save(new Folder("vict0rsan", "Family", "yellow"));

        for(int i = 0; i < 10; i++){
            emailService.sendEmail("vict0rsan", Arrays.asList("vict0rsan", "test1", "abc"), "Hello: " + i, "Body: " + i);
        }

    }
}
