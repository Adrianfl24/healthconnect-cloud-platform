package com.healthconnect.platform.controller;

import com.healthconnect.platform.model.Medic;
import com.healthconnect.platform.model.ServiciuMedical;
import com.healthconnect.platform.service.MedicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medici")
public class MedicController {

    private final MedicService medicService;

    public MedicController(MedicService medicService) {
        this.medicService = medicService;
    }

    // UC7: Înregistrare Cadru Medical
    @PostMapping("/inregistrare")
    public ResponseEntity<Medic> inregistrare(@RequestBody Medic medic) {
        Medic medicSalvat = medicService.inregistreazaMedic(medic);
        return new ResponseEntity<>(medicSalvat, HttpStatus.CREATED);
    }

    // Listing cadre medicale
    @GetMapping
    public ResponseEntity<List<Medic>> getTotiMedicii() {
        return ResponseEntity.ok(medicService.obtineTotiMedicii());
    }

    // UC8: Adăugare serviciu medical de către un medic
    @PostMapping("/{medicId}/servicii")
    public ResponseEntity<ServiciuMedical> adaugaServiciu(@PathVariable Long medicId, 
                                                          @RequestBody ServiciuMedical serviciu) {
        ServiciuMedical serviciuSalvat = medicService.adaugaServiciu(medicId, serviciu);
        return new ResponseEntity<>(serviciuSalvat, HttpStatus.CREATED);
    }

    // UC2: Căutare/Listare toate serviciile disponibile
    @GetMapping("/servicii")
    public ResponseEntity<List<ServiciuMedical>> getToateServiciile() {
        return ResponseEntity.ok(medicService.obtineToateServiciile());
    }
}