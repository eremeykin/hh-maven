# Работа с maven
### Вариант 1
Разработка maven плагина, который делает дамп mysql базы данных.
Для работы плагина потребуется уставновленная утилита `mysqldump`.
Но так как плагин позволяет сконфигурировать команду, которая 
будет играть роль `mysqldump`, то его можно запустить в режиме имитации,
то есть вместо настоящей `mysqldump` подставить самописный скрипт 
`mysqldump.sh`, который имитирует создание дампа.

Репозиторий:
* `src/it` - интеграционный тест
* `src/main` - исходники плагина
* `src/test` - исходники тестов

Сборка и деплой: `mvn deploy`

Выполнение: `mvn dump:dump`

Краткая справка: `mvn dump:help`

Полная справка: `mvn dump:help -Ddetail=true -Dgoal=dump`



    dump:dump
      Создает дапм базы данных MySql. Для работы необходимо установленная утилита
      mysqldump. Для тестирования она заменена мок скриптом mysqldump.sh, который
      имитирует работу mysqldump.
      Available parameters:
    
        completeInsert (Default: true)
          Использовать полные выражения вставки.
    
        dbName (Default: database)
          Название базы данных
          Required: Yes
    
        exec (Default: mysqldump)
          Команда для исполнения mysqldump. Через этот параметр можно подменить
          выполнение настоящей mysqldump скриптом mysqldump.sh, который имитирует
          работу mysqldump.
    
        extendedInsert (Default: true)
          Использовать синтексис INSERT, который позволяет включать несколько
          списков значений.
    
        host (Default: localhost)
          Хост сервера СУБД
          Required: Yes
    
        outputFile (Default: dump.sql)
          Файл в который запишется дамп
          User property: mySqlDump
    
        password (Default: mysql)
          Пароль для подключения к серверу СУБД
          Required: Yes
    
        port (Default: 3306)
          Порт сервера СУБД
          Required: Yes
    
        quoteNames (Default: true)
          Экранировать названия таблиц и столбцов, например, `tablename`.`colname`
    
        singleTransaction (Default: true)
          В одной транзакции, только для InnoDB.
    
        userName (Default: root)
          Имя пользователя для подключения к серверу СУБД
          Required: Yes
