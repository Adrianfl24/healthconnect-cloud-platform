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
import java.text.Normalizer;

@Controller
public class WebController {

    private final MedicRepository medicRepository;
    private final PacientRepository pacientRepository;
    private final ServiciuMedicalRepository serviciuMedicalRepository;
    private final ProgramareRepository programareRepository;
    private final RecenzieRepository recenzieRepository; // <--- Adăugat

    public WebController(MedicRepository medicRepository,
                         PacientRepository pacientRepository,
                         ServiciuMedicalRepository serviciuMedicalRepository,
                         ProgramareRepository programareRepository,
                         RecenzieRepository recenzieRepository) { // <--- Adăugat în constructor
        this.medicRepository = medicRepository;
        this.pacientRepository = pacientRepository;
        this.serviciuMedicalRepository = serviciuMedicalRepository;
        this.programareRepository = programareRepository;
        this.recenzieRepository = recenzieRepository;
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

    // 3. Procesare Logare
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String parola, 
                        HttpSession session,
                        Model model) {
        
        String inputUser = username != null ? username.trim().toLowerCase() : "";
        String inputPass = parola != null ? parola.trim() : "";

        if (inputUser.equalsIgnoreCase("adrianfl24") && inputPass.equals("admin")) {
            session.setAttribute("userRole", "ADMIN");
            return "redirect:/admin/dashboard";
        }

        for (Medic m : medicRepository.findAll()) {
            String dbNume = m.getNume() != null ? m.getNume().trim().toLowerCase() : "";
            String dbEmail = m.getEmail() != null ? m.getEmail().trim().toLowerCase() : "";
            String dbParola = m.getParola() != null ? m.getParola().trim() : "";

            boolean matchUser = dbNume.contains(inputUser) || dbEmail.contains(inputUser);
            boolean matchParola = dbParola.equals(inputPass);

            if (matchUser && matchParola) {
                session.setAttribute("loggedInMedicId", m.getId());
                session.setAttribute("userRole", "MEDIC");
                return "redirect:/medic/dashboard";
            }
        }

        for (Pacient p : pacientRepository.findAll()) {
            String dbNume = p.getNume() != null ? p.getNume().trim().toLowerCase() : "";
            String dbEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
            String dbParola = p.getParola() != null ? p.getParola().trim() : "";

            boolean matchUser = dbNume.contains(inputUser) || dbEmail.contains(inputUser);
            boolean matchParola = dbParola.equals(inputPass);

            if (matchUser && matchParola) {
                session.setAttribute("loggedInPacientId", p.getId());
                session.setAttribute("userRole", "PACIENT");
                return "redirect:/pacient/dashboard";
            }
        }

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
            m.setAprobat(false);
            
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

    @PostMapping("/admin/medic/status")
    public String schimbaStatusMedic(@RequestParam Long medicId, @RequestParam boolean aprobat) {
        Medic medic = medicRepository.findById(medicId).orElse(null);
        if (medic != null) {
            medic.setAprobat(aprobat);
            medicRepository.save(medic);
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/serviciu/sterge")
    public String stergeServiciu(@RequestParam Long serviciuId) {
        serviciuMedicalRepository.deleteById(serviciuId);
        return "redirect:/admin/dashboard";
    }

    private String normalizeazaText(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String textCurat = input.trim().toLowerCase();
        textCurat = Normalizer.normalize(textCurat, Normalizer.Form.NFD);
        return textCurat.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // 6. Portal Dashboard Pacient
    @GetMapping("/pacient/dashboard")
    public String pacientDashboard(HttpSession session, Model model) {
        Long pacientId = (Long) session.getAttribute("loggedInPacientId");
        Pacient pacient = pacientId != null ? pacientRepository.findById(pacientId).orElse(null) 
                                        : pacientRepository.findAll().stream().findFirst().orElse(null);

        var serviciiValide = serviciuMedicalRepository.findAll().stream()
                .filter(s -> {
                    Medic m = s.getMedic();
                    if (m == null || !m.isAprobat()) return false;
                    
                    if (pacient != null && pacient.getOras() != null && !pacient.getOras().isBlank()) {
                        String pTara = normalizeazaText(pacient.getTara());
                        String pJudet = normalizeazaText(pacient.getJudet());
                        String pOras = normalizeazaText(pacient.getOras());
                        
                        String mTara = normalizeazaText(m.getTara());
                        String mJudet = normalizeazaText(m.getJudet());
                        String mOras = normalizeazaText(m.getOras());

                        boolean matchTara = pTara.isEmpty() || pTara.equals(mTara);
                        boolean matchJudet = pJudet.isEmpty() || pJudet.equals(mJudet);
                        boolean matchOras = pOras.equals(mOras);
                        
                        return matchTara && matchJudet && matchOras;
                    }
                    return false;
                })
                .toList();

        var programari = programareRepository.findAll();

        model.addAttribute("pacient", pacient);
        model.addAttribute("servicii", serviciiValide);
        model.addAttribute("programari", programari != null ? programari : java.util.Collections.emptyList());

        return "pacient-dashboard";
    }

    // Pacient: Salvare Profil complet
    @PostMapping("/pacient/upload-profil")
    public String uploadProfilPacient(@RequestParam(value = "nume", required = false) String nume,
                                     @RequestParam(value = "cnp", required = false) String cnp,
                                     @RequestParam(value = "telefon", required = false) String telefon,
                                     @RequestParam(value = "tara", required = false) String tara,
                                     @RequestParam(value = "judet", required = false) String judet,
                                     @RequestParam(value = "oras", required = false) String oras,
                                     @RequestParam(value = "strada", required = false) String strada,
                                     @RequestParam(value = "numar", required = false) String numar,
                                     @RequestParam(value = "bloc", required = false) String bloc,
                                     @RequestParam(value = "scara", required = false) String scara,
                                     @RequestParam(value = "apartament", required = false) String apartament,
                                     @RequestParam(value = "latitudine", required = false) Double latitudine,
                                     @RequestParam(value = "longitudine", required = false) Double longitudine,
                                     HttpSession session) {
        
        Long pacientId = (Long) session.getAttribute("loggedInPacientId");
        Pacient pacient = pacientId != null ? pacientRepository.findById(pacientId).orElse(null) 
                                        : pacientRepository.findAll().stream().findFirst().orElse(null);

        if (pacient != null) {
            if (nume != null && !nume.isBlank()) pacient.setNume(nume.trim());
            if (cnp != null && !cnp.isBlank()) pacient.setCnp(cnp.trim());
            if (telefon != null && !telefon.isBlank()) pacient.setTelefon(telefon.trim());
            if (tara != null && !tara.isBlank()) pacient.setTara(tara.trim());
            if (judet != null && !judet.isBlank()) pacient.setJudet(judet.trim());
            if (oras != null && !oras.isBlank()) pacient.setOras(oras.trim());
            if (strada != null && !strada.isBlank()) pacient.setStrada(strada.trim());
            if (numar != null && !numar.isBlank()) pacient.setNumar(numar.trim());
            if (bloc != null && !bloc.isBlank()) pacient.setBloc(bloc.trim());
            if (scara != null && !scara.isBlank()) pacient.setScara(scara.trim());
            if (apartament != null && !apartament.isBlank()) pacient.setApartament(apartament.trim());
            
            if (latitudine != null) pacient.setLatitudine(latitudine);
            if (longitudine != null) pacient.setLongitudine(longitudine);

            pacientRepository.save(pacient);
        }

        return "redirect:/pacient/dashboard";
    }

    // Pacient: Creare Programare Nouă
    @PostMapping("/pacient/programeaza")
    public String creazaProgramare(@RequestParam Long serviciuId,
                                  @RequestParam String dataOra,
                                  @RequestParam String adresa,
                                  @RequestParam(required = false) String metodaPlata,
                                  HttpSession session) {
        Long pacientId = (Long) session.getAttribute("loggedInPacientId");
        Pacient pacient = pacientId != null ? pacientRepository.findById(pacientId).orElse(null) 
                                        : pacientRepository.findAll().stream().findFirst().orElse(null);
        
        ServiciuMedical serviciu = serviciuMedicalRepository.findById(serviciuId).orElse(null);

        if (pacient != null && serviciu != null) {
            LocalDateTime dataProgramareDorita = LocalDateTime.parse(dataOra);

            boolean existaDeja = programareRepository.findAll().stream()
                    .anyMatch(p -> p.getPacient().getId().equals(pacient.getId()) 
                                && p.getServiciu().getId().equals(serviciu.getId())
                                && p.getDataProgramare().equals(dataProgramareDorita));

            if (existaDeja) {
                return "redirect:/pacient/dashboard?error=duplicate";
            }

            Programare programare = new Programare();
            programare.setDataProgramare(dataProgramareDorita);
            programare.setAdresa(adresa);
            programare.setMetodaPlata(metodaPlata);
            programare.setStatus(StatusProgramare.IN_ASTEPTARE);
            programare.setPacient(pacient);
            programare.setServiciu(serviciu);

            programareRepository.save(programare);
        }
        
        return "redirect:/pacient/dashboard?success=true"; 
    }

    // --- METODĂ NOUĂ: Pacient adaugă recenzie legată de o programare ---
    @PostMapping("/pacient/recenzie/adauga")
    public String adaugaRecenzie(@RequestParam Long programareId,
                                 @RequestParam Integer nota,
                                 @RequestParam String comentariu,
                                 HttpSession session) {
        Programare programare = programareRepository.findById(programareId).orElse(null);

        if (programare != null && programare.getRecenzie() == null) {
            Recenzie recenzie = new Recenzie(nota, comentariu, programare);
            recenzieRepository.save(recenzie);
        }

        return "redirect:/pacient/dashboard?success=recenzie";
    }

    // 7. Portal Dashboard Medic
    @GetMapping("/medic/dashboard")
    public String medicDashboard(HttpSession session, Model model) {
        Long medicId = (Long) session.getAttribute("loggedInMedicId");
        Medic medic = medicId != null ? medicRepository.findById(medicId).orElse(null) 
                                    : medicRepository.findAll().stream().findFirst().orElse(null);

        var programari = programareRepository.findAll();
        var servicii = serviciuMedicalRepository.findAll();
        
        // Preluăm recenziile primite de acest medic
        var recenzii = medic != null ? recenzieRepository.findByProgramareServiciuMedicId(medic.getId()) : java.util.Collections.emptyList();

        model.addAttribute("medic", medic);
        model.addAttribute("programari", programari != null ? programari : java.util.Collections.emptyList());
        model.addAttribute("servicii", servicii != null ? servicii : java.util.Collections.emptyList());
        model.addAttribute("recenzii", recenzii); // <--- Trimis în pagină pentru medic

        return "medic-dashboard";
    }

    // Medic: Salvare profil complet
    @PostMapping("/medic/upload-document")
    public String uploadDocumentMedic(@RequestParam(value = "nume", required = false) String nume,
                                     @RequestParam(value = "cnp", required = false) String cnp,
                                     @RequestParam(value = "telefon", required = false) String telefon,
                                     @RequestParam(value = "codParafa", required = false) String codParafa,
                                     @RequestParam(value = "tara", required = false) String tara,
                                     @RequestParam(value = "judet", required = false) String judet,
                                     @RequestParam(value = "oras", required = false) String oras,
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
            if (tara != null && !tara.isBlank()) medic.setTara(tara.trim());
            if (judet != null && !judet.isBlank()) medic.setJudet(judet.trim());
            if (oras != null && !oras.isBlank()) medic.setOras(oras.trim());

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

    // Servire fișiere din folderul uploads
    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads").resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

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

    // Medic: Adăugare Serviciu
    @PostMapping("/medic/serviciu/adauga")
    public String adaugaServiciu(@RequestParam String nume,
                                @RequestParam String descriere,
                                @RequestParam Double pret,
                                @RequestParam Integer durataMinute,
                                HttpSession session) {
        
        Long medicId = (Long) session.getAttribute("loggedInMedicId");
        Medic medic = medicId != null ? medicRepository.findById(medicId).orElse(null) 
                                    : medicRepository.findAll().stream().findFirst().orElse(null);
        
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
            if ("ANULAT".equalsIgnoreCase(status) || "ANULATA".equalsIgnoreCase(status)) {
                p.setStatus(StatusProgramare.ANULATA);
            } else if ("ACCEPTATA".equalsIgnoreCase(status) || "CONFIRMAT".equalsIgnoreCase(status)) {
                p.setStatus(StatusProgramare.ACCEPTATA);
            } else {
                p.setStatus(StatusProgramare.valueOf(status));
            }
            programareRepository.save(p);
        }
        return "redirect:/medic/dashboard";
    }

    // Medic: Ștergere Serviciu Propriu din Portofoliu
    @PostMapping("/medic/serviciu/sterge")
    public String stergeServiciuMedic(@RequestParam Long serviciuId, HttpSession session) {
        Long medicId = (Long) session.getAttribute("loggedInMedicId");
        
        ServiciuMedical serviciu = serviciuMedicalRepository.findById(serviciuId).orElse(null);
        if (serviciu != null && serviciu.getMedic() != null && serviciu.getMedic().getId().equals(medicId)) {
            serviciuMedicalRepository.deleteById(serviciuId);
        }
        
        return "redirect:/medic/dashboard";
    }
}