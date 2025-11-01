/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

/**
 *
 * @author ZAHRA
 */
public class Ogrenci {
     private String isim, soyad;
    private int ogrNo, bolumSira, sinifSira, sinif;
    private float gano;
    private char cinsiyet;

    public Ogrenci(String isim, String soyad, int ogrNo, float gano, int bolumSira, int sinifSira, int sinif, char cinsiyet) {
        this.isim = isim;
        this.soyad = soyad;
        this.ogrNo = ogrNo;
        this.gano = gano;
        this.bolumSira = bolumSira;
        this.sinifSira = sinifSira;
        this.sinif = sinif;
        this.cinsiyet = cinsiyet;
    }

    // Getter ve Setter
    public String getIsim() { return isim; }
    public String getSoyad() { return soyad; }
    public int getOgrNo() { return ogrNo; }
    public float getGano() { return gano; }
    public int getBolumSira() { return bolumSira; }
    public int getSinifSira() { return sinifSira; }
    public int getSinif() { return sinif; }
    public char getCinsiyet() { return cinsiyet; }

    public void setGano(float gano) { this.gano = gano; }
    public void setSinif(int sinif) { this.sinif = sinif; }

    @Override
    public String toString() {
        return ogrNo + " - " + isim + " " + soyad + " | GANO: " + gano + " | Sınıf: " + sinif + " | Bölüm: " + bolumSira + " | Cinsiyet: " + cinsiyet;
    }

    public String toFileString() {
        return isim + "," + soyad + "," + ogrNo + "," + gano + "," + bolumSira + "," + sinifSira + "," + sinif + "," + cinsiyet;
    }
}