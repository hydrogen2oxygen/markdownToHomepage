package net.hydrogen2oxygen.markdowntohomepage;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.gui.ObjectDialog;
import org.junit.Assert;
import org.junit.Test;

public class ObjectDialogTest {

    @Test
    public void test() {

        Website website = new Website();
        Assert.assertEquals(null, website.getName());

        ObjectDialog objectDialog = new ObjectDialog(website);

        website = (Website) objectDialog.getObject();
        Assert.assertEquals("", website.getName());
    }
}