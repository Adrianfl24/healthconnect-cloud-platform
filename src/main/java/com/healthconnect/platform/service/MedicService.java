package com.healthconnect.platform.service;

import com.healthconnect.platform.model.Medic;
import com.healthconnect.platform.model.Role;
import com.healthconnect.platform.model.ServiciuMedical;
import com.healthconnect.platform.repository.MedicRepository;
import com.healthconnect.platform.repository.ServiciuMedicalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicService {

    private final MedicRepository medicRepository;
    private final ServiciuMedicalRepository serviciuMedicalRepository;

    public MedicService(MedicRepository medicRepository, ServiciuMedicalRepository serviciuMedicalRepository) {
        this.medicRepository = medicRepository;
        this.serviciuMedicalRepository = serviciuMedicalRepository;
    }

    // UC7: Inregistrare Cadru Medical
    public Medic inregistreazaMedic(Medic medic) {
        medic.setRol(Role.MEDIC);
        return medicRepository.save(medic);
    }

    public List<Medic> obtineTotiMedicii() {
        return medicRepository.findAll();
    }

    // UC8: Adaugare Serviciu Medical oferit de medic
    public ServiciuMedical adaugaServiciu(Long medicId, ServiciuMedical serviciu) {
        Medic medic = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medicul nu a fost gasit!"));
        serviciu.setMedic(medic);
        return serviciuMedicalRepository.save(serviciu);
    }

    // UC2: Cautare servicii medicale
    public List<ServiciuMedical> obtineToateServiciile() {
        return serviciuMedicalRepository.findAll();
    }
}