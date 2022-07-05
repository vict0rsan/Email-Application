package io.inbox.email;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "messages_by_id")
public class Email {

    @Id
    @PrimaryKeyColumn(name="id", ordinal=0, type=PrimaryKeyType.CLUSTERED)
    private UUID id;

    @CassandraType(type = Name.TEXT)
    private String sender;

    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> destination;

    @CassandraType(type = Name.TEXT)
    private String subject;

    @CassandraType(type = Name.TEXT)
    private String body;

   

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getDestination() {
        return destination;
    }

    public void setDestination(List<String> destination) {
        this.destination = destination;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    
}
