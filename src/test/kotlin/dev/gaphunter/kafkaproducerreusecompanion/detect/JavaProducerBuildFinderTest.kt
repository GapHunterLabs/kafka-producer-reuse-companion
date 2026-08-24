package dev.gaphunter.kafkaproducerreusecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaProducerBuildFinderTest : BasePlatformTestCase() {

    fun `test a producer built inside a regular method is flagged`() {
        val file = myFixture.configureByText(
            "OrderEventPublisher.java",
            """
            class OrderEventPublisher {
                void publish(String orderId) {
                    KafkaProducer<String, String> producer = new KafkaProducer<>(props);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaProducerBuildFinder.findAll(file).size)
    }

    fun `test a producer built inside a constructor is not flagged`() {
        val file = myFixture.configureByText(
            "OrderEventPublisher.java",
            """
            class OrderEventPublisher {
                private final KafkaProducer<String, String> producer;
                OrderEventPublisher() {
                    producer = new KafkaProducer<>(props);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaProducerBuildFinder.findAll(file).isEmpty())
    }

    fun `test an unrelated new expression is never flagged`() {
        val file = myFixture.configureByText(
            "OrderEventPublisher.java",
            """
            class OrderEventPublisher {
                void publish() {
                    StringBuilder sb = new StringBuilder();
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaProducerBuildFinder.findAll(file).isEmpty())
    }
}
