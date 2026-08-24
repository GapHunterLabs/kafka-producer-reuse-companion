package dev.gaphunter.kafkaproducerreusecompanion.detect

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import dev.gaphunter.kafkaproducerreusecompanion.model.ProducerBuildHit
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaProducerBuildFinder]. */
object KotlinProducerBuildFinder {

    fun findAll(file: PsiFile): List<ProducerBuildHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<ProducerBuildHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(call: KtCallExpression): ProducerBuildHit? {
        if (call.calleeExpression?.text != "KafkaProducer") return null

        if (PsiTreeUtil.getParentOfType(call, KtConstructor::class.java) != null) return null
        if (PsiTreeUtil.getParentOfType(call, KtNamedFunction::class.java) == null) return null

        return ProducerBuildHit(leafOf(call))
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
