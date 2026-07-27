package com.healthconnect.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Pacient extends Utilizator {
    
    @Column(unique = true)
    private String cnp;
    
    private String istoricMedical;

    public Pacient() {}

    public Pacient(String email, String parola, String nume, String prenume, String telefon, Role rol, String cnp, String istoricMedical) {
        super(email, parola, nume, prenume, telefon, rol);
        this.cnp = cnp;
        this.istoricMedical = istoricMedical;
    }

    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }
    public String getIstoricMedical() { return istoricMedical; }
    public void setIstoricMedical(String istoricMedical) { this.istoricMedical = istoricMedical; }
    
}