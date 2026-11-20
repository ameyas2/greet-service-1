package org.greet.repository;

import org.greet.entity.Greet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GreetJpaRepository extends JpaRepository<Greet, UUID> {
}
