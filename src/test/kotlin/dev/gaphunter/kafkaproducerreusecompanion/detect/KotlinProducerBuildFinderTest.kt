package dev.gaphunter.kafkaproducerreusecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinProducerBuildFinderTest : BasePlatformTestCase() {

    fun `test a producer built inside a regular function is flagged`() {
        val file = myFixture.configureByText(
            "OrderEventPublisher.kt",
            """
            class OrderEventPublisher {
                fun publish(orderId: String) {
                    val producer = KafkaProducer<String, String>(props)
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinProducerBuildFinder.findAll(file).size)
    }

    fun `test a producer built as a class property is not flagged`() {
        val file = myFixture.configureByText(
            "OrderEventPublisher.kt",
            """
            class OrderEventPublisher {
                val producer = KafkaProducer<String, String>(props)
            }
            """.trimIndent(),
        )
        assertTrue(KotlinProducerBuildFinder.findAll(file).isEmpty())
    }

    fun `test an unrelated call expression is never flagged`() {
        val file = myFixture.configureByText(
            "OrderEventPublisher.kt",
            """
            class OrderEventPublisher {
                fun publish() {
                    val sb = StringBuilder()
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinProducerBuildFinder.findAll(file).isEmpty())
    }
}
