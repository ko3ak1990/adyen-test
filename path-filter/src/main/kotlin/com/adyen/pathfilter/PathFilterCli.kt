package com.adyen.pathfilter

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val exitCode = PathFilterCli.run(args)
    if (exitCode != 0) {
        exitProcess(exitCode)
    }
}

object PathFilterCli {
    fun run(args: Array<String>, stderr: (String) -> Unit = { System.err.println(it) }): Int {
        val parsed = parseArgs(args)
            .onFailure { stderr(it.message ?: "Invalid arguments") }
            .getOrElse { return 1 }

        return runCatching {
            val config = YamlConfigParser().parse(parsed.configPath)
            val results = FilterEvaluator().evaluateAll(config, parsed.changedFiles)
            EnvWriter.write(parsed.outputPath, results)
        }.fold(
            onSuccess = { 0 },
            onFailure = {
                stderr(it.message ?: "Unexpected error")
                1
            }
        )
    }

    private fun parseArgs(args: Array<String>): Result<CliArgs> {
        var configPath: String? = null
        var outputPath: String? = null
        val changedFiles = mutableListOf<String>()

        var index = 0
        while (index < args.size) {
            when (val token = args[index]) {
                "--config" -> {
                    val value = args.getOrNull(index + 1)
                        ?: return Result.failure(CliParseException("Missing value for --config\n${usage()}"))
                    configPath = value
                    index += 2
                }

                "--output" -> {
                    val value = args.getOrNull(index + 1)
                        ?: return Result.failure(CliParseException("Missing value for --output\n${usage()}"))
                    outputPath = value
                    index += 2
                }

                else -> {
                    changedFiles += token
                    index++
                }
            }
        }

        if (configPath == null || outputPath == null) {
            return Result.failure(CliParseException("Both --config and --output are required\n${usage()}"))
        }

        return Result.success(CliArgs(configPath, outputPath, changedFiles))
    }

    private fun usage(): String =
        "Usage: path-filter --config <filters.yaml> --output <result.env> [changed-file ...]"
}

private class CliParseException(message: String) : IllegalArgumentException(message)
