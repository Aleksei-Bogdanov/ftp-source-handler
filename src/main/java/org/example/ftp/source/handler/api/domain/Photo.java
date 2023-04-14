package org.example.ftp.source.handler.api.domain;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Photo {
    private String fullPath;
    private Date createdAt;
    private String photoSize;
}
