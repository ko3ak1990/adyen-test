package com.adyen.pathfilter

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterEvaluatorTest {
    private val evaluator = FilterEvaluator()

    @Test
    fun `inclusion match yields true`() {
        val result = evaluator.evaluate(FilterConfig(listOf("**/*.kt")), listOf("app/src/Main.kt"))
        assertTrue(result)
    }

    @Test
    fun `exclusion wins for same file`() {
        val result = evaluator.evaluate(
            FilterConfig(listOf("**/*.kt", "!**/test/**")),
            listOf("app/src/test/MainTest.kt")
        )
        assertFalse(result)
    }

    @Test
    fun `other file can still satisfy filter`() {
        val result = evaluator.evaluate(
            FilterConfig(listOf("**/*.kt", "!**/test/**")),
            listOf("app/src/test/MainTest.kt", "app/src/Main.kt")
        )
        assertTrue(result)
    }

    @Test
    fun `double star matches nested segments`() {
        val result = evaluator.evaluate(FilterConfig(listOf("**/*.kt")), listOf("a/b/c/Foo.kt"))
        assertTrue(result)
    }

    @Test
    fun `single star matches within one segment`() {
        val result = evaluator.evaluate(FilterConfig(listOf("web/*.ts")), listOf("web/app.ts"))
        assertTrue(result)
        val nonMatch = evaluator.evaluate(FilterConfig(listOf("web/*.ts")), listOf("web/src/app.ts"))
        assertFalse(nonMatch)
    }

    @Test
    fun `empty changed files yields false`() {
        val result = evaluator.evaluate(FilterConfig(listOf("**/*.kt")), emptyList())
        assertFalse(result)
    }
}

