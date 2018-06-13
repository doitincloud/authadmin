package com.doitincloud.authadmin.repositories;

import com.doitincloud.authadmin.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource
public interface UserJpaRepo extends PagingAndSortingRepository<User, String>, UserExRepo {

    @RestResource(path="ids", rel = "ids")
    public Optional<User> findByUserId(@Param("id") String id);

    @RestResource(path="usernames", rel = "usernames")
    public Optional<User> findByUsername(@Param("name") String name);

    @RestResource(path="first-names", rel = "first-names")
    public Page<User> findByFirstName(@Param("name") String first, Pageable pageable);

    @RestResource(path="last-names", rel = "last-names")
    public Page<User> findByLastName(@Param("name") String last, Pageable pageable);

    @RestResource(path="full-names", rel = "full-names")
    public Page<User> findByFirstNameAndLastName(@Param("first-name") String first, @Param("last-name") String last, Pageable pageable);

    @RestResource(path="phone-numbers", rel = "phone-numbers")
    public Page<User> findByPhoneNumber(@Param("number") String uumber, Pageable pageable);
}
