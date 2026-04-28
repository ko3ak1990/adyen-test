package com.adyen.junitxmlfilename

import java.nio.file.Files
import java.nio.file.Path

object JunitXmlTaskProcessor {
    fun processTask(projectDir: Path, reportDir: Path, outputRoot: Path) {
        if (!Files.exists(reportDir)) return

        val sourceRoots = SourceFileResolver.discoverSourceRoots(projectDir)
        val resolver = SourceFileResolver(projectDir, sourceRoots)
        val enricher = JunitXmlEnricher(resolver)

        Files.walk(reportDir).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.fileName.toString().endsWith(".xml") }
                .forEach { input ->
                    val relative = reportDir.relativize(input)
                    val output = outputRoot.resolve(relative)
                    enricher.enrichFile(input, output)
                }
        }
    }
}
