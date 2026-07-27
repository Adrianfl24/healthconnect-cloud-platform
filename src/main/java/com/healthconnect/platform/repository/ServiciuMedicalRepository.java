package com.healthconnect.platform.repository;

import com.healthconnect.platform.model.ServiciuMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiciuMedicalRepository extends JpaRepository<ServiciuMedical, Long> {}