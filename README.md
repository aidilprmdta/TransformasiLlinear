# 📘 Transformasi Linear (JavaFX)

Aplikasi **Transformasi Linear** ini dibuat menggunakan **Java** dan **JavaFX** untuk memvisualisasikan konsep transformasi linear dalam grafika komputer dan aljabar linear.

Proyek ini ditujukan untuk kebutuhan **pembelajaran**, **tugas kuliah**, dan **eksperimen visual** terhadap transformasi matriks.

---

## ✨ Fitur Utama

* Visualisasi titik / objek pada bidang koordinat
* Transformasi linear:

  * Translasi
  * Rotasi
  * Skala (scaling)
  * Shear
* Antarmuka grafis interaktif berbasis **JavaFX**
* Perhitungan transformasi menggunakan matriks

---

## 🧰 Teknologi yang Digunakan

* **Java JDK 21**
* **JavaFX 21**
* JavaFX Canvas / Scene / Controls

---

## 📁 Struktur Proyek

```text
TransformasiLlinear/
├─ src/
│  └─ sample/
│     ├─ Main.java
│     ├─ Controller.java
│     └─ (file pendukung lainnya)
├─ README.md
└─ .gitignore
```

---

## 🚀 Cara Menjalankan Proyek

### 1️⃣ Install Java JDK 21

Unduh dan install **JDK 21 (Temurin)**:

🔗 [https://adoptium.net/temurin/releases/?version=21](https://adoptium.net/temurin/releases/?version=21)

Pastikan Java sudah terpasang:

```bash
java --version
```

---

### 2️⃣ Download JavaFX 21 SDK

JavaFX **tidak termasuk** di dalam JDK 21, sehingga harus diinstall terpisah.

🔗 **Download JavaFX 21 SDK**:
[https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)

Pilih:

* JavaFX SDK
* Versi **21.x**
* Sesuai OS (Windows / Linux / macOS)

Ekstrak hasil download, contoh:

```text
C:\javafx-sdk-21.0.x\
```

---

## ⚙️ Konfigurasi JavaFX

### ▶ IntelliJ IDEA (Direkomendasikan)

1. **File → Project Structure → Libraries**
2. Tambahkan semua `.jar` dari:

   ```text
   javafx-sdk-21.0.x/lib
   ```
3. Masuk ke **Run → Edit Configurations**
4. Tambahkan pada **VM Options**:

```text
--module-path "C:\javafx-sdk-21.0.x\lib" --add-modules javafx.controls,javafx.fxml
```

> Jika tidak menggunakan FXML, cukup `javafx.controls`

---

### ▶ VS Code

Tambahkan konfigurasi berikut pada `settings.json`:

```json
{
  "java.project.referencedLibraries": [
    "path/to/javafx-sdk-21.0.x/lib/*.jar"
  ],
  "java.project.launchConfigurations": [
    {
      "mainClass": "sample.Main",
      "vmArgs": "--module-path path/to/javafx-sdk-21.0.x/lib --add-modules javafx.controls,javafx.fxml"
    }
  ]
}
```

---

## ▶ Menjalankan via Command Line

### Compile:

```bash
javac --module-path PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d out src/sample/*.java
```

### Run:

```bash
java --module-path PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp out sample.Main
```

Ganti `PATH_TO_FX` dengan path JavaFX SDK, contoh:

```text
C:\javafx-sdk-21.0.x\lib
```

---

## ⚠️ Troubleshooting Umum

* ❌ `package javafx.application does not exist`
  → JavaFX belum ditambahkan ke `module-path`

* ❌ Aplikasi tidak muncul
  → Cek **VM Options** dan pastikan path JavaFX benar

* ❌ Error saat runtime
  → Pastikan versi Java dan JavaFX **sama (21)**

---

## 🎯 Catatan Penting

* JavaFX **wajib diset manual** pada Java 11+
* IntelliJ IDEA lebih stabil untuk pengembangan JavaFX
* Proyek ini cocok untuk tugas **Aljabar Linear**, **Grafika Komputer**, dan **OOP Java**

---

## 📜 Lisensi

Proyek ini bebas digunakan untuk keperluan akademik dan pembelajaran.

---

👤 **Author**
Aidil Pramadita Putra
📌 GitHub: [https://github.com/aidilprmdta](https://github.com/aidilprmdta)
