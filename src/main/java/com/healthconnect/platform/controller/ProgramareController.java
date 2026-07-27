package com.healthconnect.platform.controller;

import com.healthconnect.platform.model.Programare;
import com.healthconnect.platform.model.StatusProgramare;
import com.healthconnect.platform.service.ProgramareService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/programari")
public class ProgramareController {

    private final ProgramareService programareService;

    public ProgramareController(ProgramareService programareService) {
        this.programareService = programareService;
    }

    // UC4: Creare Programare Nouă
    @PostMapping
    public ResponseEntity<Programare> creeazaProgramare(
            @RequestParam Long pacientId,
            @RequestParam Long serviciuId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data,
            @RequestParam String adresa) {

        Programare programare = programareService.creeazaProgramare(pacientId, serviciuId, data, adresa);
        return new ResponseEntity<>(programare, HttpStatus.CREATED);
    }

    // UC10: Schimbare status (ACCEPTATA, FINALIZATA, ANULATA)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Programare> actualizeazaStatus(@PathVariable Long id, 
                                                          @RequestParam StatusProgramare status) {
        Programare programareActualizata = programareService.actualizeazaStatus(id, status);
        return ResponseEntity.ok(programareActualizata);
    }

    // Vizualizare toate programările
    @GetMapping
    public ResponseEntity<List<Programare>> getToateProgramarile() {
        return ResponseEntity.ok(programareService.obtineToateProgramarile());
    }
}