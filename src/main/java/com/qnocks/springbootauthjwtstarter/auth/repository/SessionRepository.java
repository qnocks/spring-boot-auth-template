package com.qnocks.springbootauthjwtstarter.auth.repository;

import com.qnocks.springbootauthjwtstarter.auth.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByUserId(Long id);

    void deleteByUserId(Long id);

    Boolean existsByUserId(Long id);
}
