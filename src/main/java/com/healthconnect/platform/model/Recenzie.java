package com.healthconnect.platform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Recenzie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer nota; // ex: 1 - 5 stele

    @Column(columnDefinition = "TEXT")
    private String comentariu;

    // Relatie One-to-One cu Programarea (O programare are o singura recenzie)
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Programare programare;

    public Recenzie() {}

    public Recenzie(Integer nota, String comentariu, Programare programare) {
        this.nota = nota;
        this.comentariu = comentariu;
        this.programare = programare;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentariu() { return comentariu; }
    public void setComentariu(String comentariu) { this.comentariu = comentariu; }

    public Programare getProgramare() { return programare; }
    public void setProgramare(Programare programare) { this.programare = programare; }
}