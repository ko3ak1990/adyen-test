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
        val parsed = parseArgs(args, stderr) ?: return 1

        return try {
            val config = YamlConfigParser().parse(parsed.configPath)
            val results = FilterEvaluator().evaluateAll(config, parsed.changedFiles)
            EnvWriter.write(parsed.outputPath, results)
            0
        } catch (e: IllegalArgumentException) {
            stderr(e.message ?: "Unexpected error")
            1
        }
    }

    private fun parseArgs(args: Array<String>, stderr: (String) -> Unit): CliArgs? {
        var configPath: String? = null
        var outputPath: String? = null
        val changedFiles = mutableListOf<String>()

        var i = 0
        while (i < args.size) {
            when (val token = args[i]) {
                "--config" -> {
                    if (i + 1 >= args.size) {
                        stderr("Missing value for --config")
                        stderr(usage())
                        return null
                    }
                    configPath = args[i + 1]
                    i += 2
                }
                "--output" -> {
                    if (i + 1 >= args.size) {
                        stderr("Missing value for --output")
                        stderr(usage())
                        return null
                    }
                    outputPath = args[i + 1]
                    i += 2
                }
                else -> {
                    changedFiles += token
                    i++
                }
            }
        }

        if (configPath == null || outputPath == null) {
            stderr("Both --config and --output are required")
            stderr(usage())
            return null
        }

        return CliArgs(configPath, outputPath, changedFiles)
    }

    private fun usage(): String =
        "Usage: path-filter --config <filters.yaml> --output <result.env> [changed-file ...]"
}

