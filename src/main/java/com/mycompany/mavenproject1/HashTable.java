package com.mycompany.mavenproject1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import javafx.scene.control.Alert;

/**
 * HashTable sınıfı: Öğrencileri hash tablosuna ekler ve arama yapar.
 * * İki çakışma çözüm yöntemi desteklenir:
 * 1) Linear Probing (taşma alanı yok)
 * 2) Linear Bucket / Overflow Area (taşma alanı var)
 */
public class HashTable {

    private Ogrenci[] table;
    private int size;
    private boolean useAdvanced;
    private HashMap<Integer, Ogrenci> advancedMap;

    public HashTable(int size, boolean useAdvanced) {
        this.size = size;
        this.useAdvanced = useAdvanced;
        if(useAdvanced) {
            advancedMap = new HashMap<>();
        } else {
            table = new Ogrenci[size];
        }
    }

    private int hash(int key) { return key % size; }

    // ------------------ ÖĞRENCİ EKLE ------------------
    public void addStudent(Ogrenci ogr) {
        if(useAdvanced) {
            advancedMap.put(ogr.getOgrNo(), ogr);
        } else {
            int index = hash(ogr.getOgrNo());
            int start = index;
            while(table[index] != null) {
                index = (index + 1) % size;
                if(index == start) {
                    System.out.println("Hash table dolu!");
                    return;
                }
            }
            table[index] = ogr;
        }
        writeToFile("ogrenciler.txt", getAllStudents());
    }

    // ------------------ ÖĞRENCİ SİL ------------------
    public boolean deleteStudent(int no) {
        if(useAdvanced) {
            return advancedMap.remove(no) != null;
        } else {
            int index = hash(no);
            int start = index;
            while(table[index] != null) {
                if(table[index].getOgrNo() == no) {
                    table[index] = null;
                    return true;
                }
                index = (index + 1) % size;
                if(index == start) break;
            }
            return false;
        }
    }

    // ------------------ NUMARAYA GÖRE ARAMA ------------------
    public Ogrenci searchByNumber(int no) {
        if(useAdvanced) return advancedMap.get(no);
        int index = hash(no);
        int start = index;
        while(table[index] != null) {
            if(table[index].getOgrNo() == no) return table[index];
            index = (index + 1) % size;
            if(index == start) break;
        }
        return null;
    }

    // ------------------ ADA GÖRE ARAMA ------------------
    public ArrayList<Ogrenci> searchByName(String ad) {
        ArrayList<Ogrenci> list = new ArrayList<>();
        for(Ogrenci ogr : getAllStudents()) {
            if(ogr.getIsim().equalsIgnoreCase(ad)) list.add(ogr);
        }
        return list;
    }

    // ------------------ TÜM ÖĞRENCİLER ------------------
    public ArrayList<Ogrenci> getAllStudents() {
        ArrayList<Ogrenci> list = new ArrayList<>();
        if(useAdvanced) list.addAll(advancedMap.values());
        else {
            for(Ogrenci ogr : table) if(ogr != null) list.add(ogr);
        }
        return list;
    }

    // ------------------ LİSTELEME ------------------
    public ArrayList<Ogrenci> listByClass(int sinif) {
        ArrayList<Ogrenci> list = new ArrayList<>();
        for(Ogrenci ogr : getAllStudents()) {
            if(ogr.getSinif() == sinif) list.add(ogr);
        }
        // GANO'ya göre büyükten küçüğe sırala
        list.sort((a,b) -> Float.compare(b.getGano(), a.getGano()));
        writeToFile("sinif_sirasi.txt", list);
        return list;
    }

    /**
     * Tüm öğrencileri öğrenci numarasına göre artan sırada listeler.
     * @return Öğrenci numarasına göre sıralanmış ArrayList.
     */
    public ArrayList<Ogrenci> listByOgrNo() {
        ArrayList<Ogrenci> list = getAllStudents();
        // Öğrenci numarasına göre küçükten büyüğe sırala
        list.sort((a, b) -> Integer.compare(a.getOgrNo(), b.getOgrNo()));
        writeToFile("ogr_no_sirasi.txt", list);
        return list;
    }

    /**
     * Belirtilen bölümdeki öğrencileri GANO'ya göre büyükten küçüğe sıralayarak listeler.
     * @param bolum Bölüm sıra numarası.
     * @return Bölüme göre filtrelenmiş ve GANO'ya göre sıralanmış ArrayList.
     */
    public ArrayList<Ogrenci> listByDepartment(int bolum) {
        ArrayList<Ogrenci> list = new ArrayList<>();
        // Bölüme göre filtrele
        for(Ogrenci ogr : getAllStudents()) {
            if(ogr.getBolumSira() == bolum) list.add(ogr);
        }
        // GANO'ya göre büyükten küçüğe sırala
        list.sort((a,b) -> Float.compare(b.getGano(), a.getGano()));
        writeToFile("bolum_sirasi.txt", list);
        return list;
    }

    public ArrayList<Ogrenci> listByGano() {
        ArrayList<Ogrenci> list = getAllStudents();
        // GANO'ya göre büyükten küçüğe sırala
        list.sort((a,b) -> Float.compare(b.getGano(), a.getGano()));
        return list;
    }

    public ArrayList<Ogrenci> listByGender(char cinsiyet) {
        ArrayList<Ogrenci> list = new ArrayList<>();
        for(Ogrenci ogr : getAllStudents()) if(ogr.getCinsiyet() == cinsiyet) list.add(ogr);
        return list;
    }

    // ------------------ HASH TABLOSU GÖSTER ------------------
    public void displayHashTable() {
        System.out.println("Hash Tablosu:");
        if(useAdvanced) {
            for(Integer key : advancedMap.keySet()) {
                System.out.println(key + " -> " + advancedMap.get(key));
            }
        } else {
            for(int i=0;i<size;i++) {
                System.out.println(i + " -> " + table[i]);
            }
        }
    }

    // ------------------ LISTE GOSTER ------------------
    public void displayList(ArrayList<Ogrenci> list, String baslik) {
        System.out.println("---- " + baslik + " ----");
        for(Ogrenci ogr : list) System.out.println(ogr);
    }

    public void displayListWithPerformance(ArrayList<Ogrenci> list, String baslik) {
        displayList(list, baslik);
        writeToFile("performans.txt", list);
    }

    // ------------------ DOSYAYA YAZ ------------------
    private void writeToFile(String filename, ArrayList<Ogrenci> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for(Ogrenci ogr : list) bw.write(ogr.toFileString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}