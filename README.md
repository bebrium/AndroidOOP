# Android-часть проекта

Клиентская часть ПАК: сбор координат LLA и параметров радиосигнала (PCI, EARFCN, RSRP, RSRQ, RSSI) и передача их на C++-сервер по ZMQ.
Репозиторий: https://github.com/bebrium/AndroidOOP (серверная часть — https://github.com/bebrium/BackendFinal).

---

## Лабы 1–3. Kotlin: «ходячий», наследование, интерфейсы

**Movable.kt** — интерфейс (контракт): каждый подвижный объект обязан иметь координаты, скорость и метод `move()`.

**Human.kt** — базовый класс, реализует Movable. Движется по модели «случайное блуждание» — на каждом шаге направление выбирается случайно:

```
x_новое = x_старое + (СлучайноеЧисло - 0.5) * Скорость
y_новое = y_старое + (СлучайноеЧисло - 0.5) * Скорость
```

**Driver.kt** — наследник Human, переопределяет `move()` для прямолинейного движения:

```
x_новое = x_старое + Скорость
y_новое = y_старое
```

**Main1.kt** — точка входа: создаёт массив объектов (Human + Driver) и заставляет их двигаться одновременно, каждый в своём `Thread`.

---

## Лаба 4. Калькулятор

**Calculator.kt** — кнопки цифр 0–9, кнопки действий `+ − * /` и `=`, TextView для вывода выражения и результата. При нажатии `=` строка из TextView обрабатывается вручную: находится символ операции, строка делится на два операнда, выполняется одна операция, результат выводится на экран.

---

## Лаба 4.5. Hub (MainActivity)

**MainActivity.kt** — главный экран-хаб: кнопки перехода на Calculator, MediaPlayer, GpsActivity и ZMQActivity через `Intent` (`startActivity(Intent(this, X::class.java))`).

---

## Лаба 5. Медиа-плеер

**MediaPlayer.kt** — проигрыватель музыки из хранилища телефона. Реализовано: воспроизведение и пауза трека, остановка при переходе Activity в `onPause()`, регулировка громкости, SeekBar с текущей длительностью и перемоткой, проверка файла на директорию (`isDirectory`), список треков на экране. Доступ к файлам — через runtime Permission.

---

## Лаба 6. GPS (GpsActivity)

**GpsActivity.kt** — выводит местоположение смартфона: Latitude, Longitude, Altitude, Current Time. Разрешения `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`; данные берутся через `getLastLocation()`, при каждом обновлении пишутся в JSON-файл.

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

---

## Лаба 8. ZMQ (ZMQActivity)

**ZMQActivity.kt** — клиент ZMQ (JeroMQ). Телефон подключается к серверу на ПК (`tcp://IP:25566`) и шлёт пакеты; сервер отвечает «OK». Если ответа нет 3 секунды — сокет пересоздаётся (REQ при сбое переходит в состояние EFSM). Телефон и ПК в одной Wi-Fi сети; для сервера в WSL2 настроен проброс порта.

```kotlin
zmqSocket = zmqContext?.createSocket(SocketType.REQ)
zmqSocket?.setReceiveTimeOut(3000)
zmqSocket?.connect("tcp://$SERVER_IP:$SERVER_PORT")
zmqSocket?.send(packet.toString().toByteArray(), 0)
val reply = zmqSocket?.recv(0)
```

---

## Лаба 10. Cellinfo

Сбор данных о базовых станциях через `TelephonyManager.allCellInfo`: LTE — PCI, EARFCN, RSRP, RSRQ, RSSI; GSM — CID, LAC, dBm; NR (5G) — скрытые поля (`mPci`, `mNrarfcn`) читаются рефлексией. Всё вместе с координатами пакуется в один атомарный JSON-пакет и отправляется на сервер:

```json
{ "Latitude": 55.0234, "Longitude": 82.9234, "Altitude": 150.0,
  "Accuracy": 3.0, "Current Time": 1756641600000,
  "Net Type": "LTE", "Primary_RSRP": -87,
  "Cells": [ {"Type":"LTE","PCI":204,"EARFCN":1475,
              "RSRP":-92,"RSRQ":-11,"RSSI":-67} ] }
```

---

## Лаба 11. Фоновый сервис (TelemetryService)

**TelemetryService.kt** — foreground-сервис с постоянным уведомлением и типом `location` (обязательно для Android 14, иначе краш). Собирает GPS (активные обновления через `LocationListener`, а не кэшированный `getLastKnownLocation`) и данные о вышках, шлёт на сервер каждые 3 секунды даже с выключенным экраном. При обрыве связи пакеты не теряются: пишутся в буфер `telemetry_buffer.jsonl` и автоматически досылаются после восстановления.

```kotlin
startForeground(NOTIFICATION_ID, createNotification(),
    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
```
