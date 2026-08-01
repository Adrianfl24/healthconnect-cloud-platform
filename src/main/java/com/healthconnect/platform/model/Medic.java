package com.healthconnect.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Medic extends Utilizator {
    
    private String specializare;
    
    @Column(unique = true)
    private String codParafa;
    private String diplomaPath; // Calea către fișierul încărcat
    private boolean aprobat = false; // Adminul trebuie să bifeze true

    private String tara;
    private String judet;
    private String oras;

    public Medic() {}

    public Medic(String email, String parola, String nume, String prenume, String telefon, Role rol,String cnp, String specializare, String codParafa, String diplomaPath,boolean aprobat,String tara, String judet, String oras) {
        super(email, parola, nume, prenume, telefon, rol,cnp);
        this.specializare = specializare;
        this.codParafa = codParafa;
        this.diplomaPath = diplomaPath;
        this.aprobat = aprobat;
        this.tara = tara;
        this.judet = judet;
        this.oras = oras;
    }

    public String getSpecializare() { return specializare; }
    public void setSpecializare(String specializare) { this.specializare = specializare; }
    public String getCodParafa() { return codParafa; }
    public void setCodParafa(String codParafa) { this.codParafa = codParafa; }
    public String getDiplomaPath() { return diplomaPath; }
public void setDiplomaPath(String diplomaPath) { this.diplomaPath = diplomaPath; }

public boolean isAprobat() { return aprobat; }
public void setAprobat(boolean aprobat) { this.aprobat = aprobat; }
public String getTara() { return tara; }
    public void setTara(String tara) { this.tara = tara; }

    public String getJudet() { return judet; }
    public void setJudet(String judet) { this.judet = judet; }

    public String getOras() { return oras; }
    public void setOras(String oras) { this.oras = oras; }
}