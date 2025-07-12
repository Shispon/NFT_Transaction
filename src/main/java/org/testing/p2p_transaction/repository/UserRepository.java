package org.testing.p2p_transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.testing.p2p_transaction.entity.User;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByUserName(String userName);
    boolean existsByMail(String mail);
    boolean existsByUserName(String userName);
}
