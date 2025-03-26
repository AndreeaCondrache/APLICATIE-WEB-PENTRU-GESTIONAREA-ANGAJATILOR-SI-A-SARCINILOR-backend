package com.andreeacondrache.licenta.repositories;

import com.andreeacondrache.licenta.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.andreeacondrache.licenta.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserRole(UserRole userRole);

    Optional<User>findFirstByEmail(String email);

}
