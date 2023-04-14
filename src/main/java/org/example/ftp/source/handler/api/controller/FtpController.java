package org.example.ftp.source.handler.api.controller;

import org.example.ftp.source.handler.api.domain.Photography;
import org.example.ftp.source.handler.api.service.FtpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("api/v1/photos")
public class FtpController {
    @Autowired
    private FtpClientService ftpClientService;

    @GetMapping
    public List<Photography> findPhotosByPrefix(
            @RequestParam String searchDirName,
            @RequestParam String photoPrefix
    ) {
        return ftpClientService.findPhotosByPrefix(searchDirName, photoPrefix);
    }

    @GetMapping("/photo")
    public ResponseEntity<Resource> showPhoto(@RequestParam(name = "path") String photoPath) {
        return ftpClientService.showPhoto(photoPath);
    }
}
