package net.hydrogen2oxygen.markdowntohomepage.domain;

import org.junit.Test;

public class WebsiteTest {

    @Test
    public void test() {
        Website website = new Website();
        website.setName("My Blog");
        website.setAuthor("John Smith");
        website.setTitle("My Blog");
        website.setDescription("Personal Stuff");
        website.setBaseUrl("https://www.hydrogen2oxygen.net/blog/");
        website.setGitUrl("https://git...");
        website.setGitUser("user");
        website.setGitPassword("secretOrUseSSH");
        website.setSourceFolder("/myBlog/source");
        website.setTargetFolder("/myBlog/public");
        website.setHeaderFile("/myBlog/templates/header.html");
        website.setFooterFile("/myBlog/templates/footer.html");
        website.setFtpHost("ftp://...");
        website.setFtpPort(22);
        website.setFtpUser("user");
        website.setFtpPassword("secret");
        website.setFtpRootPath("/etc/www/myBlog/");

        System.out.println(website);
    }
}
