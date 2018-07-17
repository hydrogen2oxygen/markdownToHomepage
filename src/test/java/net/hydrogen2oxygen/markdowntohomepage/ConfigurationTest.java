package net.hydrogen2oxygen.markdowntohomepage;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hydrogen2oxygen.markdowntohomepage.domain.ConfigurationObject;
import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ConfigurationTest {

    @Test
    public void testConfig() throws IOException {
        ConfigurationObject configurationObject = new ConfigurationObject();
        Website website = new Website();
        website.setName("Test");
        website.setSourceFolder("src/test/resources");
        website.setTargetFolder("target");
        website.setHeaderFile("src/test/resources/testHeader.html");
        website.setFooterFile("src/test/resources/testFooter.html");
        configurationObject.getWebsites().add(website);
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(new File("target/configurationExample.json"), configurationObject);
    }
}