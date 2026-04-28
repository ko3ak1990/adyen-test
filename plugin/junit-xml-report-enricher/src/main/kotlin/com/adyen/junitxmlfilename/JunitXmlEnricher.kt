package com.adyen.junitxmlfilename

import org.w3c.dom.Element
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.io.path.name

class JunitXmlEnricher(
    private val sourceFileResolver: SourceFileResolver,
    private val lineExtractor: StackTraceLineExtractor = StackTraceLineExtractor()
) {
    fun enrichFile(inputXml: Path, outputXml: Path) {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = Files.newInputStream(inputXml).use { documentBuilder.parse(it) }

        val testcases = document.getElementsByTagName("testcase")
        for (i in 0 until testcases.length) {
            val testcase = testcases.item(i) as? Element ?: continue
            val className = testcase.getAttribute("classname")

            val filename = ensureFilename(testcase, className)
            ensureLine(testcase, filename)
        }

        Files.createDirectories(outputXml.parent)
        val transformer = TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
        }
        Files.newOutputStream(outputXml).use { output ->
            transformer.transform(DOMSource(document), StreamResult(output))
        }
    }

    private fun ensureFilename(testcase: Element, className: String): String? {
        val existing = testcase.getAttribute("filename")
        if (existing.isNotBlank()) return existing
        if (className.isBlank()) return null

        val resolved = sourceFileResolver.resolve(className)
        if (!resolved.isNullOrBlank()) {
            testcase.setAttribute("filename", resolved)
        }
        return resolved
    }

    private fun ensureLine(testcase: Element, filename: String?) {
        if (testcase.getAttribute("line").isNotBlank()) return

        val failureNodes = testcase.getElementsByTagName("failure")
        if (failureNodes.length == 0) return

        val stack = failureNodes.item(0)?.textContent ?: return
        val fileNameOnly = filename?.let { Path.of(it).name }
        val line = lineExtractor.extract(stack, fileNameOnly) ?: return
        testcase.setAttribute("line", line.toString())
    }
}

