-- mpa_ratings
INSERT INTO mpa_ratings (id, code, name, description)
SELECT 1, 'G', 'G', 'General Audiences: Нет возрастных ограничений'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'G');

INSERT INTO mpa_ratings (id, code, name, description)
SELECT 2, 'PG', 'PG', 'Parental Guidance: Рекомендуется присутствие родителей'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'PG');

INSERT INTO mpa_ratings (id, code, name, description)
SELECT 3, 'PG_13', 'PG-13', 'Parents Strongly Cautioned: Не рекомендовано до 13'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'PG-13');

INSERT INTO mpa_ratings (id, code, name, description)
SELECT 4, 'R', 'R', 'Restricted: До 17 лет с родителями'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'R');

INSERT INTO mpa_ratings (id, code, name, description)
SELECT 5, 'NC_17', 'NC-17', 'Adults Only: До 18 лет запрещено'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'NC-17');

-- genres
INSERT INTO genres (id, code, name, description)
SELECT 1, 'COMEDY', 'Комедия', ''
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE code = 'COMEDY');

INSERT INTO genres (id, code, name, description)
SELECT 2, 'DRAMA', 'Драма', ''
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE code = 'DRAMA');

INSERT INTO genres (id, code, name, description)
SELECT 3, 'ANIMATION', 'Мультфильм', ''
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE code = 'ANIMATION');

INSERT INTO genres (id, code, name, description)
SELECT 4, 'THRILLER', 'Триллер', ''
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE code = 'THRILLER');

INSERT INTO genres (id, code, name, description)
SELECT 5, 'DOCUMENTARY', 'Документальный', ''
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE code = 'DOCUMENTARY');

INSERT INTO genres (id, code, name, description)
SELECT 6, 'ACTION', 'Боевик', ''
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE code = 'ACTION');

-- friendship_status
INSERT INTO friendship_status (id, code, name, description)
SELECT 1, 'PENDING', 'Запрошена', ''
WHERE NOT EXISTS (SELECT 1 FROM friendship_status WHERE code = 'PENDING');

INSERT INTO friendship_status (id, code, name, description)
SELECT 2, 'CONFIRMED', 'Подтверждена', ''
WHERE NOT EXISTS (SELECT 1 FROM friendship_status WHERE code = 'CONFIRMED');

