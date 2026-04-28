package com.adyen.junitxmlfilename

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

class SourceFileResolver(
    private val projectRoot: Path,
    private val sourceRoots: List<Path>
) {
    fun resolve(classname: String): String? {
        val className = classname.substringBefore('$')
        val relativeClassPath = className.replace('.', '/')

        for (root in sourceRoots) {
            for (ext in listOf("kt", "java")) {
                val candidate = root.resolve("$relativeClassPath.$ext")
                if (Files.exists(candidate)) {
                    return projectRoot.relativize(candidate).toString().replace('\\', '/')
                }
            }
        }
        return null
    }

    companion object {
        fun discoverSourceRoots(projectRoot: Path): List<Path> {
            val srcDir = projectRoot.resolve("src")
            if (!Files.exists(srcDir)) return emptyList()

            val roots = mutableListOf<Path>()
            Files.walk(srcDir).use { stream ->
                stream
                    .filter { Files.isDirectory(it) }
                    .filter { it.name == "kotlin" || it.name == "java" }
                    .forEach { roots.add(it) }
            }
            return roots
        }
    }
}
