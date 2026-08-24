# Kafka Producer Reuse Companion

Gutter warning icon on a `new KafkaProducer<>(...)` construction
written inside a regular method body — Apache Kafka's own javadoc,
unchanged across every released version, states "The producer is
thread safe and sharing a single producer instance across threads
will generally be faster than having multiple instances." Building
one inside a method means the real connection/initialization cost is
paid on every call, and defeats internal batching across calls.

## Why it exists

`new KafkaProducer<>(props)` reads like a harmless local variable, but
Kafka's own docs are explicit that it's meant to be a shared,
long-lived instance — every construction pays real connection setup
cost and loses the batching benefits a shared producer gets. Nothing
in the IDE flags a producer built the wrong way today.

## Why built this way

- **100% static text/PSI analysis** — matches the class name by simple
  text, so it works whether the real Kafka client jar is on the
  classpath or not. Java and Kotlin.

## v0.1 scope — stated honestly, not exhaustively

Only `KafkaProducer` is checked — `KafkaConsumer` is deliberately
excluded (unlike the producer, a single consumer instance is not safe
to use from multiple threads concurrently, so "build once, reuse"
isn't the same unqualified recommendation there). Never flags a
construction inside a constructor or a field/property initializer.

## Usage

Open any Java/Kotlin file using the Kafka client. A producer built
inside a regular method shows a warning icon.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
