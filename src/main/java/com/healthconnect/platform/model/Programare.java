package com.healthconnect.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Programare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataProgramare;

    @Column(nullable = false)
    private String adresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProgramare status;

    // Relatia cu Pacientul
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pacient_id", nullable = false)
    private Pacient pacient;

    // Relatia cu Serviciul Medical
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviciu_id", nullable = false)
    private ServiciuMedical serviciu;

    public Programare() {}

    public Programare(LocalDateTime dataProgramare, String adresa, StatusProgramare status, Pacient pacient, ServiciuMedical serviciu) {
        this.dataProgramare = dataProgramare;
        this.adresa = adresa;
        this.status = status;
        this.pacient = pacient;
        this.serviciu = serviciu;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataProgramare() { return dataProgramare; }
    public void setDataProgramare(LocalDateTime dataProgramare) { this.dataProgramare = dataProgramare; }

    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }

    public StatusProgramare getStatus() { return status; }
    public void setStatus(StatusProgramare status) { this.status = status; }

    public Pacient getPacient() { return pacient; }
    public void setPacient(Pacient pacient) { this.pacient = pacient; }

    public ServiciuMedical getServiciu() { return serviciu; }
    public void setServiciu(ServiciuMedical serviciu) { this.serviciu = serviciu; }
}