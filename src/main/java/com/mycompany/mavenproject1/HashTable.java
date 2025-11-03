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
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

/**
 * HashTable sınıfı: Öğrencileri hash tablosuna ekler ve arama yapar.
 * * İki çakışma çözüm yöntemi desteklenir:
 * 1) Linear Probing (taşma alanı yok)
 * 2) Linear Bucket / Overflow Area (taşma alanı var)
 */
public class HashTable {

    private Ogrenci[] table;
    private boolean[] deleted; // tombstone işaretleri
    private int size;
    public boolean useAdvanced;
    private HashMap<Integer, Ogrenci> advancedMap;

    public void setUseAdvanced(boolean useAdvanced) {
        this.useAdvanced = useAdvanced;
    }

    public HashTable(int size, boolean useAdvanced) {
        this.size = size;
        this.useAdvanced = useAdvanced;
        if (useAdvanced) {
            advancedMap = new HashMap<>();
        } else {
            table = new Ogrenci[size];
            deleted = new boolean[size];
            // başlangıçta tüm deleted false
        }
    }

    private int hash(int key) {
        return Math.abs(key) % size;
    }

    // Yardımcı: tabloda bir anahtarın index'ini bulur, bulunamazsa -1 döner.
    private int findIndex(int key) {
        int index = hash(key);
        int start = index;
        while (true) {
            // tamamen boş (null ve tombstone değil) ise artık probe bitmiştir
            if (table[index] == null && !deleted[index]) {
                return -1; // bulunamadı
            }
            // eğer burada bir kayıt varsa kontrol et
            if (table[index] != null && table[index].getOgrNo() == key) {
                return index;
            }
            index = (index + 1) % size;
            if (index == start) return -1; // tüm tablo tarandı
        }
    }

    // ------------------ ÖĞRENCİ EKLE ------------------
    public void addStudent(Ogrenci ogr) {
        if (useAdvanced) {
            advancedMap.put(ogr.getOgrNo(), ogr);
            // İsteğe bağlı: dosyaya yaz
            writeToFile("ogrenciler.txt", getAllStudentsAdvanced());
            return;
        }

        // önce aynı öğrenci var mı kontrol et (tekrar eklemeyi engellemek için)
        int existing = findIndex(ogr.getOgrNo());
        if (existing != -1) {
            // istersen burada güncelleme de yapabilirsin:
            table[existing] = ogr;
            deleted[existing] = false; // güvenlik
            writeToFile("ogrenciler.txt", getAllStudents());
            return;
        }

        int index = hash(ogr.getOgrNo());
        int start = index;
        int firstDeletedIndex = -1;

        while (true) {
            // eğer boş ve tombstone olmayan bir hücre ise doğrudan yerleş
            if (table[index] == null && !deleted[index]) {
                if (firstDeletedIndex != -1) {
                    table[firstDeletedIndex] = ogr;
                    deleted[firstDeletedIndex] = false;
                } else {
                    table[index] = ogr;
                    deleted[index] = false;
                }
                writeToFile("ogrenciler.txt", getAllStudents());
                return;
            }

            // tombstone ise ilk tombstone'ı kaydet
            if (table[index] == null && deleted[index]) {
                if (firstDeletedIndex == -1) firstDeletedIndex = index;
            }

            // eğer mevcut bir öğe varsa aynı anahtarı kontrol ettik zaten (üstte)
            index = (index + 1) % size;
            if (index == start) {
                // tablo dolu veya sadece tombstonelar var
                if (firstDeletedIndex != -1) {
                    table[firstDeletedIndex] = ogr;
                    deleted[firstDeletedIndex] = false;
                    writeToFile("ogrenciler.txt", getAllStudents());
                    return;
                } else {
                    System.out.println("Hash table dolu! Öğrenci eklenemedi: " + ogr.getOgrNo());
                    return;
                }
            }
        }
    }
    private ArrayList<Ogrenci> getAllStudentsAdvanced() {
        return new ArrayList<>(advancedMap.values());
    }

    // ------------------ ÖĞRENCİ SİL ------------------
    public boolean deleteStudent(int no) {
        if (useAdvanced) {
            boolean removed = advancedMap.remove(no) != null;
            if (removed) writeToFile("ogrenciler.txt", getAllStudentsAdvanced());
            return removed;
        } else {
            int index = findIndex(no);
            if (index == -1) return false; // bulunamadı

            // tombstone koy: fiziksel nesneyi null yap ve deleted=true
            table[index] = null;
            deleted[index] = true;

            writeToFile("ogrenciler.txt", getAllStudents());
            return true;
        }
    }

    // ------------------ NUMARAYA GÖRE ARAMA ------------------
    public Ogrenci searchByNumber(int no) {
        Ogrenci ogr;
        String type;
        long startTime = System.nanoTime();
        if(useAdvanced){
            type="Advanced";

            ogr = advancedMap.get(no);


        }else{
            type="temel";


            int index = hash(no);
            int start = index;
            while(table[index] != null) {
                if(table[index].getOgrNo() == no){
                    ogr = table[index];
                    break;
                }
                index = (index + 1) % size;
                if(index == start) break;
            }
            ogr = table[index];

        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "Ogrenci_No'ya göre arama  "+type+ " mod  "  + " arama süresi: " + duration + " ms");
        return ogr;
    }

    // ------------------ ADA GÖRE ARAMA (GELİŞMİŞ - ArrayList) ------------------
    public ArrayList<Ogrenci> searchByNameAdvanced(String ad) {
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> list = new ArrayList<>();
        for (Ogrenci ogr : getAllStudents()) {
            if (ogr.getIsim().equalsIgnoreCase(ad)) {
                list.add(ogr);
            }
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "Ad'a göre gelişmiş mod  "  + " arama süresi: " + duration + " ms");
        return list;
    }

    // ------------------ ADA GÖRE ARAMA (TEMEL - Dizi) ------------------
    public Ogrenci[] searchByNameBasic(String ad) {
        long startTime = System.nanoTime();
        // Önce tüm öğrencileri dizide toplayalım
        ArrayList<Ogrenci> allStudents = getAllStudents();
        Ogrenci[] dizi = new Ogrenci[allStudents.size()];
        for (int i = 0; i < allStudents.size(); i++) {
            dizi[i] = allStudents.get(i);
        }

        // Arama sonucunu geçici bir listeye koyacağız
        ArrayList<Ogrenci> bulunanlar = new ArrayList<>();

        for (int i = 0; i < dizi.length; i++) {
            if (dizi[i].getIsim().equalsIgnoreCase(ad)) {
                bulunanlar.add(dizi[i]);
            }
        }

        // Sonuçları dizi olarak döndür
        Ogrenci[] sonuc = new Ogrenci[bulunanlar.size()];
        for (int i = 0; i < bulunanlar.size(); i++) {
            sonuc[i] = bulunanlar.get(i);
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "Ad'a göre temel mod  "  + " arama süresi: " + duration + " ms");

        return sonuc;
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
    public Ogrenci[] getAllStudentsArray() {
        ArrayList<Ogrenci> list = new ArrayList<>();
        if (useAdvanced) {
            list.addAll(advancedMap.values());
        } else {
            for (Ogrenci ogr : table) {
                if (ogr != null) list.add(ogr);
            }
        }

        // ArrayList'i diziye çevir
        Ogrenci[] array = new Ogrenci[list.size()];
        return list.toArray(array);
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
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> list = getAllStudents();
        // Öğrenci numarasına göre küçükten büyüğe sırala
        list.sort((a, b) -> Integer.compare(a.getOgrNo(), b.getOgrNo()));
        writeToFile("ogr_no_sirasi.txt", list);
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "Ogrenci_No'ya göre gelişmiş mod  "  + " sıralama süresi: " + duration + " ms");
        return list;
    }

    public Ogrenci[] listByOgrNoArray() {
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> allStudents = getAllStudents(); // mevcut öğrencileri al
        Ogrenci[] dizi = new Ogrenci[allStudents.size()];
        for (int i = 0; i < allStudents.size(); i++) {
            dizi[i] = allStudents.get(i);
        }

        // Basit Bubble Sort ile dizi sıralaması (öğrenci numarasına göre)
        for (int i = 0; i < dizi.length - 1; i++) {
            for (int j = 0; j < dizi.length - i - 1; j++) {
                if (dizi[j].getOgrNo() > dizi[j + 1].getOgrNo()) {
                    Ogrenci temp = dizi[j];
                    dizi[j] = dizi[j + 1];
                    dizi[j + 1] = temp;
                }
            }
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "OgrenciNo'ya göre temel mod  "  + " sıralama süresi: " + duration + " ms");

        return dizi;
    }


    /**
     * Belirtilen bölümdeki öğrencileri GANO'ya göre büyükten küçüğe sıralayarak listeler.
     * @param bolum Bölüm sıra numarası.
     * @return Bölüme göre filtrelenmiş ve GANO'ya göre sıralanmış ArrayList.
     */
    public ArrayList<Ogrenci> listByDepartment(int bolum) {
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> list = new ArrayList<>();
        // Bölüme göre filtrele
        for(Ogrenci ogr : getAllStudents()) {
            if(ogr.getBolumSira() == bolum) list.add(ogr);
        }
        // GANO'ya göre büyükten küçüğe sırala
        list.sort((a,b) -> Float.compare(b.getGano(), a.getGano()));
        writeToFile("bolum_sirasi.txt", list);
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "GANO'ya göre gelişmiş mod  "  +bolum+ ". (Sınıf) sıralama süresi: " + duration + " ms");
        return list;
    }
    public Ogrenci[] listbydepartmanArray(int bolum) {
        long startTime = System.nanoTime();
        Ogrenci[] array = getAllStudentsArray(); // Daha önce yazdığımız diziyi döndüren metot

        int n = array.length;
        int l=0;
        for(int i = 0; i < n; i++) {
            if(array[i].getBolumSira() == bolum) {

                l++;
            }
        }
        Ogrenci[] array2=new Ogrenci[l];
        int k = 0;
        for(int i = 0; i < n; i++) {
            if(array[i].getBolumSira() == bolum) {
                array2[k] = array[i];
                k++;
            }
        }
        for (int i = 0; i < l - 1; i++) {
            for (int j = 0; j < l - 1 - i; j++) {
                if (array2[j].getGano() < array2[j + 1].getGano()) {
                    // Swap işlemi
                    Ogrenci temp = array2[j];
                    array2[j] = array2[j + 1];
                    array2[j + 1] = temp;
                }
            }
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "GANO'ya göre temel mod  "  +bolum+ ". (Sınıf) sıralama süresi: " + duration + " ms");

        return array2;

    }

    public ArrayList<Ogrenci> listByGanoAdvanced() {
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> list = new ArrayList<>();



             list = getAllStudents();
            // GANO'ya göre büyükten küçüğe sırala
            list.sort((a,b) -> Float.compare(b.getGano(), a.getGano()));
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "GANO'ya göre gelişmiş mod  "  + " sıralama süresi: " + duration + " ms");


        return list;
    }
    public Ogrenci[] listByGanoArray() {
        long startTime = System.nanoTime();
        Ogrenci[] array = getAllStudentsArray(); // Daha önce yazdığımız diziyi döndüren metot

        int n = array.length;

        // Bubble Sort ile GANO'ya göre büyükten küçüğe sıralama
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (array[j].getGano() < array[j + 1].getGano()) {
                    // Swap işlemi
                    Ogrenci temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "GANO'ya göre temel mod  "  + " sıralama süresi: " + duration + " ms");

        return array;
    }
    private void writePerformanceToFile(String filename, String content) {
        try (FileWriter fw = new FileWriter(filename, true)) { // true = append (üstüne yaz)
            fw.write(content + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ------------------ CİNSİYETE GÖRE LİSTELE (GELİŞMİŞ - ArrayList) ------------------
    public ArrayList<Ogrenci> listByGenderAdvanced(char cinsiyet) {
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> list = new ArrayList<>();
        for (Ogrenci ogr : getAllStudents()) {
            if (ogr.getCinsiyet() == cinsiyet) {
                list.add(ogr);
            }
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "Cinsiyet'e göre gelişmiş mod  "  + " arama süresi: " + duration + " ms");
        return list;
    }

    // ------------------ CİNSİYETE GÖRE LİSTELE (TEMEL - Dizi) ------------------
    public Ogrenci[] listByGenderBasic(char cinsiyet) {
        long startTime = System.nanoTime();
        ArrayList<Ogrenci> allStudents = getAllStudents();
        Ogrenci[] dizi = new Ogrenci[allStudents.size()];
        for (int i = 0; i < allStudents.size(); i++) {
            dizi[i] = allStudents.get(i);
        }

        ArrayList<Ogrenci> bulunanlar = new ArrayList<>();
        for (int i = 0; i < dizi.length; i++) {
            if (dizi[i].getCinsiyet() == cinsiyet) {
                bulunanlar.add(dizi[i]);
            }
        }

        Ogrenci[] sonuc = new Ogrenci[bulunanlar.size()];
        for (int i = 0; i < bulunanlar.size(); i++) {
            sonuc[i] = bulunanlar.get(i);
        }
        long endTime = System.nanoTime(); // Bitiş zamanı
        long duration = (endTime - startTime) / 1_000_000;
        writePerformanceToFile("performans.txt", "Cinsiyet'e göre temel mod  "  + " arama süresi: " + duration + " ms");

        return sonuc;
    }


    public ObservableList<HashRow> getHashTableData() {
        ObservableList<HashRow> list = FXCollections.observableArrayList();
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                Ogrenci ogr = table[i]; // table[i] = Ogrenci objesi
                list.add(new HashRow(i, String.valueOf(ogr.getOgrNo()), ogr.toString()));
            }
        }
        return list;
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