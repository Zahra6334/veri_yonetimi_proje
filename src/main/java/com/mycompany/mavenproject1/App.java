package com.mycompany.mavenproject1;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class App extends Application {

    private boolean Use_Advanced_Data_Type = false; // true = gelişmiş mod, false = temel mod
    private HashTable hashTable;

    @Override
    public void start(Stage stage) {
        hashTable = new HashTable(12007, Use_Advanced_Data_Type);
        addRandomStudents(1000);

        Label header = new Label("Öğrenci Kayıt Sistemi");

        // -------------------------
        // Öğrenci Ekleme Formu
        // -------------------------
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        CheckBox advanced=new CheckBox("advanced");
        TextField tfIsim = new TextField();
        TextField tfSoyad = new TextField();
        TextField tfOgrNo = new TextField();
        TextField tfGano = new TextField();
        TextField tfBolumSira = new TextField();
        TextField tfSinif = new TextField();
        TextField tfSinifSira = new TextField();
        TextField tfCinsiyet = new TextField();

        form.addRow(0, new Label("İsim:"), tfIsim);
        form.addRow(1, new Label("Soyad:"), tfSoyad);
        form.addRow(2, new Label("Öğrenci No:"), tfOgrNo);
        form.addRow(3, new Label("GANO:"), tfGano);
        form.addRow(4, new Label("Bölüm Sıra:"), tfBolumSira);
        form.addRow(5, new Label("Sınıf:"), tfSinif);
        form.addRow(6, new Label("Sınıf Sıra:"), tfSinifSira);
        form.addRow(7, new Label("Cinsiyet (E/K):"), tfCinsiyet);
        form.addRow(8,advanced);

        advanced.setOnAction(e->{
            if(advanced.isSelected()){
                Use_Advanced_Data_Type = true;
            }else{
                Use_Advanced_Data_Type = false;
            }
        });
        // -------------------------
        // Öğrenci Ekle
        // -------------------------
        Button btnEkle = new Button("Öğrenci Ekle");
        btnEkle.setOnAction(e -> {
            try {
                String isim = tfIsim.getText();
                String soyad = tfSoyad.getText();
                int ogrNo = Integer.parseInt(tfOgrNo.getText());
                float gano = Float.parseFloat(tfGano.getText());
                int bolumSira = Integer.parseInt(tfBolumSira.getText());
                int sinif = Integer.parseInt(tfSinif.getText());
                int sinifSira = Integer.parseInt(tfSinifSira.getText());
                char cinsiyet = tfCinsiyet.getText().toUpperCase().charAt(0);

                Ogrenci ogr = new Ogrenci(isim, soyad, ogrNo, gano, bolumSira, sinifSira, sinif, cinsiyet);
                hashTable.addStudent(ogr);

                showAlert("Başarılı", "Öğrenci eklendi: " + ogr.getOgrNo());

                tfIsim.clear(); tfSoyad.clear(); tfOgrNo.clear(); tfGano.clear();
                tfBolumSira.clear(); tfSinif.clear(); tfSinifSira.clear(); tfCinsiyet.clear();

            } catch (Exception ex) {
                showAlert("Hata", "Tüm alanları doğru doldurun!");
            }
        });

        // -------------------------
// Hash Tablosunu Göster Butonu
// -------------------------
        Button btnHash = new Button("Hash Tablosunu Göster");
        btnHash.setOnAction(e -> {
            TableView<HashRow> tableView = new TableView<>();

            TableColumn<HashRow, Integer> colIndex = new TableColumn<>("Index");
            colIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
            colIndex.setPrefWidth(50);

            TableColumn<HashRow, String> colKey = new TableColumn<>("Anahtar");
            colKey.setCellValueFactory(new PropertyValueFactory<>("key"));
            colKey.setPrefWidth(100);

            TableColumn<HashRow, String> colValue = new TableColumn<>("Öğrenci Bilgisi");
            colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
            colValue.setPrefWidth(600);

            tableView.getColumns().addAll(colIndex, colKey, colValue);
            tableView.setItems(hashTable.getHashTableData());

            VBox vbox = new VBox(tableView);
            VBox.setVgrow(tableView, Priority.ALWAYS);

            Stage stageHash = new Stage();
            stageHash.setTitle("Hash Tablosu");
            stageHash.setScene(new Scene(vbox, 800, 600));
            stageHash.show();
        });

        // -------------------------
        // Öğrenci No ile Ara (Arama sonuçları yine Alert ile gösteriliyor)
        // -------------------------
        Button btnAraNo = new Button("Öğrenci No ile Ara");
        btnAraNo.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Öğrenci numarasını girin:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    int no = Integer.parseInt(input);
                    Ogrenci ogr = hashTable.searchByNumber(no);
                    if (ogr != null) showAlert("Bulundu", ogr.toString());
                    else showAlert("Bulunamadı", "Öğrenci bulunamadı!");
                } catch (NumberFormatException ex) {
                    showAlert("Hata", "Geçerli bir sayı girin!");
                }
            });
        });

        // -------------------------
        // Ada Göre Ara (Arama sonuçları Tabloda gösteriliyor)
        // -------------------------
        Button btnAdaAra = new Button("Ada Göre Ara");
        btnAdaAra.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Aranacak adı girin:");
            dialog.showAndWait().ifPresent(input -> {
                long startTime = System.currentTimeMillis();
                ArrayList<Ogrenci> list = hashTable.searchByName(input);
                long endTime = System.currentTimeMillis();
                if (!list.isEmpty()) {
                    displayStudentsInTable(list, "Ada Göre Arama Sonuçları: " + input); // Tabloda göster
                } else {
                    showAlert("Bulunamadı", input + " isimli öğrenci bulunamadı!");
                }
                showAlert("Bilgi", "Arama süresi: " + (endTime - startTime) + " ms");
            });
        });

        // -------------------------
        // Öğrenci Sil
        // -------------------------
        Button btnSil = new Button("Öğrenci Sil");
        btnSil.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Silinecek öğrenci numarasını girin:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    int no = Integer.parseInt(input);
                    boolean silindi = hashTable.deleteStudent(no);
                    if (silindi) showAlert("Başarılı", no + " numaralı öğrenci silindi.");
                    else showAlert("Hata", "Öğrenci bulunamadı!");
                } catch (NumberFormatException ex) {
                    showAlert("Hata", "Geçerli bir sayı girin!");
                }
            });
        });

        // -------------------------
        // Öğrenci Güncelle
        // -------------------------
        Button btnGuncelle = new Button("Öğrenci Güncelle");
        btnGuncelle.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Güncellenecek öğrenci numarasını girin:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    int no = Integer.parseInt(input);
                    Ogrenci ogr = hashTable.searchByNumber(no);
                    if (ogr != null) {
                        TextInputDialog ganoDialog = new TextInputDialog(Float.toString(ogr.getGano()));
                        ganoDialog.setHeaderText("Yeni GANO girin:");
                        ganoDialog.showAndWait().ifPresent(ganoInput -> ogr.setGano(Float.parseFloat(ganoInput)));

                        TextInputDialog sinifDialog = new TextInputDialog(Integer.toString(ogr.getSinif()));
                        sinifDialog.setHeaderText("Yeni Sınıf girin:");
                        sinifDialog.showAndWait().ifPresent(sinifInput -> ogr.setSinif(Integer.parseInt(sinifInput)));

                        showAlert("Başarılı", "Öğrenci güncellendi: " + ogr.getOgrNo());
                    } else {
                        showAlert("Hata", "Öğrenci bulunamadı!");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Hata", "Geçerli bir sayı girin!");
                }
            });
        });


        // -------------------------
        // Tüm Öğrenciler
        // -------------------------
        Button btnTumOgr = new Button("Tüm Öğrenciler");
        btnTumOgr.setOnAction(e -> {
            long startTime = System.currentTimeMillis();
            ArrayList<Ogrenci> list = hashTable.getAllStudents();
            long endTime = System.currentTimeMillis();

            displayStudentsInTable(list, "Tüm Öğrenciler Listesi"); // Tabloda göster

            showAlert("Bilgi", "Listeleme süresi: " + (endTime - startTime) + " ms");
        });



        // -------------------------
        // ÖĞRENCİ NO'YA GÖRE LİSTELE
        // -------------------------
        Button btnNoListe = new Button("Öğrenci No'ya Göre Listele");
        btnNoListe.setOnAction(e -> {
            long startTime = System.currentTimeMillis();
            ArrayList<Ogrenci> list = hashTable.listByOgrNo();
            long endTime = System.currentTimeMillis();

            displayStudentsInTable(list, "Öğrenci No'ya Göre Sıralama"); // Tabloda göster

            showAlert("Bilgi", "Listeleme süresi: " + (endTime - startTime) + " ms");
        });





        // -------------------------
        // Cinsiyete Göre Listele
        // -------------------------
        Button btnCinsiyetListe = new Button("Cinsiyete Göre Listele");
        btnCinsiyetListe.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Cinsiyet girin (E/K):");
            dialog.showAndWait().ifPresent(input -> {
                if (input.length() > 0) {
                    char cins = Character.toUpperCase(input.charAt(0));
                    ArrayList<Ogrenci> list = hashTable.listByGender(cins);

                    displayStudentsInTable(list, cins + " Cinsiyetli Öğrenciler");

                    showAlert("Bilgi", list.size() + " öğrenci bulundu.");
                } else {
                    showAlert("Hata", "Geçerli bir cinsiyet girin!");
                }
            });
        });

        // -------------------------
        // Bölüme Göre Sıralı Analiz (İstenen Yeni Fonksiyon)
        // -------------------------
        Button btnAnaliz = new Button("Sınıflara Göre Sıralı Analiz Yap");
        btnAnaliz.setOnAction(e -> {
            long totalStartTime = System.currentTimeMillis();
            if(Use_Advanced_Data_Type){
                // 1. Bölüm 1'i GANO'ya göre sırala ve göster
                ArrayList<Ogrenci> list1 = hashTable.listByDepartment(1);
                displayStudentsInTable(list1, "Analiz: 1. Sınıf GANO Sıralaması");

                // 2. Bölüm 2'yi GANO'ya göre sırala ve göster
                ArrayList<Ogrenci> list2 = hashTable.listByDepartment(2);
                displayStudentsInTable(list2, "Analiz: 2. Sınıf GANO Sıralaması");

                // 3. Bölüm 3'ü GANO'ya göre sırala ve göster
                ArrayList<Ogrenci> list3 = hashTable.listByDepartment(3);
                displayStudentsInTable(list3, "Analiz: 3. Sınıf GANO Sıralaması");

                // 4. Bölüm 4'ü GANO'ya göre sırala ve göster
                ArrayList<Ogrenci> list4 = hashTable.listByDepartment(4);
                displayStudentsInTable(list4, "Analiz: 4. Sınıf GANO Sıralaması");

                // 5. TÜM Bölümleri GANO'ya Göre Sırala ve göster
                ArrayList<Ogrenci> allStudentsByGano = hashTable.listByGanoAdvanced();
                displayStudentsInTable(allStudentsByGano, "Analiz: TÜM Bölümler GANO Sıralaması");

            }else{
                Ogrenci[] list1 = hashTable.listbydepartmanArray(1);
                ArrayList<Ogrenci> list1view = new ArrayList<>();
                for (Ogrenci o : list1) {
                    list1view.add(o);
                }

// Mevcut metodla gösteriyoruz
                displayStudentsInTable(list1view, "Analiz: 1. Sınıf GANO Sıralaması");

                Ogrenci[] list2 = hashTable.listbydepartmanArray(2);
                ArrayList<Ogrenci> list2view = new ArrayList<>();
                for (Ogrenci o : list2) {
                    list2view.add(o);
                }

// Mevcut metodla gösteriyoruz
                displayStudentsInTable(list2view, "Analiz: 2. Sınıf GANO Sıralaması");

                Ogrenci[] list3 = hashTable.listbydepartmanArray(3);
                ArrayList<Ogrenci> list3view = new ArrayList<>();
                for (Ogrenci o : list3) {
                    list3view.add(o);
                }

// Mevcut metodla gösteriyoruz
                displayStudentsInTable(list3view, "Analiz: 3. Sınıf GANO Sıralaması");

                Ogrenci[] list4 = hashTable.listbydepartmanArray(4);
                ArrayList<Ogrenci> list4view = new ArrayList<>();
                for (Ogrenci o : list4) {
                    list4view.add(o);
                }

// Mevcut metodla gösteriyoruz
                displayStudentsInTable(list4view, "Analiz: 4. Sınıf GANO Sıralaması");
                // Diziyi ArrayList'e çeviriyoruz
                Ogrenci[] allStudentsByGano = hashTable.listByGanoArray();
                ArrayList<Ogrenci> list = new ArrayList<>();
                for (Ogrenci o : allStudentsByGano) {
                    list.add(o);
                }

// Mevcut metodla gösteriyoruz
                displayStudentsInTable(list, "Analiz: TÜM Bölümler GANO Sıralaması");

            }


            long totalEndTime = System.currentTimeMillis();
            showAlert("Analiz Tamamlandı",
                    "Toplam 5 farklı listeleme işlemi ardışık olarak tamamlandı.\n" +
                            "Toplam Süre: " + (totalEndTime - totalStartTime) + " ms");
        });

        // -------------------------
        // GUI’ye ekleme
        // -------------------------

        VBox root = new VBox(10, header, form, btnEkle, btnAraNo, btnAdaAra, btnSil, btnGuncelle,
                btnTumOgr,btnNoListe, btnCinsiyetListe, btnAnaliz); // Yeni buton eklendi
        Scene scene = new Scene(root, 600, 780); // Pencere boyutu artırıldı

        stage.setTitle("Öğrenci Kayıt Sistemi");
        stage.setScene(scene);
        stage.show();
    }

    // ... (addRandomStudents, displayStudentsInTable ve showAlert metodları aynı kalmıştır) ...

    private void addRandomStudents(int count) {
        Random random = new Random();
        // Basit rastgele isim ve soyadlar
        String[] isimler = {"Ahmet", "Ayşe", "Mehmet", "Fatma", "Ali", "Zeynep", "Emre", "Elif"};
        String[] soyadlar = {"Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Öztürk", "Yıldız", "Aydın"};

        // Öğrenci numaraları için bir başlangıç değeri belirleyelim
        int ogrNoStart = 202000000;

        System.out.println(count + " adet rastgele öğrenci ekleniyor...");

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            // Rastgele veriler üretme
            String isim = isimler[random.nextInt(isimler.length)];
            String soyad = soyadlar[random.nextInt(soyadlar.length)];
            // Benzersiz numara:
            int ogrNo = ogrNoStart + i;

            // GANO: 1.00 ile 4.00 arasında
            float gano = 1.00f + random.nextFloat() * 3.00f;
            gano = Math.round(gano * 100.0f) / 100.0f;

            int bolumSira = random.nextInt(4) + 1; // 1-5 arası
            int sinif = random.nextInt(4) + 1; // 1-4 arası
            int sinifSira = random.nextInt(100) + 1; // 1-100 arası
            char cinsiyet = random.nextBoolean() ? 'E' : 'K';

            Ogrenci ogr = new Ogrenci(isim, soyad, ogrNo, gano, bolumSira, sinifSira, sinif, cinsiyet);
            hashTable.addStudent(ogr);
        }
        long endTime = System.currentTimeMillis();

        System.out.println(count + " öğrenci ekleme tamamlandı. Süre: " + (endTime - startTime) + " ms");
        showAlert("Bilgi", count + " rastgele öğrenci başarıyla eklendi. Süre: " + (endTime - startTime) + " ms");
    }

    private void displayStudentsInTable(ArrayList<Ogrenci> students, String title) {
        Stage tableStage = new Stage();
        tableStage.initModality(Modality.APPLICATION_MODAL);
        tableStage.setTitle(title);

        // 1. TableView ve ObservableList oluştur
        TableView<Ogrenci> tableView = new TableView<>();
        ObservableList<Ogrenci> studentData = FXCollections.observableArrayList(students);
        tableView.setItems(studentData);

        // 2. Sütunları Tanımla

        // Öğrenci No Sütunu
        TableColumn<Ogrenci, Integer> noCol = new TableColumn<>("Öğrenci No");
        noCol.setCellValueFactory(new PropertyValueFactory<>("ogrNo"));
        noCol.setPrefWidth(100);

        // İsim Sütunu
        TableColumn<Ogrenci, String> isimCol = new TableColumn<>("İsim");
        isimCol.setCellValueFactory(new PropertyValueFactory<>("isim"));
        isimCol.setPrefWidth(120);

        // Soyad Sütunu
        TableColumn<Ogrenci, String> soyadCol = new TableColumn<>("Soyad");
        soyadCol.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        soyadCol.setPrefWidth(120);

        // GANO Sütunu
        TableColumn<Ogrenci, Float> ganoCol = new TableColumn<>("GANO");
        ganoCol.setCellValueFactory(new PropertyValueFactory<>("gano"));
        ganoCol.setPrefWidth(70);

        // Sınıf Sütunu


        // Bölüm Sıra Sütunu (Ekstra Sütun)
        TableColumn<Ogrenci, Integer> sınıfSiraCol = new TableColumn<>("sınıf");
        sınıfSiraCol.setCellValueFactory(new PropertyValueFactory<>("bolumSira"));
        sınıfSiraCol.setPrefWidth(60);

        // Cinsiyet Sütunu
        TableColumn<Ogrenci, Character> cinsiyetCol = new TableColumn<>("Cins.");
        cinsiyetCol.setCellValueFactory(new PropertyValueFactory<>("cinsiyet"));
        cinsiyetCol.setPrefWidth(50);

        // Sütunları tabloya ekle
        tableView.getColumns().addAll(noCol, isimCol, soyadCol, ganoCol, sınıfSiraCol, cinsiyetCol); // Bölüm eklendi

        // 3. Sahneyi oluştur ve göster
        VBox root = new VBox(tableView);
        Scene scene = new Scene(root, 760, 500); // Pencere genişliği artırıldı
        tableStage.setScene(scene);
        tableStage.show();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}