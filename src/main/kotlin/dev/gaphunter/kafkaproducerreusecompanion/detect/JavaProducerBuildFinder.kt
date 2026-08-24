package dev.gaphunter.kafkaproducerreusecompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.util.PsiTreeUtil
import dev.gaphunter.kafkaproducerreusecompanion.model.ProducerBuildHit

/**
 * Finds `new KafkaProducer<>(...)` constructions written inside a
 * non-constructor method body -- Apache Kafka's own javadoc, unchanged
 * across every released version, states "The producer is thread safe
 * and sharing a single producer instance across threads will
 * generally be faster than having multiple instances." Building one
 * inside a regular method means the real connection/initialization
 * cost is paid on every call, and defeats internal batching across
 * calls.
 *
 * **v0.1 scope, stated honestly:** only `KafkaProducer` is checked --
 * `KafkaConsumer` is deliberately excluded (unlike the producer, a
 * single consumer instance is not safe to use from multiple threads
 * concurrently, so "build once, reuse" isn't the same unqualified
 * recommendation there). Never flags a construction inside a
 * constructor or a field initializer.
 */
object JavaProducerBuildFinder {

    fun findAll(file: PsiFile): List<ProducerBuildHit> {
        val hits = mutableListOf<ProducerBuildHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitNewExpression(expression: PsiNewExpression) {
                super.visitNewExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(newExpr: PsiNewExpression): ProducerBuildHit? {
        val className = newExpr.classReference?.referenceName ?: return null
        if (className != "KafkaProducer") return null

        val containingMethod = PsiTreeUtil.getParentOfType(newExpr, PsiMethod::class.java) ?: return null
        if (containingMethod.isConstructor) return null

        return ProducerBuildHit(leafOf(newExpr))
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
