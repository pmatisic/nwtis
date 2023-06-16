CREATE TABLE KORISNICI (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    korime VARCHAR(255) NOT NULL,
    lozinka VARCHAR(255) NOT NULL,
    ime VARCHAR(255) NOT NULL,
    prezime VARCHAR(255) NOT NULL
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE KORISNICI TO APLIKACIJA;

CREATE TABLE DNEVNIK (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    vrsta VARCHAR(255),
    vrijeme_pristupa TIMESTAMP,
    putanja VARCHAR(1024),
    korisnik INTEGER,
    FOREIGN KEY (korisnik) REFERENCES KORISNICI(id)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE DNEVNIK TO APLIKACIJA;

ALTER TABLE DNEVNIK ADD ip_adresa VARCHAR(255);

CREATE TABLE AERODROMI_LETOVI (
    icao VARCHAR(255) PRIMARY KEY,
    aktivan BOOLEAN,
    FOREIGN KEY (icao) REFERENCES AIRPORTS (icao)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE AERODROMI_LETOVI TO APLIKACIJA;

INSERT INTO PUBLIC.AERODROMI_LETOVI (icao, aktivan) VALUES
    ('EDDF', true),
    ('EDDM', true),
    ('EGGP', true),
    ('EGLL', true),
    ('EIDW', true),
    ('EPWA', true),
    ('GCLP', true),
    ('HEGN', true),
    ('LDZA', true),
    ('LEBL', true),
    ('LEPA', true),
    ('LFPG', true),
    ('EDDS', true),
    ('LIPZ', true),
    ('LOWW', true),
    ('LTBJ', true),
    ('LSZH', true),
    ('LJLJ', true),
    ('OMDB', true);
   
TRUNCATE TABLE LETOVI_POLASCI;

SELECT * FROM PUBLIC.LETOVI_POLASCI;

SELECT COUNT(*) FROM PUBLIC.LETOVI_POLASCI;