package org.example.ftp.source.handler.api.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.example.ftp.source.handler.api.domain.Photo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FtpClientService {
    private final FTPClient ftpClient;
    private final ResourceLoader resourceLoader;
    private final List<Photo> photoList = new ArrayList<>();

    public List<Photo> findPhotosByPrefix(
            String searchDirName,
            String photoPrefix
    ){
        return findDirectoriesWithGivenName("", "", 0, searchDirName, photoPrefix);
    }

    public ResponseEntity<Resource> showPhoto(String photoPath) {
        String filePath = downloadFile(photoPath);

        File file = new File(filePath);
        InputStreamResource resource;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }


    private List<Photo> findDirectoriesWithGivenName(
            String parentDir,
            String currentDir,
            int level,
            String searchDir,
            String photoPrefix
    ) {

        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += File.separator + currentDir;
        }

        FTPFile[] subFiles;
        try {
            subFiles = ftpClient.listFiles(dirToList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (subFiles != null) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();

                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and directory itself
                    continue;
                }

                if (aFile.isDirectory() ) {
                    if (aFile.getName().equals(searchDir)){
                        String searchingPath = dirToList + File.separator + aFile.getName();
                        photoList.addAll(getPhotosWithGivenPrefixFromCurrentDir(searchingPath, photoPrefix));
                    }
                    findDirectoriesWithGivenName(dirToList, currentFileName, level + 1, searchDir, photoPrefix);
                }
            }
        }
        return photoList;
    }

    private List<Photo> getPhotosWithGivenPrefixFromCurrentDir(String currentDirPath, String filePrefix) {
        List<Photo> photosList;

        try {
            FTPFile[] dir = ftpClient.listFiles (currentDirPath);
            photosList = Arrays.stream(dir)
                    .filter(FTPFile::isFile)
                    .filter(file -> file.getName().startsWith(filePrefix))
                    .map(file -> new Photo(
                            currentDirPath + File.separator + file.getName(),
                            file.getTimestamp().getTime(),
                            file.getSize() + " kb"
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return photosList;
    }

    private String downloadFile(String source) {
        String fileName = String.valueOf(Paths.get(source).getFileName());
        String downloadPath = "./downloads";
        File folder = new File(downloadPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String destination = downloadPath + File.separator + fileName;
        try {
            FileOutputStream out = new FileOutputStream(destination);
            ftpClient.retrieveFile(source, out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return destination;
    }
}
