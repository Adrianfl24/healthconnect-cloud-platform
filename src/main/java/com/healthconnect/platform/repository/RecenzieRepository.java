package com.healthconnect.platform.repository;

import com.healthconnect.platform.model.Recenzie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecenzieRepository extends JpaRepository<Recenzie, Long> {}