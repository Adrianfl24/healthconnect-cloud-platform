package com.healthconnect.platform.repository;

import com.healthconnect.platform.model.Recenzie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecenzieRepository extends JpaRepository<Recenzie, Long> {
    // Găsește toate recenziile asociate unui anumit medic prin intermediul programării
    List<Recenzie> findByProgramareServiciuMedicId(Long medicId);
}