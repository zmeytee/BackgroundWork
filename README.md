# Выполнение задач в фоне (учебный проект)

Цель задания

    Поработать с Foreground Service.
    Научиться запускать отложенные задачи с помощью WorkManager.
    Поработать с условиями запуска задач, цепочками задач, стратегиями перезапуска задач.


Что нужно сделать

    Создайте Worker для загрузки файлов по URL. Файлы скачиваются и сохраняются в app specific external storage.
    Задача принимает на вход URL, по которому нужно скачать файл. URL вводится пользователем. Пользователь нажимает на кнопку «Загрузить», после чего задача становится в очередь для выполнения.
    Задача должна запускаться в том случае, если есть доступ к вайфаю и уровень заряда батареи не низкий.
    Если произошла ошибка сети при скачивании файла, то скачивание должно повториться снова через промежуток времени, который увеличивается линейно, delay = 20 секунд.
    На экране с вводом URL обработайте статусы задачи:
        Ожидание загрузки — показывается текст.
        Процесс загрузки — показывается ProgressBar.
        Ошибка загрузки — показывается ошибка с кнопкой Retry.
        Успех загрузки — отображается Toast.
    Пока задача скачивания выполняется, не должно быть возможности начать новое скачивание. Даже при перезапуске приложения. Для этого используйте ограничение UI (заблокированная кнопка загрузки) и уникальную работу с политикой ExistingWorkPolicy.KEEP.
    Добавьте возможность отмены задачи. Кнопка отмены отображается только в случае ожидания загрузки или в случае прогресса загрузки. То есть в состояниях 1, 2 из пункта 5.
    По желанию: настройте работу, которая выполняется раз в определённый период.
