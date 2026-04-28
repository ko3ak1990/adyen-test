package com.adyen.junitxmlfilename

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StackTraceLineExtractorTest {
    private val extractor = StackTraceLineExtractor()

    @Test
    fun `extracts line when frame matches expected file`() {
        val stack = """
            java.lang.AssertionError: boom
                at com.adyen.demo.ExampleTest.testOne(ExampleTest.kt:42)
                at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
        """.trimIndent()

        assertEquals(42, extractor.extract(stack, "ExampleTest.kt"))
    }

    @Test
    fun `returns null when no matching frame`() {
        val stack = "at com.adyen.demo.OtherTest.testOne(OtherTest.kt:10)"
        assertNull(extractor.extract(stack, "ExampleTest.kt"))
    }

    @Test
    fun `returns null when no failure text`() {
        assertNull(extractor.extract("", "ExampleTest.kt"))
    }
}

