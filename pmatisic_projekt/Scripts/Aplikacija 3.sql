# Aerodromi za preuzimanje
SELECT * FROM AERODROMI_LETOVI;



# Broj preuzetih podataka
SELECT COUNT(*) FROM LETOVI_POLASCI;



# Broj preuzetih podataka po danima za sve aerodrome sortirano po danu:
SELECT CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE) AS date, COUNT(*) AS total_departures
FROM LETOVI_POLASCI
WHERE estDepartureAirport IN ('EBBR', 'EDDF', 'EDDM', 'EGGP', 'EGLL', 'EIDW', 'EPWA', 'GCLP', 'HEGN', 'LDZA', 'LEBL', 'LEPA', 'LFPG', 'EDDS', 'LIPZ', 'LOWW', 'LTBJ', 'LSZH', 'LJLJ', 'OMDB')
GROUP BY CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE)
ORDER BY CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE);



DELETE FROM LETOVI_POLASCI
WHERE TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') > TIMESTAMP '2022-11-29 23:59:59';



# Broj preuzetih podataka po danima za sve aerodrome pojedinačno i sortirano po aerodromu i danu:
SELECT estDepartureAirport,
       CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE) AS flight_date,
       COUNT(*) AS total_departures
FROM LETOVI_POLASCI
WHERE estDepartureAirport IN ('EBBR', 'EDDF', 'EDDM', 'EGGP', 'EGLL', 'EIDW', 'EPWA', 'GCLP', 'HEGN', 'LDZA', 'LEBL', 'LEPA', 'LFPG', 'EDDS', 'LIPZ', 'LOWW', 'LTBJ', 'LSZH', 'LJLJ', 'OMDB')
GROUP BY estDepartureAirport, CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE)
ORDER BY estDepartureAirport, CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE);



# Broj preuzetih podataka po danima za odabrani aerodrom sortirano po danu (primjer za LDZA):
SELECT estDepartureAirport,
       CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE) AS flight_date,
       COUNT(*) AS total_departures
FROM LETOVI_POLASCI
WHERE estDepartureAirport = 'LDZA'
GROUP BY estDepartureAirport, CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE)
ORDER BY CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE);



# Broj preuzetih podataka za sve aerodrome pojedinačno sortirano po aerodromu na odabrani dan (primjer za 01.01.2022):
SELECT estDepartureAirport,
       CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE) AS flight_date,
       COUNT(*) AS total_departures
FROM LETOVI_POLASCI
WHERE CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE) = '2022-01-01'
  AND estDepartureAirport IN ('EBBR', 'EDDF', 'EDDM', 'EGGP', 'EGLL', 'EIDW', 'EPWA', 'GCLP', 'HEGN', 'LDZA', 'LEBL', 'LEPA', 'LFPG', 'EDDS', 'LIPZ', 'LOWW', 'LTBJ', 'LSZH', 'LJLJ', 'OMDB')
GROUP BY estDepartureAirport, CAST(TIMESTAMPADD(SQL_TSI_SECOND, firstSeen, TIMESTAMP '1970-01-01 00:00:00') AS DATE)
ORDER BY estDepartureAirport;