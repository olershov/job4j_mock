job4j_mock
==============

## Описание
Платформа для проведения пробных собеседований. Программист имеет возможность выбрать нужный раздел, например, Java.
Затем выбирает интересующую тему собеседования и создаёт его. На собеседовании он может быть в роли интервьюера и 
интервьюируемого. Другие пользователи могут откликнуться на собеседование в противоположной роли. Автор выбирает 
одного из откликнувшихся и проводит собеседование. По результатам оба участника оставляют друг другу отзывы о
знаниях выбранной темы.

## Запуск приложения через docker-compose
* Установить Docker и Docker-compose
* Склонировать проект с репозитория:
 `git clone https://github.com/olershov/job4j_mock.git`
* Перейти в директорию с приложением:
  `cd job4j_mock`
* Создать образы проекта:
  `docker compose build`
* Поднять образы проекты:
  `docker compose up`

## Вариант запуска через выполнение bash-скрипта (данный способ остановит и удалит ранее созданные контейнеры и образы и соберет всё заново)
* Также убедитесь что у вас установлены Docker и Docker-compose
* Склонировать проект с репозитория:
  `git clone https://github.com/olershov/job4j_mock.git`
* Перейти в директорию с приложением:
  `cd job4j_mock`
* Запустите выполнение скрипта в файле docker_script.sh
  `bash ./docker_script.sh`

  
## Контактная информация:
<a href="https://t.me/ol_ruff">
<img alt="Static Badge" src="https://img.shields.io/badge/Telegram-blue?style=social&logo=telegram&logoColor=rgb&labelColor=hex&color=hex">
</a>
