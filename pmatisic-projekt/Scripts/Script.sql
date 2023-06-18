CREATE TABLE KORISNICI (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    korime VARCHAR(255) NOT NULL,
    lozinka VARCHAR(255) NOT NULL,
    ime VARCHAR(255) NOT NULL,
    prezime VARCHAR(255) NOT NULL
);

CREATE TABLE AERODROMI_LETOVI (
    icao VARCHAR(255) PRIMARY KEY,
    aktivan BOOLEAN,
    FOREIGN KEY (icao) REFERENCES AIRPORTS (icao)
);

CREATE TABLE DNEVNIK (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    vrsta VARCHAR(255),
    vrijeme_pristupa TIMESTAMP,
    putanja VARCHAR(1024),
    ip_adresa VARCHAR(255),
    korisnik INTEGER,
    FOREIGN KEY (korisnik) REFERENCES KORISNICI(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE KORISNICI TO APLIKACIJA;

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE DNEVNIK TO APLIKACIJA;

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE AERODROMI_LETOVI TO APLIKACIJA;
   
TRUNCATE TABLE LETOVI_POLASCI;

SELECT COUNT(*) FROM LETOVI_POLASCI;

SELECT * FROM LETOVI_POLASCI;

TRUNCATE TABLE DNEVNIK; 

SELECT * FROM DNEVNIK;

SELECT * FROM AERODROMI_LETOVI;

DELETE FROM KORISNICI WHERE ID = '5';

SELECT * FROM KORISNICI;

SELECT * FROM LETOVI_POLASCI WHERE ESTDEPARTUREAIRPORT = 'LOWW' AND FIRSTSEEN BETWEEN 1454621991 AND 1454630400 LIMIT 20 OFFSET 0;
