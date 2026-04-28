package com.adyen.junitxmlfilename

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.nio.file.Files

class JunitXmlEnricherTest {
    @Test
    fun `adds filename attribute when source exists`() {
        val root = Files.createTempDirectory("enricher-root")
        val sourceRoot = root.resolve("src/test/kotlin")
        val source = sourceRoot.resolve("com/adyen/demo/ExampleTest.kt")
        Files.createDirectories(source.parent)
        Files.writeString(source, "class ExampleTest")

        val input = root.resolve("input.xml")
        Files.writeString(
            input,
            """
            <testsuite>
              <testcase name="testExample" classname="com.adyen.demo.ExampleTest" time="0.1"/>
            </testsuite>
            """.trimIndent()
        )
        val output = root.resolve("out/result.xml")

        val resolver = SourceFileResolver(root, listOf(sourceRoot))
        JunitXmlEnricher(resolver).enrichFile(input, output)

        val xml = Files.readString(output)
        assertTrue(xml.contains("filename=\"src/test/kotlin/com/adyen/demo/ExampleTest.kt\""))
    }

    @Test
    fun `preserves existing filename attribute`() {
        val root = Files.createTempDirectory("enricher-existing")
        val input = root.resolve("input.xml")
        Files.writeString(
            input,
            """
            <testsuite>
              <testcase name="testExample" classname="com.adyen.demo.ExampleTest" filename="already.kt"/>
            </testsuite>
            """.trimIndent()
        )
        val output = root.resolve("out/result.xml")

        val resolver = SourceFileResolver(root, emptyList())
        JunitXmlEnricher(resolver).enrichFile(input, output)

        val xml = Files.readString(output)
        assertTrue(xml.contains("filename=\"already.kt\""))
    }

    @Test
    fun `omits filename when source missing`() {
        val root = Files.createTempDirectory("enricher-missing")
        val input = root.resolve("input.xml")
        Files.writeString(
            input,
            """
            <testsuite>
              <testcase name="testExample" classname="com.adyen.demo.ExampleTest"/>
            </testsuite>
            """.trimIndent()
        )
        val output = root.resolve("out/result.xml")

        val resolver = SourceFileResolver(root, emptyList())
        JunitXmlEnricher(resolver).enrichFile(input, output)

        val xml = Files.readString(output)
        assertFalse(xml.contains("filename="))
    }
}

