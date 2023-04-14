package org.example.ftp.source.handler.api.config;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class FtpClientConfig {
    private final String server = "ftpupload.net";
    private final int port = 21;
    private final String user = System.getenv("FTP_USER");
    private final String password = System.getenv("FTP_PASSWORD");
    private FTPClient ftp;

    @Bean("ftpClient")
    public FTPClient ftpClient() {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        try{
            ftp.connect(server, port);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("Exception in connecting to FTP Server");
            }
            ftp.login(user, password);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        return ftp;
    }
}
