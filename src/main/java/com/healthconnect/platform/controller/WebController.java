package com.healthconnect.platform.controller;

import com.healthconnect.platform.model.*;
import com.healthconnect.platform.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    // 1. Pagina Principală (Landing Page)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 2. Afișare Pagina de Login / Înregistrare
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        return "login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // 3. Procesare Logare (Versiune Bulletproof + Debugging in Consola)
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String parola, 
                        HttpSession session,
                        Model model) {
        
        // Curățăm spațiile goale accidentale de la început și final
        String inputUser = username != null ? username.trim().toLowerCase() : "";
        String inputPass = parola != null ? parola.trim() : "";

        System.out.println("\n=== INCERCARE DE LOGARE ===");
        System.out.println("Ai introdus user: [" + inputUser + "]");
        System.out.println("Ai introdus parola: [" + inputPass + "]");

        // 1. Verificare Admin Suprem
        if (inputUser.equalsIgnoreCase("adrianfl24") && inputPass.equals("admin")) {
            session.setAttribute("userRole", "ADMIN");
            System.out.println("-> Logare ADMIN cu succes!");
            return "redirect:/admin/dashboard";
        }

        // 2. Căutare în lista de Medici
        for (Medic m : medicRepository.findAll()) {
            String dbNume = m.getNume() != null ? m.getNume().trim().toLowerCase() : "";
            String dbEmail = m.getEmail() != null ? m.getEmail().trim().toLowerCase() : "";
            String dbParola = m.getParola() != null ? m.getParola().trim() : "";

            System.out.println("Verific Medic in DB -> Nume: [" + dbNume + "], Email: [" + dbEmail + "], Parola: [" + dbParola + "]");

            boolean matchUser = dbNume.contains(inputUser) || dbEmail.contains(inputUser);
            boolean matchParola = dbParola.equals(inputPass);

            if (matchUser && matchParola) {
                session.setAttribute("loggedInMedicId", m.getId());
                session.setAttribute("userRole", "MEDIC");
                System.out.println("-> Logare MEDIC cu succes!");
                return "redirect:/medic/dashboard";
            }
        }

        // 3. Căutare în lista de Pacienți
        for (Pacient p : pacientRepository.findAll()) {
            String dbNume = p.getNume() != null ? p.getNume().trim().toLowerCase() : "";
            String dbEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
            String dbParola = p.getParola() != null ? p.getParola().trim() : "";

            System.out.println("Verific Pacient in DB -> Nume: [" + dbNume + "], Email: [" + dbEmail + "], Parola: [" + dbParola + "]");

            boolean matchUser = dbNume.contains(inputUser) || dbEmail.contains(inputUser);
            boolean matchParola = dbParola.equals(inputPass);

            if (matchUser && matchParola) {
                session.setAttribute("loggedInPacientId", p.getId());
                session.setAttribute("userRole", "PACIENT");
                System.out.println("-> Logare PACIENT cu succes!");
                return "redirect:/pacient/dashboard";
            }
        }

        // Dacă nicio condiție nu a fost îndeplinită
        System.out.println("-> LOGARE ESUATA: Nu s-a gasit nicio potrivire intre ce ai scris si baza de date.");
        model.addAttribute("error", true);
        return "login";
    }

    // 4. Procesare Înregistrare Cont Nou
    @PostMapping("/register")
    public String register(@RequestParam String nume,
                           @RequestParam String email,
                           @RequestParam String parola,
                           @RequestParam String rol,
                           HttpSession session) {

        String roleClean = rol != null ? rol.trim().toUpperCase() : "PACIENT";

        if (roleClean.equals("ADMIN") || (nume.equalsIgnoreCase("adrianfl24") && parola.equals("admin"))) {
            session.setAttribute("userRole", "ADMIN");
            return "redirect:/admin/dashboard";
        } else if (roleClean.equals("MEDIC")) {
            Medic m = new Medic();
            m.setNume(nume.trim());
            m.setEmail(email.trim());
            m.setParola(parola.trim());
            m.setRol(Role.MEDIC);
            m.setSpecializare("Medicină Generală");
            // AICI AM ȘTERS GENERAREA AUTOMATĂ A CODULUI DE PARAFĂ
            m.setAprobat(false); // Contul rămâne în așteptarea validării de către Admin
            
            Medic medicSalvat = medicRepository.save(m);
            session.setAttribute("loggedInMedicId", medicSalvat.getId());
            session.setAttribute("userRole", "MEDIC");
            
            return "redirect:/medic/dashboard";
        } else {
            Pacient p = new Pacient();
            p.setNume(nume.trim());
            p.setEmail(email.trim());
            p.setParola(parola.trim());
            p.setRol(Role.PACIENT);
            
            Pacient pacientSalvat = pacientRepository.save(p);
            session.setAttribute("loggedInPacientId", pacientSalvat.getId());
            session.setAttribute("userRole", "PACIENT");
            
            return "redirect:/pacient/dashboard";
        }
    }

    // 5. Panou Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        var medici = medicRepository.findAll();
        var servicii = serviciuMedicalRepository.findAll();
        var pacienti = pacientRepository.findAll();
        var programari = programareRepository.findAll();

        model.addAttribute("totalPacienti", pacienti.size());
        model.addAttribute("totalMedici", medici.size());
        model.addAttribute("totalServicii", servicii.size());
        model.addAttribute("totalProgramari", programari.size());

        model.addAttribute("medici", medici);
        model.addAttribute("servicii", servicii);

        return "admin-dashboard";
    }

    // Admin: Schimbare Status Validare Medic (Aprobă / Revocă)
    @PostMapping("/admin/medic/status")
    public String schimbaStatusMedic(@RequestParam Long medicId, @RequestParam boolean aprobat) {
        Medic medic = medicRepository.findById(medicId).orElse(null);
        if (medic != null) {
            medic.setAprobat(aprobat);
            medicRepository.save(medic);
        }
        return "redirect:/admin/dashboard";
    }

    // Admin: Ștergere / Anulare Serviciu Medical
    @PostMapping("/admin/serviciu/sterge")
    public String stergeServiciu(@RequestParam Long serviciuId) {
        serviciuMedicalRepository.deleteById(serviciuId);
        return "redirect:/admin/dashboard";
    }

    // 6. Portal Dashboard Pacient
    // 6. Portal Dashboard Pacient (Filtrat după Țară, Județ și Oraș)
    @GetMapping("/pacient/dashboard")
    public String pacientDashboard(HttpSession session, Model model) {
        Long pacientId = (Long) session.getAttribute("loggedInPacientId");
        Pacient pacient = pacientId != null ? pacientRepository.findById(pacientId).orElse(null) 
                                           : pacientRepository.findAll().stream().findFirst().orElse(null);

        // Filtrăm serviciile: medicul trebuie să fie aprobat ȘI să fie din aceeași țară, județ și oraș ca pacientul
        var serviciiValide = serviciuMedicalRepository.findAll().stream()
                .filter(s -> {
                    Medic m = s.getMedic();
                    if (m == null || !m.isAprobat()) return false;
                    
                    // Verificăm dacă pacientul și-a setat locația; dacă da, facem filtrarea
                    if (pacient != null && pacient.getOras() != null && !pacient.getOras().isBlank()) {
                        boolean matchTara = pacient.getTara() == null || pacient.getTara().equalsIgnoreCase(m.getTara());
                        boolean matchJudet = pacient.getJudet() == null || pacient.getJudet().equalsIgnoreCase(m.getJudet());
                        boolean matchOras = pacient.getOras().equalsIgnoreCase(m.getOras());
                        return matchTara && matchJudet && matchOras;
                    }
                    
                    // Dacă pacientul nu și-a completat încă profilul/orasul, nu afișăm nimic sau lăsăm gol până își setează locatia
                    return false;
                })
                .toList();

        var programari = programareRepository.findAll();

        model.addAttribute("pacient", pacient);
        model.addAttribute("servicii", serviciiValide);
        model.addAttribute("programari", programari != null ? programari : java.util.Collections.emptyList());

        return "pacient-dashboard";
    }

    // Pacient: Creare Programare Nouă
    @PostMapping("/pacient/programeaza")
    public String creazaProgramare(@RequestParam Long serviciuId,
                                  @RequestParam String dataOra,
                                  @RequestParam String adresa,
                                  HttpSession session) {
        Long pacientId = (Long) session.getAttribute("loggedInPacientId");
        Pacient pacient = pacientId != null ? pacientRepository.findById(pacientId).orElse(null) 
                                           : pacientRepository.findAll().stream().findFirst().orElse(null);
        
        ServiciuMedical serviciu = serviciuMedicalRepository.findById(serviciuId).orElse(null);

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

    // 7. Portal Dashboard Medic
    @GetMapping("/medic/dashboard")
    public String medicDashboard(HttpSession session, Model model) {
        Long medicId = (Long) session.getAttribute("loggedInMedicId");
        Medic medic = medicId != null ? medicRepository.findById(medicId).orElse(null) 
                                      : medicRepository.findAll().stream().findFirst().orElse(null);

        var programari = programareRepository.findAll();
        var servicii = serviciuMedicalRepository.findAll();

        model.addAttribute("medic", medic);
        model.addAttribute("programari", programari != null ? programari : java.util.Collections.emptyList());
        model.addAttribute("servicii", servicii != null ? servicii : java.util.Collections.emptyList());

        return "medic-dashboard";
    }

    // Medic: Salvare profil complet (Nume, CNP, Telefon, Cod Parafă) și diplomă pe disc
    @PostMapping("/medic/upload-document")
    public String uploadDocumentMedic(@RequestParam(value = "nume", required = false) String nume,
                                     @RequestParam(value = "cnp", required = false) String cnp,
                                     @RequestParam(value = "telefon", required = false) String telefon,
                                     @RequestParam(value = "codParafa", required = false) String codParafa,
                                     @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
                                     HttpSession session) {
        
        Long medicId = (Long) session.getAttribute("loggedInMedicId");
        Medic medic = medicId != null ? medicRepository.findById(medicId).orElse(null) 
                                      : medicRepository.findAll().stream().findFirst().orElse(null);

        if (medic != null) {
            if (nume != null && !nume.isBlank()) medic.setNume(nume.trim());
            if (cnp != null && !cnp.isBlank()) medic.setCnp(cnp.trim());
            if (telefon != null && !telefon.isBlank()) medic.setTelefon(telefon.trim());
            if (codParafa != null && !codParafa.isBlank()) medic.setCodParafa(codParafa.trim());

            if (documentFile != null && !documentFile.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + documentFile.getOriginalFilename();
                    
                    Path uploadPath = Paths.get("uploads");
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(documentFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    medic.setDiplomaPath(fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            medicRepository.save(medic);
        }

        return "redirect:/medic/dashboard";
    }

    // Servire fișiere din folderul uploads direct în browser (Corectat pentru a afișa imagini/PDF-uri)
    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads").resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                // 1. Detectăm automat tipul fișierului (image/png, image/jpeg, application/pdf etc.)
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream"; // fallback generic
                }

                // 2. Trimitem fișierul cu "Content-Type"-ul corect, ca browserul să știe să-l randeze
                return org.springframework.http.ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    // Medic: Adăugare Serviciu Medical Nou (DOAR DACA ESTE APROBAT)
    @PostMapping("/medic/serviciu/adauga")
    public String adaugaServiciu(@RequestParam String nume,
                                @RequestParam String descriere,
                                @RequestParam Double pret,
                                @RequestParam Integer durataMinute,
                                HttpSession session) {
        
        Long medicId = (Long) session.getAttribute("loggedInMedicId");
        Medic medic = medicId != null ? medicRepository.findById(medicId).orElse(null) 
                                      : medicRepository.findAll().stream().findFirst().orElse(null);
        
        // SECURITATE: Salvăm serviciul doar dacă medicul a fost validat
        if (medic != null && medic.isAprobat()) {
            ServiciuMedical s = new ServiciuMedical();
            s.setDenumire(nume.trim());
            s.setDescriere(descriere.trim());
            s.setPret(pret.floatValue());
            s.setDurataMinute(durataMinute);
            s.setMedic(medic);

            serviciuMedicalRepository.save(s);
        }
        return "redirect:/medic/dashboard";
    }

    // Medic: Schimbare Status Programare
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