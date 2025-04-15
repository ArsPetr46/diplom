# Рекомендації щодо резервного копіювання проєкту

## 1. Стратегія резервного копіювання

### 1.1 Типи резервних копій
- **Повні копії**: повне копіювання всіх даних системи
    - Створювати щотижня
- **Інкрементальні копії**: лише зміни з часу останнього копіювання
    - Створювати щодня
- **Диференціальні копії**: всі зміни з часу останнього повного копіювання
    - Створювати двічі на тиждень

### 1.2 Частота створення резервних копій
- **База даних**:
    - Повна: щотижня
    - Інкрементальна: щодня
    - Миттєва копія перед важливими оновленнями
- **Конфігураційні файли**:
    - При кожній зміні конфігурації
    - Щотижня разом із повним копіюванням
- **Код проєкту**:
    - Щотижня разом із повним копіюванням
    - При кожному релізі

### 1.3 Зберігання та ротація копій
- Зберігати на окремому фізичному носії або в хмарі
- Схема ротації:
    - Щоденні копії: зберігати 7 днів
    - Щотижневі повні копії: зберігати 4 тижні
    - Щомісячні копії: зберігати 12 місяців
    - Щорічні копії: зберігати 5 років

## 2. Процедура резервного копіювання

### 2.1 База даних
- Повне копіювання PostgreSQL:
  ```bash
    pg_dump -U <DB_USER> -h <DB_HOST> -d <DB_NAME> -F c -f backup_<DB_NAME>_$(date +%F_%H-%M).dump
  ```
- Для інкрементального копіювання в PgSQL можна використати розширення WAL, для цього потрібно додати наступне в файл `postgresql.conf`:
  ```bash
    wal_level = replica
    archive_mode = on
    archive_command = 'cp %p /path/to/archive/%f'
  ```

### 2.2 Файли конфігурацій
- Копіювання конфігураційних файлів:
  ```bash
    tar -czvf config_backup_$(date +%F).tar.gz /src/main/resources/application*.properties
  ```

### 2.3 Користувацькі дані
- Користувацькі дані зберігаються в базі даних, тому їх резервне копіювання відбувається разом із базою даних.
- Також можливий експорт користувацьких даних через API, наприклад JSON файл з усіма користувачами:
  ```bash
      curl -X GET "http://localhost:8080/api/users" \
        -H "Authorization: Bearer $TOKEN" \
        -o users_export_$(date +%F).json
  ```

### 2.4 Логи системи
- Копіювання логів:
  ```bash
    find /var/log/app -name "*.log*" -mtime -7 | xargs tar -czvf logs_backup_$(date +%F).tar.gz
  ```

## 3. Перевірка цілісності резервних копій

- Перевірка цілісності бази даних:
  ```bash
    pg_restore -l backup_<DB_NAME>_*.dump > /dev/null && echo "Backup is valid"
  ```
- Перевірка архівів:
  ```bash
    tar -tvf config_backup_*.tar.gz > /dev/null && echo "Archive is valid"
  ```
- Тестове відновлення:
  ```bash
    pg_restore -C -d postgres backup_<DB_NAME>_*.dump
  ```

## 4. Автоматизація процесу резервного копіювання
Bash скрипт для автоматизації:
```bash
#!/bin/bash

DATE=$(date +%F)
TIME=$(date +%H-%M)
BACKUP_DIR="/path/to/backups"
DB_HOST="localhost"
DB_USER="postgres"
DB_NAME="mydatabase"
CONF_DIR="/src/main/resources"
LOG_DIR="/var/log/app"

mkdir -p $BACKUP_DIR/{db,config,logs}

pg_dump -U $DB_USER -h $DB_HOST -d $DB_NAME -F c -f $BACKUP_DIR/db/backup_${DB_NAME}_${DATE}_${TIME}.dump

tar -czvf $BACKUP_DIR/config/config_backup_${DATE}.tar.gz $CONF_DIR/application*.properties

tar -czvf $BACKUP_DIR/logs/logs_backup_${DATE}.tar.gz $LOG_DIR/*.log

find $BACKUP_DIR -type f -mtime +30 -name "*.dump" -delete
find $BACKUP_DIR -type f -mtime +30 -name "*.tar.gz" -delete

echo "Backup completed: ${DATE}_${TIME}"
```

## 5. Процедура відновлення з резервних копій

### 5.1 Повне відновлення системи
1. Зупинити всі сервіси:
   ```bash
   sudo systemctl stop <service-name>
   ```
2. Відновити базу даних:
   ```bash
   pg_restore -U $DB_USER -h $DB_HOST -d $DB_NAME -c -C backup_<DB_NAME>_<DATE>_<TIME>.dump
   ```
3. Відновити конфігурації:
   ```bash
   tar -xzvf config_backup_<DATE>.tar.gz -C /
   ```
4. Перезапустити сервіси:
   ```bash
   java -jar <service-name>.jar
   ```

### 5.2 Вибіркове відновлення даних
1. Відновлення окремих таблиць в БД:
   ```bash
   pg_restore -U $DB_USER -h $DB_HOST -d $DB_NAME -t <TABLE_NAME> backup_<DB_NAME>_<DATE>_<TIME>.dump
   ```
2. Відновлення конкретних файлів конфігурацій:
   ```bash
   tar -xzvf config_backup_<DATE>.tar.gz -C /tmp/
   cp /tmp/src/main/resources/application-prod.properties /src/main/resources/
   ```

### 5.3 Тестування відновлення
Тестування відновлення БД:
   ```bash
   createdb -U $DB_USER -h $DB_HOST test_restore
   
   pg_restore -U $DB_USER -h $DB_HOST -d test_restore backup_<DB_NAME>_<DATE>_<TIME>.dump
   
   psql -U $DB_USER -h $DB_HOST -d test_restore -c "SELECT count(*) FROM users;"
   ```