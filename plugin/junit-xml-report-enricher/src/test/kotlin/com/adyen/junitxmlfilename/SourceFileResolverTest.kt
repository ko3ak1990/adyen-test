package com.adyen.junitxmlfilename

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.nio.file.Files

class SourceFileResolverTest {
    @Test
    fun `finds kotlin source by classname`() {
        val root = Files.createTempDirectory("resolver-root")
        val sourceRoot = root.resolve("src/test/kotlin")
        val file = sourceRoot.resolve("com/adyen/demo/SampleTest.kt")
        Files.createDirectories(file.parent)
        Files.writeString(file, "class SampleTest")

        val resolver = SourceFileResolver(root, listOf(sourceRoot))
        val resolved = resolver.resolve("com.adyen.demo.SampleTest")

        assertEquals("src/test/kotlin/com/adyen/demo/SampleTest.kt", resolved)
    }

    @Test
    fun `handles inner class names`() {
        val root = Files.createTempDirectory("resolver-inner")
        val sourceRoot = root.resolve("src/test/java")
        val file = sourceRoot.resolve("com/adyen/demo/InnerTest.java")
        Files.createDirectories(file.parent)
        Files.writeString(file, "class InnerTest {}")

        val resolver = SourceFileResolver(root, listOf(sourceRoot))
        val resolved = resolver.resolve("com.adyen.demo.InnerTest\$Nested")

        assertEquals("src/test/java/com/adyen/demo/InnerTest.java", resolved)
    }

    @Test
    fun `returns null when source missing`() {
        val root = Files.createTempDirectory("resolver-missing")
        val sourceRoot = root.resolve("src/test/kotlin")
        Files.createDirectories(sourceRoot)

        val resolver = SourceFileResolver(root, listOf(sourceRoot))
        assertNull(resolver.resolve("com.adyen.demo.Nope"))
    }
}
