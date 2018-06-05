package doitincloud.authadmin.repositories;

import doitincloud.authadmin.models.Client;
import doitincloud.authadmin.models.Term;
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
public interface ClientJpaRepo extends PagingAndSortingRepository<Client, String>, ClientExRepo {

    @RestResource(path="ids", rel = "ids")
    public Optional<Client> findByClientId(@Param("id") String id);

    @RestResource(path="client-names", rel = "client-names")
    public Optional<Client> findByClientName(@Param("name") String name);

    @RestResource(path="contact-names", rel = "contact-names")
    public Page<Client> findByContactName(@Param("name") String name, Pageable pageable);

    @RestResource(path="contact-emails", rel = "contact-emails")
    public Page<Client> findByContactEmail(@Param("email") String email, Pageable pageable);

    @RestResource(path="contact-phone-numbers", rel = "contact-phone-numbers")
    public Page<Client> findByContactPhoneNumber(@Param("number") String number, Pageable pageable);

}
