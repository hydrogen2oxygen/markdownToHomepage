package net.hydrogen2oxygen.markdowntohomepage.transformator;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedOutput;
import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RssFeedGenerator {

    private SyndFeed feed = new SyndFeedImpl();

    public RssFeedGenerator(Website website) {
        feed.setFeedType("rss_1.0");
        feed.setTitle(website.getTitle());
        feed.setLink(website.getBaseUrl());
        feed.setDescription(website.getDescription());
        //feed.setImage();
    }

    public static void main(String[] args) {

        Website website = new Website();
        website.setTitle("Test Site");
        website.setDescription("Some description");
        website.setBaseUrl("https://test.pi.pa.po.de");
        RssFeedGenerator rssFeedGenerator = new RssFeedGenerator(website);

        PostDetails postDetails = new PostDetails();
        postDetails.setTitle("Super Post Number 1");
        postDetails.setDate(Calendar.getInstance().getTime().toString());
        postDetails.setCategories("Homeopathy");
        postDetails.setTags("Calendula");
        postDetails.setAuthor("John Smith");
        postDetails.setUrl("https://test.pi.pa.po.de/2019/12/12/Super-Post-Number-1");

        rssFeedGenerator.addEntry(postDetails);
        rssFeedGenerator.generate("target/rss.xml");
    }

    public void addEntry(PostDetails postDetails) {

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(postDetails.getTitle());
        entry.setAuthor(postDetails.getAuthor());
        entry.setLink(postDetails.getUrl());
        entry.setPublishedDate(Calendar.getInstance().getTime());

        feed.setEntries(Arrays.asList(entry));

        if (postDetails.getDescription() != null) {
            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(postDetails.getDescription());
            entry.setDescription(description);
        }

        List<SyndCategory> categories = new ArrayList<>();
        SyndCategory category = new SyndCategoryImpl();
        category.setName(postDetails.getCategories());
        categories.add(category);

        entry.setCategories(categories);
    }

    private void generate(String path) {
        try {
            Writer writer = new FileWriter(path);
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
            syndFeedOutput.output(feed, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
