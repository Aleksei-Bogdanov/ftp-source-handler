package org.example.ftp.source.handler.api.controller;

import org.example.ftp.source.handler.api.domain.Photo;
import org.example.ftp.source.handler.api.service.FtpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/photos")
public class FtpController {
    @Autowired
    private FtpClientService ftpClientService;

    @GetMapping
    public List<Photo> findPhotosByPrefix() {
        return ftpClientService.findPhotosByPrefix("photos", "GRP327_");
    }

    @GetMapping("/photo")
    public ResponseEntity<Resource> showPhoto(@RequestParam(name = "path") String photoPath) {
        return ftpClientService.showPhoto(photoPath);
    }
}
