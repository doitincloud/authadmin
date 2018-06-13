package com.doitincloud.authadmin.repositories;

import com.doitincloud.authadmin.models.Term;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface TermJpaRepo extends PagingAndSortingRepository<Term, Long> {

    @RestResource(path="name-types", rel = "name-types")
    public Optional<Term> findByNameAndType(@Param("name") String name, @Param("type") String type);

    @RestResource(path="types", rel = "types")
    public Page<Term> findByType(@Param("type") String type, Pageable pageable);

    @RestResource(path="types2", rel = "types2")
    public List<Term> findByType(@Param("type") String type);

    @RestResource(path="names", rel = "names")
    public Page<Term> findByName(@Param("name") String name, Pageable pageable);

    @RestResource(path="unique-names", rel = "unique-names")
    @Query(value = "SELECT DISTINCT t.name FROM Term t")
    public String[] findDistinctByName();

    @RestResource(path="unique-types", rel = "unique-types")
    @Query(value = "SELECT DISTINCT t.type FROM Term t")
    public String[] findDistinctByType();
}
