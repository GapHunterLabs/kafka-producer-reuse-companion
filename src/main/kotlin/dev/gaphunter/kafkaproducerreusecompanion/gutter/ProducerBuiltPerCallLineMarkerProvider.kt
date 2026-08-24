package dev.gaphunter.kafkaproducerreusecompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.kafkaproducerreusecompanion.detect.JavaProducerBuildFinder
import dev.gaphunter.kafkaproducerreusecompanion.detect.KotlinProducerBuildFinder
import dev.gaphunter.kafkaproducerreusecompanion.model.ProducerBuildHit
import dev.gaphunter.kafkaproducerreusecompanion.review.ReviewPrompt

class ProducerBuiltPerCallLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "KafkaProducer built inside a method"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaProducerBuildFinder.findAll(file)
            "kotlin" -> KotlinProducerBuildFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.callElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: ProducerBuildHit): LineMarkerInfo<PsiElement> {
        val tooltip = "KafkaProducer is built here inside a method -- Kafka's own javadoc says \"the producer is " +
            "thread safe and sharing a single producer instance across threads will generally be faster\""
        return LineMarkerInfo(
            hit.callElement,
            hit.callElement.textRange,
            ProducerReuseIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
