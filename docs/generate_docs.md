# Генерація документації проєкту

## Загальна інформація

Для документування коду в нашому проєкті ми використовуємо стандарт JavaDoc. Детальні правила документування та приклади можна знайти в [розділі про документування коду](../README.md#документування-коду) в основному README файлі проєкту.

## Налаштування IDE

### Встановлення плагіну JavaDoc

Д��я зручної роботи з JavaDoc необхідно встановити відповідний плагін у вашій IDE:

#### IntelliJ IDEA
1. IntelliJ IDEA має вбудовану підтримку JavaDoc, тому додаткових плагінів не потрібно
2. Для швидкого створення шаблону документації введіть `/**` над методом чи класом і натисніть Enter

#### Eclipse
1. Відкрийте Eclipse Marketplace: Help > Eclipse Marketplace
2. Знайдіть "JavaDoc" і встановіть плагін "JavaDoc Tools"
3. Перезавантажте Eclipse

#### VS Code
1. Відкрийте Extensions (Ctrl+Shift+X)
2. Знайдіть і встановіть "Java Extension Pack"
3. Додатково встановіть розширення "Javadoc Tools"

## Генерація документації

Для генерації документації використовуйте стандартні налаштування JavaDoc:

1. Через Maven:
   ```bash
   mvn javadoc:javadoc
   ```

2. Через IDE:
    - IntelliJ IDEA: Tools > Generate JavaDoc
    - Eclipse: Project > Generate Javadoc

## Розміщення документації

Згенерована документація повинна зберігатись у теці `documentation` в корені проєкту. Не додавайте цю теку до системи контролю версій (вона вже додана до `.gitignore`).

При генерації документації вказуйте цю теку як вихідну директорію:

```bash
mvn javadoc:javadoc -DoutputDirectory=./documentation
```

## Перевірка якості документації

Перед відправленням коду на code review переконайтесь, що:

1. Вся публічна частина API має документацію
2. Документація відповідає стандартам, описаним у README
3. Немає помилок при генерації документації

## Перегляд документації

Після генерації ви можете відкрити `documentation/index.html` у будь-якому браузері для перегляду згенерованої документації.