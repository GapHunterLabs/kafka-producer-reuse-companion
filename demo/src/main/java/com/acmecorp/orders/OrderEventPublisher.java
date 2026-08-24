package com.acmecorp.orders;

public class OrderEventPublisher {

    private final KafkaProducer<String, String> sharedProducer;

    // Built once, in the constructor -- not flagged.
    OrderEventPublisher(Properties props) {
        this.sharedProducer = new KafkaProducer<>(props);
    }

    // Built again on every call inside a regular method -- flagged.
    void publish(String orderId, Properties props) {
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>("orders", orderId, "created"));
    }
}
