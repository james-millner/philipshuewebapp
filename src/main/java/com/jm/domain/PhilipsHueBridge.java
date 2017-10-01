package com.jm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class PhilipsHueBridge {

    @Id
    private String id;

    private String ipAddress;

    private String username;

    @CreatedDate
    private Date establishedConnection;

    private Date lastUsed;

    public PhilipsHueBridge() {
    }

    public PhilipsHueBridge(String ipAddress, String username) {
        this.id = UUID.randomUUID().toString();
        this.ipAddress = ipAddress;
        this.username = username;
        this.establishedConnection = new Date();
    }
}
