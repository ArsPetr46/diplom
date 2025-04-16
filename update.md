# Рекомендації щодо оновлення проєкту

## 1. Підготовка до оновлення

### 1.1 Створення резервних копій
- Виконати резервне копіювання бази даних:
  ```bash
    pg_dump -U <DB_USER> -h <DB_HOST> <DB_NAME> > backup_<DB_NAME>_$(date +%F).sql
  ```
- Зберегти резервну копію коду проєкту:
  ```bash 
    git archive -o project_backup_$(date +%F).zip HEAD
    ```
- Зберегти резервну копію конфігураційних файлів:
  ```bash
    cp -r /src/main/resources/application.properties /path/to/backup/config_$(date +%F)
  ```
  
### 1.2 Перевірка сумісності
- Перевірити сумісність нової версії з поточним середовищем через документацію відповідних технологій (Java, PostgreSQL, Spring).
- Виконати тестове розгортання в IDE (див. `Readme.md`).
- Перевірити версії залежностей в `pom.xml`.

### 1.3 Планування часу простою
- Проєкт не передбачає широкого використання в реальному часі, тому оновлення можна виконати в будь-який зручний час.
- Для повідомлення користувачів про оновлення можна створити окрему сторінку з необхідною інформацією.

## 2. Процес оновлення

### 2.1 Зупинка потрібних служб
- Зупинити сервер бази даних PostgreSQL:
  ```bash
    sudo systemctl stop postgresql
  ```
- Зупинити всі сервіси:
  ```bash
    sudo systemctl stop <service_name>
  ```

### 2.2 Розгортання нового коду
- Завантажити нову версію коду з репозиторію:
  ```bash
    git pull origin main
  ```
- Перевірити, чи немає конфліктів з локальними змінами.
- Виконати команду для оновлення залежностей:
  ```bash
    ./mvnw dependency:resolve
  ```
  Або одразу для оновлення та компіляції проєкту:
  ```bash
    ./mvnw clean install
  ```

- Замінити старі Jar файли та запустити сервіси:
  ```bash
    cp target/<service-name>.jar /path/to/deployment
    java -jar /path/to/deployment/<service-name>.jar
  ```
- Протестувати API ендпоінти.

### 2.3 Оновлення конфігурацій
- Перевірити, чи потрібно оновити конфігураційні файли.
- Внести зміни в `target/classes/application.properties` за потребою.
- Перевірити змінні середовища.

## 3. Процедура відкату

### 3.1 Зупинка потрібних служб
- Зупинити сервер бази даних PostgreSQL:
  ```bash
    sudo systemctl stop postgresql
  ```
- Зупинити сервіси:
  ```bash
    sudo systemctl stop <service_name>
  ```
  
### 3.2 Відновлення резервних копій
- Відновлення коду сервера:
  ```bash
    git checkout <previous-tag>
    ./mvnw clean install
    cp target/<project-name>.jar /path/to/deployment/
  ```
- Відновлення бази даних:
  ```bash
    psql -U <DB_USER> -h <DB_HOST> -d <DB_NAME> -f backup_<DB_NAME>_<DATE>.sql
  ```
- Відновлення конфігураційних файлів:
  ```bash
    cp -r /path/to/backup/config_<DATE> /path/to/config
  ```
  
### 3.3 Перезапуск служб
- Запустити сервер бази даних PostgreSQL:
  ```bash
    sudo systemctl start postgresql
  ```
- Запустити сервіси:
  ```bash
    java -jar /path/to/deployment/<service-name>.jar
  ```
  
### 3.4 Документування
- Задокументувати причини відкату та вжиті заходи.
- Спланувати наступні кроки для усунення проблем.