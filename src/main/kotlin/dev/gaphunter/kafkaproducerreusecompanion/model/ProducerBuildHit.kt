package dev.gaphunter.kafkaproducerreusecompanion.model

import com.intellij.psi.PsiElement

/** One `new KafkaProducer<>(...)` construction found inside a non-constructor method body. */
data class ProducerBuildHit(val callElement: PsiElement)
