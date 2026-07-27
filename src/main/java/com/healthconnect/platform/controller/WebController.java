package com.healthconnect.platform.controller;

import com.healthconnect.platform.model.*;
import com.healthconnect.platform.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class WebController {

    private final MedicRepository medicRepository;
    private final PacientRepository pacientRepository;
    private final ServiciuMedicalRepository serviciuMedicalRepository;
    private final ProgramareRepository programareRepository;

    public WebController(MedicRepository medicRepository,
                         PacientRepository pacientRepository,
                         ServiciuMedicalRepository serviciuMedicalRepository,
                         ProgramareRepository programareRepository) {
        this.medicRepository = medicRepository;
        this.pacientRepository = pacientRepository;
        this.serviciuMedicalRepository = serviciuMedicalRepository;
        this.programareRepository = programareRepository;
    }

    // 1. Pagina Principală (Landing Page cu Login & Register)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 2. Procesare Logare
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String parola, 
                        Model model) {
        
        // Verificare specială cont Admin Suprem
        if (username.equals("adrianfl24") && parola.equals("admin")) {
            return "redirect:/admin/dashboard";
        }

        // Căutare în lista de Medici
        boolean esteMedic = medicRepository.findAll().stream()
                .anyMatch(m -> (m.getEmail().equalsIgnoreCase(username) || m.getNume().equalsIgnoreCase(username)) 
                        && m.getParola().equals(parola));

        if (esteMedic) {
            return "redirect:/medic/dashboard";
        }

        // Căutare în lista de Pacienți
        boolean estePacient = pacientRepository.findAll().stream()
                .anyMatch(p -> (p.getEmail().equalsIgnoreCase(username) || p.getNume().equalsIgnoreCase(username)) 
                        && p.getParola().equals(parola));

        if (estePacient) {
            return "redirect:/pacient/dashboard";
        }

        // Dacă datele introduse sunt incorecte
        model.addAttribute("error", true);
        return "index";
    }

    // 3. Procesare Înregistrare Cont Nou
    @PostMapping("/register")
    public String register(@RequestParam String nume,
                           @RequestParam String email,
                           @RequestParam String parola,
                           @RequestParam String rol) {

        if (rol.equals("ADMIN") || (nume.equalsIgnoreCase("adrianfl24") && parola.equals("admin"))) {
            return "redirect:/admin/dashboard";
        } else if (rol.equals("MEDIC")) {
            Medic m = new Medic();
            m.setNume(nume);
            m.setEmail(email);
            m.setParola(parola);
            m.setRol(Role.MEDIC);
            m.setSpecializare("Medicină Generală");
            m.setCodParafa("PARAF" + (System.currentTimeMillis() % 10000));
            medicRepository.save(m);
            return "redirect:/medic/dashboard";
        } else {
            Pacient p = new Pacient();
            p.setNume(nume);
            p.setEmail(email);
            p.setParola(parola);
            p.setRol(Role.PACIENT);
            pacientRepository.save(p);
            return "redirect:/pacient/dashboard";
        }
    }

    // 4. Panou Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalPacienti", pacientRepository.count());
        model.addAttribute("totalMedici", medicRepository.count());
        model.addAttribute("totalProgramari", programareRepository.count());
        return "admin-dashboard";
    }

    // 5. Portal Dashboard Pacient
    @GetMapping("/pacient/dashboard")
    public String pacientDashboard(Model model) {
        var servicii = serviciuMedicalRepository.findAll();
        var programari = programareRepository.findAll();
        var medici = medicRepository.findAll();

        model.addAttribute("servicii", servicii != null ? servicii : java.util.Collections.emptyList());
        model.addAttribute("programari", programari != null ? programari : java.util.Collections.emptyList());
        model.addAttribute("medici", medici != null ? medici : java.util.Collections.emptyList());

        return "pacient-dashboard";
    }

    // Pacient: Creare Programare Nouă
    @PostMapping("/pacient/programeaza")
    public String creazaProgramare(@RequestParam Long serviciuId,
                                  @RequestParam String dataOra,
                                  @RequestParam String adresa) {
        ServiciuMedical serviciu = serviciuMedicalRepository.findById(serviciuId).orElse(null);
        Pacient pacient = pacientRepository.findAll().stream().findFirst().orElse(null);

        if (pacient != null && serviciu != null) {
            Programare programare = new Programare(
                    LocalDateTime.parse(dataOra),
                    adresa,
                    StatusProgramare.IN_ASTEPTARE,
                    pacient,
                    serviciu
            );
            programareRepository.save(programare);
        }
        return "redirect:/pacient/dashboard";
    }

    // Pacient: Adăugare Recenzie Medic
    @PostMapping("/pacient/recenzie/adauga")
    public String adaugaRecenzie(@RequestParam Long medicId,
                                @RequestParam Integer nota,
                                @RequestParam String comentariu) {
        return "redirect:/pacient/dashboard";
    }

    // 6. Portal Dashboard Medic
    @GetMapping("/medic/dashboard")
    public String medicDashboard(Model model) {
        var programari = programareRepository.findAll();
        var servicii = serviciuMedicalRepository.findAll();

        model.addAttribute("programari", programari != null ? programari : java.util.Collections.emptyList());
        model.addAttribute("servicii", servicii != null ? servicii : java.util.Collections.emptyList());

        return "medic-dashboard";
    }

    // Medic: Adăugare Serviciu Medical Nou
    @PostMapping("/medic/serviciu/adauga")
    public String adaugaServiciu(@RequestParam String nume,
                                @RequestParam String descriere,
                                @RequestParam Double pret,
                                @RequestParam Integer durataMinute) {
        Medic medic = medicRepository.findAll().stream().findFirst().orElse(null);
        if (medic != null) {
            ServiciuMedical s = new ServiciuMedical(
                    nume,
                    descriere,
                    pret.floatValue(),
                    durataMinute,
                    medic
            );
            serviciuMedicalRepository.save(s);
        }
        return "redirect:/medic/dashboard";
    }

    // Medic: Schimbare Status Programare (Acceptă/Respinge)
    @PostMapping("/medic/programare/status")
    public String schimbaStatus(@RequestParam Long programareId, @RequestParam String status) {
        Programare p = programareRepository.findById(programareId).orElse(null);
        if (p != null) {
            p.setStatus(StatusProgramare.valueOf(status));
            programareRepository.save(p);
        }
        return "redirect:/medic/dashboard";
    }
}