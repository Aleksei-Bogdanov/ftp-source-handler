package org.example.ftp.source.handler.api.service;

import lombok.SneakyThrows;
import org.example.ftp.source.handler.api.domain.Photography;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FtpClientServiceTest {
    private FakeFtpServer fakeFtpServer;

    @Mock
    private FtpClient ftpClient;

    @Mock
    private FtpClientService ftpClientService;

    @BeforeEach
    @SneakyThrows
    public void setup() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();

        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/GRP327_.jpeg"));

        fileSystem.add(new DirectoryEntry("/data/photos"));
        fileSystem.add(new FileEntry("/data/photos/GRP327_1.jpeg"));
        fileSystem.add(new FileEntry("/data/photos/GRP327_2.jpeg"));

        fileSystem.add(new DirectoryEntry("/data/photos/photos"));
        fileSystem.add(new FileEntry("/data/photos/photos/GRP327_3.jpeg"));

        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
        ftpClientService = new FtpClientService(ftpClient);
    }

    @AfterEach
    @SneakyThrows
    public void teardown(){
        ftpClient.close();
        fakeFtpServer.stop();
    }

    public List<Photography> data() {

        Photography mockPhoto1 = new Photography();
        mockPhoto1.setFullPath("/data/photos/GRP327_1.jpeg");

        Photography mockPhoto2 = new Photography();
        mockPhoto2.setFullPath("/data/photos/GRP327_2.jpeg");

        Photography mockPhoto3 = new Photography();
        mockPhoto3.setFullPath("/data/photos/photos/GRP327_3.jpeg");

        return Arrays.asList(mockPhoto1, mockPhoto3, mockPhoto3);
    }

    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() {
        String searchDirName = "photos";
        String photoPrefix = "GRP327_";
        List<Photography> actualPhotos = ftpClientService.findPhotosByPrefix(searchDirName, photoPrefix);

        Photography photoForFind1 = new Photography();
        photoForFind1.setFullPath("/data/photos/GRP327_1.jpeg");

        assertThat(actualPhotos).contains(photoForFind1);
    }

    @Test
    void findPhotosByPrefix() {
    }

    @Test
    void showPhoto() {
    }
}