package net.hydrogen2oxygen.markdowntohomepage.dao;

import net.hydrogen2oxygen.markdowntohomepage.domain.Website;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "website", path = "website")
public interface WebsiteRepository extends PagingAndSortingRepository<Website, Long> {

    List<Website> findByName(@Param("name") String name);
}
