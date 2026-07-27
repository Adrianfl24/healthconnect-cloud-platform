package com.healthconnect.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Medic extends Utilizator {
    
    private String specializare;
    
    @Column(unique = true)
    private String codParafa;

    public Medic() {}

    public Medic(String email, String parola, String nume, String prenume, String telefon, Role rol, String specializare, String codParafa) {
        super(email, parola, nume, prenume, telefon, rol);
        this.specializare = specializare;
        this.codParafa = codParafa;
    }

    public String getSpecializare() { return specializare; }
    public void setSpecializare(String specializare) { this.specializare = specializare; }
    public String getCodParafa() { return codParafa; }
    public void setCodParafa(String codParafa) { this.codParafa = codParafa; }
}