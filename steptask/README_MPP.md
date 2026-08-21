# Compose Multiplatform başlangıç kılavuzu

Bu branch `feature/mpp-compose` — projeyi Kotlin Multiplatform ve Compose Multiplatform iskeleti ile genişletir.

Nasıl çalıştırılır (kısa):

- Desktop (JVM):
  - ./gradlew :desktop:run
  - veya native dağıtım: ./gradlew :desktop:packageReleaseExecutable

- Web (dev):
  - ./gradlew :web:browserDevelopmentRun
  - Üretilen statik paket: ./gradlew :web:build

Not: Bu ilk PR bir iskelet sağlar. Mevcut Android kodundan (app modülü) ortak kodlara taşınması için ek refactorlar gerekecektir.
