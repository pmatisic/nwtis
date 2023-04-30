SELECT ADM.COUNTRY, MAX(ADM.DIST_CTRY) AS MAX_DIST_CTRY
FROM AIRPORTS_DISTANCE_MATRIX ADM
WHERE ADM.ICAO_FROM = 'EDDF'
GROUP BY ADM.COUNTRY
ORDER BY MAX_DIST_CTRY DESC
LIMIT 1;
