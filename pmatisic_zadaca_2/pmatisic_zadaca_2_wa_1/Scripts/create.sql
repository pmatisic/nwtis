CREATE TABLE LETOVI_POLASCI (
  id integer not null generated always as identity (start with 1, increment by 1),
  icao24 varchar(30) NOT NULL,
  firstSeen integer not null,
  estDepartureAirport varchar (10) NOT NULL,
  lastSeen integer not null,
  estArrivalAirport varchar (10) NOT NULL,
  callsign varchar (20),
  estDepartureAirportHorizDistance integer not null,
  estDepartureAirportVertDistance integer not null,
  estArrivalAirportHorizDistance integer not null,
  estArrivalAirportVertDistance integer not null,
  departureAirportCandidatesCount integer not null,
  arrivalAirportCandidatesCount integer not null,
  stored TIMESTAMP NOT NULL, 
  PRIMARY KEY (id),
  FOREIGN KEY (estDepartureAirport) REFERENCES airports (icao),
  CONSTRAINT LETOVI_POLASCI_icao24_firstSeen UNIQUE (icao24, firstSeen)
);

GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE LETOVI_POLASCI TO APLIKACIJA;