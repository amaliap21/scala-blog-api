<!-- Improved compatibility of back to top link: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->

[![Scala][scala-shield]][scala-url]
[![http4s][http4s-shield]][http4s-url]
[![PostgreSQL][postgres-shield]][postgres-url]
[![Docker][docker-shield]][docker-url]

<br />

https://github.com/user-attachments/assets/841887a2-5c5c-4281-b509-fcd29a18dbed

<br />
<div align="center">
  <h3 align="center">Scala Blog REST API</h3>

  <p align="center">
    REST API untuk pengelolaan blog post, dengan autentikasi JWT dan arsitektur Domain-Driven Design (DDD).
    <br />
    <a href="#endpoint-api"><strong>Jelajahi dokumentasi API »</strong></a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Daftar Isi</summary>
  <ol>
    <li>
      <a href="#tentang-proyek">Tentang Proyek</a>
      <ul>
        <li><a href="#fitur-utama">Fitur Utama</a></li>
        <li><a href="#dibuat-dengan">Dibuat Dengan</a></li>
      </ul>
    </li>
    <li>
      <a href="#panduan-memulai">Panduan Memulai</a>
      <ul>
        <li><a href="#prasyarat">Prasyarat</a></li>
        <li><a href="#menjalankan-dengan-docker-compose">Menjalankan dengan Docker Compose</a></li>
        <li><a href="#menjalankan-secara-lokal-tanpa-docker">Menjalankan secara lokal</a></li>
      </ul>
    </li>
    <li><a href="#endpoint-api">Endpoint API</a></li>
    <li><a href="#menjalankan-unit-test">Menjalankan Unit Test</a></li>
    <li><a href="#struktur-proyek-ddd">Struktur Proyek (DDD)</a></li>
    <li><a href="#kontak">Kontak</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## Tentang Proyek

Proyek ini merupakan implementasi _take-home test_ Backend Engineer untuk membangun REST API pengelolaan blog post. Aplikasi dikembangkan dengan paradigma pemrograman fungsional menggunakan **Scala**, serta menerapkan **Domain-Driven Design (DDD)** dan **Repository Pattern** agar domain dan infrastruktur tetap terpisah dengan jelas.

### Fitur Utama

- **Autentikasi JWT**: registrasi pengguna dan login berbasis token.
- **Manajemen blog post (CRUD)**: daftar dan detail post dapat diakses publik; pembuatan, perubahan, serta penghapusan membutuhkan autentikasi.
- **Kontrol kepemilikan**: hanya pemilik post yang dapat mengubah atau menghapus post tersebut.
- **Keamanan**: password di-hash memakai BCrypt, tidak dikembalikan dalam respons register, dan akses diverifikasi melalui JWT.
- **Respons JSON konsisten**: seluruh respons bisnis dan autentikasi memakai format terstruktur.
- **Pengujian**: unit test menggunakan ScalaTest dan _in-memory mock repository_.
- **Konteinerisasi**: aplikasi dan PostgreSQL dapat dijalankan melalui Docker Compose.

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

### Dibuat Dengan

- [![Scala][scala-shield]][scala-url]
- [![http4s][http4s-shield]][http4s-url]
- [![Cats Effect][cats-effect-shield]][cats-effect-url]
- [![PostgreSQL][postgres-shield]][postgres-url]
- [![Docker][docker-shield]][docker-url]

Komponen lainnya: **Doobie** untuk akses database, **Circe** untuk JSON, **BCrypt** dan **jwt-circe** untuk keamanan, serta **ScalaTest** untuk pengujian.

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

<!-- GETTING STARTED -->

## Panduan Memulai

Ikuti salah satu opsi berikut untuk menjalankan API. Docker Compose adalah cara paling cepat karena database PostgreSQL ikut disiapkan.

### Prasyarat

- **Docker** dan **Docker Compose**: direkomendasikan untuk menjalankan seluruh stack.
- **JDK 11 atau 17**: diperlukan bila menjalankan aplikasi tanpa Docker.
- **SBT 1.9+**: Scala Build Tool untuk menjalankan aplikasi dan test secara lokal.

### Menjalankan dengan Docker Compose

1. Clone repositori dan masuk ke direktori proyek.

   ```sh
   git clone https://github.com/amaliap21/scala-blog-api.git
   cd scala-blog-api
   ```

2. Siapkan konfigurasi lokal. File `.env` tidak di-commit; `.env.example` adalah template yang aman dibagikan.

   ```sh
   cp .env.example .env
   ```

   PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

3. Ganti `DB_PASSWORD` dan `JWT_SECRET` di `.env` dengan nilai lokal yang kuat. Jangan memakai kredensial produksi atau memasukkan file `.env` ke Git.

4. Bangun dan jalankan aplikasi beserta PostgreSQL.

   ```sh
   docker compose up --build
   ```

5. API siap diakses di `http://localhost:8080`. Buka alamat tersebut di browser untuk menggunakan **API Playground** dan mendemonstrasikan register, login, serta CRUD post tanpa Postman.

> Password database dan JWT secret tidak ditulis di `docker-compose.yml`. Docker Compose membaca nilainya dari `.env`. Ini tetap nyaman untuk tugas lokal, tanpa mengekspos nilai rahasia saat repository diunggah ke GitHub.

### Menjalankan Secara Lokal (Tanpa Docker)

1. Pastikan PostgreSQL aktif, lalu buat database bernama `blog_db`.

2. Atur environment variable berikut bila diperlukan.

   ```sh
   export DB_HOST=localhost
   export DB_PORT=5433
   export DB_NAME=blog_db
   export DB_USER=postgres
   export DB_PASSWORD=<nilai-DB_PASSWORD-dari-.env>
   export JWT_SECRET=<nilai-JWT_SECRET-dari-.env>
   ```

3. Jalankan aplikasi.

   ```sh
   sbt run
   ```

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

<!-- API -->

## Endpoint API

Semua respons API mengikuti format berikut.

```json
{
  "status": "success",
  "message": "Deskripsi singkat tentang hasil operasi",
  "data": {}
}
```

| Method   | Endpoint      | Akses                 | Deskripsi                              |
| :------- | :------------ | :-------------------- | :------------------------------------- |
| `POST`   | `/register`   | Publik                | Mendaftarkan pengguna baru             |
| `POST`   | `/login`      | Publik                | Autentikasi dan mendapatkan token JWT  |
| `GET`    | `/posts`      | Publik                | Mengambil seluruh post                 |
| `GET`    | `/posts/{id}` | Publik                | Mengambil detail post berdasarkan UUID |
| `POST`   | `/posts`      | Terproteksi (JWT)     | Membuat post baru                      |
| `PUT`    | `/posts/{id}` | Terproteksi (pemilik) | Mengubah post                          |
| `DELETE` | `/posts/{id}` | Terproteksi (pemilik) | Menghapus post                         |

> Untuk endpoint terproteksi, sertakan header `Authorization: Bearer <JWT_TOKEN>`.

Jika token tidak ada atau tidak valid, API mengembalikan respons berikut:

```json
{
  "status": "error",
  "message": "Token JWT tidak valid atau tidak ditemukan",
  "data": null
}
```

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

<!-- TESTING -->

## Menjalankan Unit Test

Jalankan perintah berikut untuk mengeksekusi unit test berbasis ScalaTest:

```sh
sbt test
```

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

<!-- PROJECT STRUCTURE -->

## Struktur Proyek (DDD)

```
src/
├── main/
│   └── scala/com/nolimit/blog/
│       ├── domain/              # Entitas inti dan repository traits
│       ├── infrastructure/      # Database (Doobie) dan keamanan (BCrypt, JWT)
│       ├── application/         # Logika bisnis (services)
│       └── presentation/        # Controller HTTP, routes, DTO, dan middleware
└── test/
    └── scala/com/nolimit/blog/  # Unit test dan mock repositories
```

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

## Kontak

Untuk pertanyaan, pengujian, atau verifikasi proyek, hubungi pemilik repositori melalui kontak yang dicantumkan pada profil GitHub proyek.

> Email: amaliaputriii2104@gmail.com

<p align="right">(<a href="#readme-top">kembali ke atas</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->

[scala-shield]: https://img.shields.io/badge/Scala-2.13-DC322F?style=for-the-badge&logo=scala&logoColor=white
[scala-url]: https://www.scala-lang.org/
[http4s-shield]: https://img.shields.io/badge/http4s-0A0A0A?style=for-the-badge&logo=scala&logoColor=white
[http4s-url]: https://http4s.org/
[cats-effect-shield]: https://img.shields.io/badge/Cats%20Effect-Functional%20Programming-4B275F?style=for-the-badge
[cats-effect-url]: https://typelevel.org/cats-effect/
[postgres-shield]: https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white
[postgres-url]: https://www.postgresql.org/
[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://www.docker.com/
