WinxCare - Sistem za rezervaciju medicinskih pregleda
Uvod
WinxCare je moderni sistem za rezervaciju medicinskih pregleda razvijen kao distribuirana aplikacija bazirana na mikroservisnoj arhitekturi. Cilj projekta je omogućiti pacijentima da lako i brzo zakazuju termine kod doktora putem interneta, dok doktorima pruža alate za upravljanje dostupnošću i komunikaciju s pacijentima. Sistem značajno smanjuje administrativne zadatke i poboljšava organizaciju zdravstvenih usluga.

Osnovne Funkcionalnosti
Sistem WinxCare pruža sljedeće ključne funkcionalnosti:

Pretraga doktora: Pacijenti mogu pretraživati doktore po specijalizaciji, gradu ili dostupnosti termina.

Pregled profila doktora: Detaljan prikaz informacija o doktoru, uključujući specijalizaciju, radno vrijeme, iskustvo i ocjene.

Zakazivanje pregleda: Jednostavan proces rezervacije dostupnih termina.

Pregled zakazanih pregleda: Doktori mogu vidjeti svoje rasporede, a pacijenti svoje nadolazeće i prošle preglede.

Upravljanje terminima: Otkazivanje i promjena termina od strane pacijenata, te ažuriranje dostupnosti od strane doktora.

Slanje notifikacija: Potvrde rezervacije, podsjetnici i obavijesti o promjenama termina putem e-pošte/SMS-a.

Ocjenjivanje doktora: Pacijenti mogu ostaviti ocjene i komentare nakon pregleda.

Pregled historije pregleda: Pristup svim prethodnim pregledima pacijenta.

Filtriranje pregleda: Mogućnost sužavanja pretrage po specijalizaciji ili datumu.

Upravljanje medicinskom dokumentacijom: Čuvanje, pregled i upravljanje pristupom medicinskim izvještajima i rezultatima.

Arhitektura
WinxCare je izgrađen na principima mikroservisne arhitekture, što omogućava modularnost, skalabilnost i nezavisno postavljanje komponenti. Korišten je Spring Boot za razvoj mikroservisa i Spring Cloud Gateway kao API Gateway.

Mikroservisi
Projekt se sastoji od sljedećih ključnih mikroservisa:

Auth Service (LOGIN-REGISTRACIJA-APLIKACIJA): Odgovoran za autentifikaciju korisnika (login, registracija, reset lozinke) i generisanje JWT tokena.

Mikroservis Korisnici i Doktori (KORISNICI-DOKTORI): Upravlja podacima o doktorima (profili, specijalizacije, ocjene) i pacijentima (lični podaci, medicinska historija).

Mikroservis Termini i Pregledi (TERMINI-PREGLEDI): Zadužen za kreiranje, upravljanje i praćenje termina i pregleda. Koordinira Saga transakcije za zakazivanje pregleda.

Mikroservis Obavijesti i Dokumentacija (OBAVIJESTI-DOKUMENTACIJA): Upravlja slanjem notifikacija, sigurnim porukama i pohranjivanjem/dohvaćanjem medicinske dokumentacije.

API Gateway: Centralna ulazna tačka za sve eksterne zahtjeve, odgovoran za rutiranje, autentifikaciju i grubu autorizaciju.

Postavljanje i Pokretanje
Da biste postavili i pokrenuli aplikaciju, slijedite ove korake:

Preduslovi
Prije početka, osigurajte da imate instalirano sljedeće:

Java Development Kit (JDK) 17+

Maven (za izgradnju projekta)

Docker (opcionalno, za lakše pokretanje servisa)

Docker Compose (opcionalno, za orkestraciju servisa)

RabbitMQ (za Message Broker)

Eureka Server (za Service Discovery - ako se koristi, ili individualni servisi moraju znati adrese jedni drugih)

Baze podataka: Svaki mikroservis koristi svoju bazu podataka.

Koraci za Pokretanje
Pokrenite RabbitMQ Server:

Ako ste instalirali direktno na OS, osigurajte da je RabbitMQ servis pokrenut (npr. sudo systemctl start rabbitmq-server na Linuxu, ili kroz Windows Services).

Provjerite RabbitMQ Management UI na http://localhost:15672 (default username/password: guest/guest).

Pokrenite Eureka Server (ako se koristi):

Navigirajte do direktorija vašeg Eureka Server projekta.

Izgradite ga koristeći Maven:

mvn clean install

Pokrenite ga:

mvn spring-boot:run

Provjerite Eureka Dashboard, obično na http://localhost:8761.

Pokrenite Backend Mikroservise:

Za svaki od sljedećih mikroservisa: korisnici-doktori, termini-pregledi, obavijesti-dokumentacija, auth:

Navigirajte do njegovog root direktorija.

Izgradite ga koristeći Maven:

mvn clean install

Pokrenite ga:

mvn spring-boot:run

Osigurajte da su svi mikroservisi pravilno konfigurirani za povezivanje sa svojim bazama podataka i RabbitMQ-om.

Pokrenite API Gateway:

Navigirajte do direktorija vašeg API Gateway projekta.

Izgradite ga:

mvn clean install

Pokrenite ga:

mvn spring-boot:run

API Gateway će slušati na portu 8081.

Pokrenite Frontend Aplikaciju (React):

Navigirajte do root direktorija vaše React aplikacije.

Instalirajte zavisnosti:

npm install

Pokrenite aplikaciju:

npm start

Frontend će se obično otvoriti u vašem pregledniku na http://localhost:3000.

Kontakt
Za sva pitanja ili podršku, slobodno kontaktirajte članove tima:

Dženana Selimović

Adna Nuspahić

Ilma Džaferović
