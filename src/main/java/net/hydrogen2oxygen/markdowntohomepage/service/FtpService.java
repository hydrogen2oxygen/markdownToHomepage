package net.hydrogen2oxygen.markdowntohomepage.service;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class FtpService {

    private Website website;
    private FTPClient ftp;

    public static void main(String[] args) throws IOException {
        if (args.length == 6) {
            File file = new File(args[0]);

            Website website = new Website();
            website.setFtpHost(args[1]);
            website.setFtpPort(Integer.parseInt(args[2]));
            website.setFtpUser(args[3]);
            website.setFtpPassword(args[4]);
            website.setFtpRootPath(args[5]);

            FtpService ftpService = new FtpService(website);
            ftpService.uploadFile(file,"/" + file.getName());
            ftpService.close();
        }
    }

    public FtpService(Website website) throws IOException {
        this.website = website;
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(website.getFtpHost(), website.getFtpPort());
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(website.getFtpUser(), website.getFtpPassword());

        for (String file : listFiles("/")) {
            System.out.println(file);
        }
    }

    public Collection<String> listFiles(String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }

    public void uploadFile(File file, String path) throws IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s not found for uploading with FTP!", file.getAbsolutePath()));
        }

        ftp.storeFile(website.getFtpRootPath() + "/" + path, new FileInputStream(file));
    }

    public void close() throws IOException {
        ftp.disconnect();
    }
}
