package net.hydrogen2oxygen.markdowntohomepage.transformator;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import net.hydrogen2oxygen.markdowntohomepage.domain.PostDetails;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;

import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

public class RssFeedGenerator {

    private SyndFeed feed = new SyndFeedImpl();
    private Website website;
    private List<SyndEntry> entries = new ArrayList<>();

    public RssFeedGenerator(Website website) {
        this.website = website;
        feed.setFeedType("rss_2.0");
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
        postDetails.setDate("2018-01-17T08:57:54+00:00");
        postDetails.setCategories("Homeopathy");
        postDetails.setTags("Calendula");
        postDetails.setAuthor("John Smith");
        postDetails.setUrl("2019/12/12/Super-Post-Number-1");
        rssFeedGenerator.addEntry(postDetails);

        PostDetails postDetails2 = new PostDetails();
        postDetails2.setTitle("Super Post Number 2");
        postDetails2.setDate("2019-06-17T08:57:54+00:00");
        postDetails2.setCategories("Homeopathy");
        postDetails2.setTags("Calendula");
        postDetails2.setAuthor("John Smith");
        postDetails2.setUrl("2019/12/12/Super-Post-Number-1");
        rssFeedGenerator.addEntry(postDetails2);

        rssFeedGenerator.generate("target/rss.xml");
    }

    public void addEntry(PostDetails postDetails) {

        if (StringUtility.isEmpty(postDetails.getDate())) {
            return;
        }

        if (dateIsOlderThanDays(postDetails, 30)) {
            return;
        }

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(postDetails.getTitle());
        entry.setAuthor(postDetails.getAuthor());
        entry.setLink(website.getBaseUrl() + "/" + postDetails.getUrl());
        entry.setPublishedDate(getCalendar(postDetails).getTime());

        /*if (postDetails.getDescription() != null) {
            SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(postDetails.getDescription());
            entry.setDescription(description);
        }*/

        /*
        List<SyndCategory> categories = new ArrayList<>();
        SyndCategory category = new SyndCategoryImpl();
        category.setName(postDetails.getCategories());
        categories.add(category);

        entry.setCategories(categories);
         */

        entries.add(entry);
    }

    private Boolean dateIsOlderThanDays(PostDetails postDetails, int days) {

        Calendar cal = getCalendar(postDetails);
        Calendar oneMonthAgo = new GregorianCalendar();
        oneMonthAgo.add(Calendar.DAY_OF_YEAR, -days);

        if (cal.after(oneMonthAgo)) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private Calendar getCalendar(PostDetails postDetails) {
        try {
            Calendar cal = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            cal.setTime(sdf.parse(postDetails.getDate().substring(0, 10)));
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generate(String path) {
        try {

            feed.setEntries(entries);

            Writer writer = new FileWriter(path);
            SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
            syndFeedOutput.output(feed, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
