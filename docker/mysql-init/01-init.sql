-- Bases necessaires aux deux microservices (le conteneur MySQL n'en cree qu'une par defaut)
CREATE DATABASE IF NOT EXISTS clientservicedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS carservicedb    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
