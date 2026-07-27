package com.healthconnect.platform.service;

import com.healthconnect.platform.model.Pacient;
import com.healthconnect.platform.model.Role;
import com.healthconnect.platform.repository.PacientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacientService {

    private final PacientRepository pacientRepository;

    public PacientService(PacientRepository pacientRepository) {
        this.pacientRepository = pacientRepository;
    }

    // UC1: Inregistrare Pacient
    public Pacient inregistreazaPacient(Pacient pacient) {
        pacient.setRol(Role.PACIENT);
        return pacientRepository.save(pacient);
    }

    public List<Pacient> obtineTotiPacientii() {
        return pacientRepository.findAll();
    }

    public Optional<Pacient> obtinePacientDupaId(Long id) {
        return pacientRepository.findById(id);
    }
}