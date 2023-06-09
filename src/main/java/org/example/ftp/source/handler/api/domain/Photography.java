package org.example.ftp.source.handler.api.domain;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Photography {
    private String fullPath;
    private Date createdAt;
    private String photoSize;
}
