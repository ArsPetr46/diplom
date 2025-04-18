# Інструкція з розгортання проєкту у виробничому середовищі

## Вимоги до апаратного забезпечення
- **Архітектура**: x86-64
- **Мінімальні вимоги**:
    - **CPU**: 4 ядра 2.0 Ghz+
    - **Оперативна пам'ять**: 8 ГБ
    - **Диск**: 10 ГБ вільного місця (SSD рекомендовано)
- **Мережа**: стабільне підключення зі швидкістю не менше 50 Мбіт/с

## Необхідне програмне забезпечення
- **Операційна система**: Linux (рекомендовано Ubuntu 20.04 або новіше)
- **Java**: OpenJDK 23
- **Maven**: 4.0.0 або новіше
- **СУБД**: PostgreSQL 13

## Налаштування мережі
- Відкрити порти:
    - **8080**: для доступу до API
    - **5432**: для підключення до PostgreSQL
- Налаштувати брандмауер для захисту серверу від небажаних з'єднань.
- Забезпечити використання HTTPS та наявність валідного SSL сертифікату.

## Конфігурація серверів
1. **Сервери сервісів**:
    - Встановити OpenJDK 23.
    - Налаштувати змінні середовища:
      ```ini
      DB_URL=jdbc:postgresql://<DB_HOST>:5432/<DB_NAME>
      DB_USERNAME=<DB_USER>
      DB_PASSWORD=<DB_PASSWORD>
      ```

2. **Сервер бази даних**:
    - Встановити PostgreSQL 13.
    - Налаштувати резервне копіювання бази даних, наприклад за допомогою pg_dump.
    - Забезпечити доступ лише з IP сервісів.

## Налаштування СУБД
1. Створити базу даних:
    ```sql
    CREATE DATABASE <DB_NAME>;
    ```
2. ORM Hibernate автоматично створює таблиці в базі даних.
3. Налаштувати права доступу для користувача:
    ```sql
    GRANT ALL PRIVILEGES ON DATABASE <DB_NAME> TO <DB_USER>;
    ```
4. Налаштувати postgresql.conf для оптимізації продуктивності:
    ```ini
    max_connections = 100
    shared_buffers = 2GB
    work_mem = 64MB
    maintenance_work_mem = 512MB
    effective_cache_size = 4GB
    ```
   
## Розгортання коду
1. Клонувати репозиторій:
    ```bash
    git clone https://github.com/ArsPetr46/diplom.git
    cd diplom
   ```
2. Розділити проєкт на відповідні мікросервіси:
    - auth-service
    - chat-service
    - user-service
    - notification-service
    - messaging-service
    - history-service
    - application-service
3. Побудувати кожний мікросервіс:
    ```bash
    ./mvnw clean install
    ```
4. Запустити мікросервіси:
    ```bash
    ./java -jar target/<service-name>.jar
    ```
   
## Перевірка працездатності
1. Перевірити доступність API:
    ```bash
    curl -X GET http://<SERVER_IP>:8080/api/users
    ```
   В результаті має повернутися список користувачів.
2. Окремо перевірити запити на інші мікросервіси користуючись документацією Swagger.