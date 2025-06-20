# WinxCare - Sistem za rezervaciju medicinskih pregleda

## Uvod

WinxCare je moderni sistem za rezervaciju medicinskih pregleda razvijen kao distribuirana aplikacija bazirana na mikroservisnoj arhitekturi.  
Cilj projekta je omogućiti pacijentima da lako i brzo zakazuju termine kod doktora putem interneta, dok doktorima pruža alate za upravljanje dostupnošću i komunikaciju s pacijentima.  
Sistem značajno smanjuje administrativne zadatke i poboljšava organizaciju zdravstvenih usluga.

## Osnovne funkcionalnosti

Sistem WinxCare omogućava sljedeće ključne funkcionalnosti:

- Pretraga doktora po specijalizaciji, gradu ili dostupnosti termina
- Pregled profila doktora (specijalizacija, iskustvo, radno vrijeme, ocjene)
- Zakazivanje pregleda kroz jednostavan proces rezervacije termina
- Pregled zakazanih pregleda za pacijente i doktore
- Upravljanje terminima: otkazivanje, promjena termina, ažuriranje dostupnosti
- Slanje notifikacija putem e-maila ili SMS-a (potvrde, podsjetnici, obavijesti o izmjenama)
- Ocjenjivanje doktora (komentari i ocjene nakon pregleda)
- Pregled historije pregleda pacijenta
- Filtriranje pregleda po datumu i specijalizaciji
- Upravljanje medicinskom dokumentacijom (čuvanje, pregled, pristup izvještajima)

## Arhitektura

WinxCare koristi mikroservisnu arhitekturu radi modularnosti, skalabilnosti i lakšeg održavanja.

Tehnologije:

- Spring Boot (za razvoj mikroservisa)
- Spring Cloud Gateway (API Gateway)
- RabbitMQ (za komunikaciju između servisa)
- JWT (za autentifikaciju korisnika)

## Mikroservisi

### Auth Service (LOGIN-REGISTRACIJA-APLIKACIJA)

- Autentifikacija korisnika (login, registracija, reset lozinke)
- Generisanje i validacija JWT tokena

### Mikroservis Korisnici i Doktori (KORISNICI-DOKTORI)

- Upravljanje profilima doktora i pacijenata
- Specijalizacije i ocjenjivanje doktora
- Medicinska historija pacijenata

### Mikroservis Termini i Pregledi (TERMINI-PREGLEDI)

- Kreiranje, upravljanje i praćenje termina i pregleda
- Saga transakcije za rezervacije termina

### Mikroservis Obavijesti i Dokumentacija (OBAVIJESTI-DOKUMENTACIJA)

- Slanje notifikacija (e-mail/SMS)
- Upravljanje medicinskom dokumentacijom

### API Gateway

- Centralna ulazna tačka za sve zahtjeve
- Rutiranje prema odgovarajućim mikroservisima
- Osnovna autentifikacija i autorizacija

## Postavljanje i pokretanje

### Preduslovi

Prije pokretanja aplikacije potrebno je imati instalirano:

- Java Development Kit (JDK) 21+
- Maven
- Docker (opcionalno)
- Docker Compose (opcionalno)
- RabbitMQ
- Eureka Server (ako se koristi za Service Discovery)
- Odvojene baze podataka za svaki mikroservis

## Kontakt

Za sva pitanja ili podršku, kontaktirajte članove tima:

- **Adna Nuspahić** 
- **Dženana Selimović**   
- **Ilma Džaferović**  
