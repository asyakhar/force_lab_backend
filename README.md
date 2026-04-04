# Информационная система «FORCE LAB» (Backend)

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?logo=jsonwebtokens&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.5-02303A?logo=gradle&logoColor=white)

**FORCE LAB (Backend)** — это серверная часть платформы для управления спортивной деятельностью в многопрофильном спортивном клубе. Система обеспечивает централизованное управление тренировочным процессом, учетом спортсменов и тренеров, планированием тренировок и отслеживанием прогресса.


## Установка и запуск

```bash
# Клонирование репозитория
git clone https://github.com/asyakhar/force_lab_backend

# Создание базы данных
psql -U postgres
CREATE DATABASE sport_club;

# Сборка и запуск
./gradlew clean build
./gradlew bootRun
```

