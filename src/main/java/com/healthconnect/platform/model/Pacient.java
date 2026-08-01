package com.healthconnect.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Pacient extends Utilizator {
    
    @Column(unique = true)

    
    private String istoricMedical;

    // Câmpuri detaliate adresă domiciliu
    private String tara;
    private String judet;
    private String oras;
    private String strada;
    private String numar;
    private String bloc;
    private String scara;
    private String apartament;

    // Coordonate pentru harta interactivă (Pin-ul pus de pacient)
    private Double latitudine;
    private Double longitudine;



    public Pacient() {}

    public Pacient(String email, String parola, String nume, String prenume, String telefon, Role rol, String cnp, String istoricMedical,String tara, String judet,String oras,String strada, String numar,String bloc,String scara, String apartament
    ,Double latitudine,Double longitudine) {
        super(email, parola, nume, prenume, telefon, rol,cnp);
        this.istoricMedical = istoricMedical;
       this.tara = tara;
       this.judet = judet;
       this.oras = oras;
       this.strada = strada;
       this.numar = numar;
       this.bloc = bloc;
       this.scara = scara;
       this.apartament = apartament;
       this.latitudine = latitudine;
       this.longitudine = longitudine;
    }

 
    public String getIstoricMedical() { return istoricMedical; }
    public void setIstoricMedical(String istoricMedical) { this.istoricMedical = istoricMedical; }
    public String getTara() { return tara; }
    public void setTara(String tara) { this.tara = tara; }

    public String getJudet() { return judet; }
    public void setJudet(String judet) { this.judet = judet; }

    public String getOras() { return oras; }
    public void setOras(String oras) { this.oras = oras; }

    public String getStrada() { return strada; }
    public void setStrada(String strada) { this.strada = strada; }

    public String getNumar() { return numar; }
    public void setNumar(String numar) { this.numar = numar; }

    public String getBloc() { return bloc; }
    public void setBloc(String bloc) { this.bloc = bloc; }

    public String getScara() { return scara; }
    public void setScara(String scara) { this.scara = scara; }

    public String getApartament() { return apartament; }
    public void setApartament(String apartament) { this.apartament = apartament; }

    public Double getLatitudine() { return latitudine; }
    public void setLatitudine(Double latitudine) { this.latitudine = latitudine; }

    public Double getLongitudine() { return longitudine; }
    public void setLongitudine(Double longitudine) { this.longitudine = longitudine; }
}
    
