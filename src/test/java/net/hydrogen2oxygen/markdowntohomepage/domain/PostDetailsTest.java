package net.hydrogen2oxygen.markdowntohomepage.domain;

import org.junit.Test;

public class PostDetailsTest {

    @Test
    public void testInit() {
        PostDetails postDetails = new PostDetails();
        Website website = new Website();
        website.setAuthor("Pietro Lusso");
        postDetails.initNewPostDetails(website);
        System.out.println(postDetails);
    }

}
