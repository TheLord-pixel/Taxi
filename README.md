# Taxi
# Это приложение Taxi, оно не настоящее (так называемое Fake Taxi), создано в рамках выполнения учебного задания.


Команды для чискти БД

docker exec -it taxi_postgres psql -U postgres -d database_taxi

DELETE FROM notifications;
DELETE FROM ratings;
DELETE FROM trips;
DELETE FROM drivers;
DELETE FROM passengers;

ALTER SEQUENCE passengers_id_seq RESTART WITH 1;
ALTER SEQUENCE drivers_id_seq RESTART WITH 1;
ALTER SEQUENCE trips_id_seq RESTART WITH 1;
ALTER SEQUENCE notifications_id_seq RESTART WITH 1;
ALTER SEQUENCE ratings_id_seq RESTART WITH 1;

Работа с докером

docker-compose down -v

docker rmi taxi-user-service taxi-trip-service taxi-notification-service

docker-compose up --build

docker-compose logs user-service

docker-compose logs trip-service

docker-compose logs notification-service

-- Посмотреть все таблицы
\dt

-- Посмотреть пассажиров
SELECT * FROM passengers;

-- Посмотреть водителей
SELECT * FROM drivers;

-- Посмотреть поездки
SELECT * FROM trips;

-- Посмотреть уведомления
SELECT * FROM notifications;

-- Посмотреть оценки
SELECT * FROM ratings;

-- Выйти
\q
\c database_taxi