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
    private File directory;

    public static void main(String[] args) throws IOException {
        if (args.length == 6) {
            Website website = new Website();
            website.setFtpHost(args[1]);
            website.setFtpPort(Integer.parseInt(args[2]));
            website.setFtpUser(args[3]);
            website.setFtpPassword(args[4]);
            website.setFtpRootPath(args[5]);
            website.setTargetFolder(args[0]);

            FtpService ftpService = new FtpService(website);
            System.out.println("=============================");
            ftpService.uploadDirectory(new File(website.getTargetFolder()));
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

    public void uploadDirectory(File directory) throws IOException {

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("The path provided does not exist or it is not a directory!");
        }

        if (this.directory == null) {
            this.directory = directory;
        }

        for (File file : directory.listFiles()) {

            if (file.isDirectory()) {
                uploadDirectory(file);
                continue;
            }

            String path = getDirectoryPath(file.getParentFile());

            createDirectory(path);

            System.err.println(path);
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getParentFile().getName());
        }
    }

    public boolean directoryExist(String path) {
        try {
            return ftp.changeWorkingDirectory(website.getFtpRootPath() + path);
        } catch (IOException e) {
            return false;
        } finally {
            changeToParentDirectory();
        }
    }

    private void changeToParentDirectory() {
        try {
            ftp.changeToParentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDirectory(String path) {
        try {

            String parts [] = path.split("/");
            String newPath = "";

            for (String part : parts) {

                if(part.length() == 0) {
                    continue;
                }

                newPath += "/" + part;

                if (!directoryExist(newPath)) {
                    System.out.println("create path " + newPath);
                    ftp.makeDirectory(website.getFtpRootPath() + newPath);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDirectoryPath(File directory) {
        if (directory.equals(this.directory)) {
            return "";
        }

        String path = "";

        File currentDirectory = directory;

        while (!currentDirectory.equals(this.directory)) {
            path = "/" + currentDirectory.getName() + path;
            currentDirectory = currentDirectory.getParentFile();
        }

        return path;
    }

    public void close() throws IOException {
        ftp.disconnect();
    }
}
