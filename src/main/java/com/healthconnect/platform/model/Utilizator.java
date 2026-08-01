package com.healthconnect.platform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilizator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String parola;
    private String nume;
    private String prenume;
    private String telefon;
    private String cnp;
    
    @Enumerated(EnumType.STRING)
    private Role rol;

    // Constructori
    public Utilizator() {}

    public Utilizator(String email, String parola, String nume, String prenume, String telefon, Role rol,String cnp) {
        this.email = email;
        this.parola = parola;
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.rol = rol;
         this.cnp = cnp;
    }

    // Getters și Setters
      public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public Role getRol() { return rol; }
    public void setRol(Role rol) { this.rol = rol; }
}