package net.hydrogen2oxygen.markdowntohomepage.adapter;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import net.hydrogen2oxygen.markdowntohomepage.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/website")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    @GetMapping
    public Collection<Website> getAll() throws IOException {

        return websiteService.loadAllWebsites();
    }

    @GetMapping("{name}")
    public Website getByName(@PathVariable String name) throws IOException {

        return websiteService.getByName(name);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> create(@RequestBody Website website) throws IOException {

        websiteService.createOrUpdateWebsite(website);

        return new ResponseEntity<String>("Create or update ok!", HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<String> update(@RequestBody Website website) throws IOException {

        return create(website);
    }

    @DeleteMapping
    public @ResponseBody ResponseEntity<String> delete(Website website) throws IOException {

        websiteService.delete(website);

        return new ResponseEntity<String>("DELETE Response", HttpStatus.OK);
    }
}