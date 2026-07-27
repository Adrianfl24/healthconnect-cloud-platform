package com.healthconnect.platform.repository;

import com.healthconnect.platform.model.Utilizator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilizatorRepository extends JpaRepository<Utilizator, Long> {
    // Aici vom putea adăuga metode custom mai târziu, de ex: findByEmail(String email)
}