package com.healthconnect.platform.service;

import com.healthconnect.platform.model.Pacient;
import com.healthconnect.platform.model.Programare;
import com.healthconnect.platform.model.ServiciuMedical;
import com.healthconnect.platform.model.StatusProgramare;
import com.healthconnect.platform.repository.PacientRepository;
import com.healthconnect.platform.repository.ProgramareRepository;
import com.healthconnect.platform.repository.ServiciuMedicalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgramareService {

    private final ProgramareRepository programareRepository;
    private final PacientRepository pacientRepository;
    private final ServiciuMedicalRepository serviciuMedicalRepository;

    public ProgramareService(ProgramareRepository programareRepository,
                           PacientRepository pacientRepository,
                           ServiciuMedicalRepository serviciuMedicalRepository) {
        this.programareRepository = programareRepository;
        this.pacientRepository = pacientRepository;
        this.serviciuMedicalRepository = serviciuMedicalRepository;
    }

   public Programare creeazaProgramare(Long pacientId, Long serviciuId, LocalDateTime data, String adresa, String metodaPlata) {
        Pacient pacient = pacientRepository.findById(pacientId)
                .orElseThrow(() -> new RuntimeException("Pacientul nu a fost gasit!"));
        ServiciuMedical serviciu = serviciuMedicalRepository.findById(serviciuId)
                .orElseThrow(() -> new RuntimeException("Serviciul medical nu a fost gasit!"));

        Programare programare = new Programare();
        programare.setDataProgramare(data);
        programare.setAdresa(adresa);
        programare.setMetodaPlata(metodaPlata);
        programare.setStatus(StatusProgramare.IN_ASTEPTARE);
        programare.setPacient(pacient);
        programare.setServiciu(serviciu);

        return programareRepository.save(programare);
    }

    // UC10: Schimbare status (Acceptare / Anulare / Finalizare)
    public Programare actualizeazaStatus(Long programareId, StatusProgramare statusNou) {
        Programare programare = programareRepository.findById(programareId)
                .orElseThrow(() -> new RuntimeException("Programarea nu exista!"));
        programare.setStatus(statusNou);
        return programareRepository.save(programare);
    }

    public List<Programare> obtineToateProgramarile() {
        return programareRepository.findAll();
    }
}