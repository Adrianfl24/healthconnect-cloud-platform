package com.healthconnect.platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_services")
public class ServiciuMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String denumire;

    @Column(columnDefinition = "TEXT")
    private String descriere;

    @Column(nullable = false)
    private Float pret;

    private Integer durataMinute;

    // Relatia de apartenenta la un Cadru Medical (Many Services to One Medic)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medic_id", nullable = false)
    private Medic medic;

    public ServiciuMedical() {}

    public ServiciuMedical(String denumire, String descriere, Float pret, Integer durataMinute, Medic medic) {
        this.denumire = denumire;
        this.descriere = descriere;
        this.pret = pret;
        this.durataMinute = durataMinute;
        this.medic = medic;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public Float getPret() { return pret; }
    public void setPret(Float pret) { this.pret = pret; }

    public Integer getDurataMinute() { return durataMinute; }
    public void setDurataMinute(Integer durataMinute) { this.durataMinute = durataMinute; }

    public Medic getMedic() { return medic; }
    public void setMedic(Medic medic) { this.medic = medic; }
}