package com.healthconnect.platform.controller;

import com.healthconnect.platform.model.Pacient;
import com.healthconnect.platform.service.PacientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacienti")
public class PacientController {

    private final PacientService pacientService;

    public PacientController(PacientService pacientService) {
        this.pacientService = pacientService;
    }

    // UC1: Înregistrare Pacient
    @PostMapping("/inregistrare")
    public ResponseEntity<Pacient> inregistrare(@RequestBody Pacient pacient) {
        Pacient pacientSalvat = pacientService.inregistreazaPacient(pacient);
        return new ResponseEntity<>(pacientSalvat, HttpStatus.CREATED);
    }

    // Obținere lista tuturor pacienților
    @GetMapping
    public ResponseEntity<List<Pacient>> getTotiPacientii() {
        return ResponseEntity.ok(pacientService.obtineTotiPacientii());
    }

    // Obținere pacient după ID
    @GetMapping("/{id}")
    public ResponseEntity<Pacient> getPacientDupaId(@PathVariable Long id) {
        return pacientService.obtinePacientDupaId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}