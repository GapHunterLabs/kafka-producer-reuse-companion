# Demo data for screenshots

`OrderEventPublisher.java` — the constructor builds `sharedProducer`
once (not flagged), `publish` builds a brand new producer on every
call (flagged).

## How to get the screenshot

1. `./gradlew runIde` from `kafka-producer-reuse-companion`, open
   this `demo/` folder as the project.
2. Full Screen, open `OrderEventPublisher.java` — a warning icon
   should appear on the `new KafkaProducer<>(props)` call in
   `publish` only.
3. Screenshot with both methods visible, save into
   `kafka-producer-reuse-companion/docs/screenshots/`. Close the
   sandbox.
