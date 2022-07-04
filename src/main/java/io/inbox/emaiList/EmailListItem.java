package io.inbox.emaiList;

import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table(value = "messages_by_user_and_folder")
public class EmailListItem {
    
    @PrimaryKey
    private EmailListItemKey key;

    @CassandraType(type=Name.LIST, typeArguments = Name.TEXT)
    private List<String> destination;

    @CassandraType(type=Name.TEXT)
    private String subject;

    @CassandraType(type=Name.BOOLEAN)
    private boolean isRead;

    @Transient
    private String timeAgo;

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public EmailListItemKey getKey() {
        return key;
    }

    public void setKey(EmailListItemKey key) {
        this.key = key;
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

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
   

}
