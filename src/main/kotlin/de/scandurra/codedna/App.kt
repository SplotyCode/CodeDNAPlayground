package de.scandurra.codedna

import de.scandurra.codedna.core.FingerprintService
import de.scandurra.codedna.core.ZipReader.FileZipReader
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import java.nio.file.Path

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val service = FingerprintService()
    val parser = ArgParser("code-dna")

    class FingerprintCmd : Subcommand("fingerprint", "Create a fingerprint for a ZIP file") {
        private val path by argument(ArgType.String, description = "Path")
        private val method by argument(ArgType.String, description = "Method")

        override fun execute() {
            FileZipReader(Path.of(path)).use { zr ->
                val fp = service.compute(method, zr)
                println(fp.fingerprint)
            }
        }
    }

    class CompareCmd : Subcommand("compare", "Compares two ZIP files") {
        private val left by argument(ArgType.String, description = "Left ZIP")
        private val right by argument(ArgType.String, description = "Right ZIP")
        private val method by argument(ArgType.String, description = "Method")

        override fun execute() {
            FileZipReader(Path.of(left)).use { zl ->
                FileZipReader(Path.of(right)).use { zr ->
                    val comparison = service.compare(method, zl, zr)
                    println("Result: ${comparison.result}")
                    println("Fingerprint of left zip:  ${comparison.left}")
                    println("Fingerprint of right zip: ${comparison.right}")
                }
            }
        }
    }

    parser.subcommands(FingerprintCmd(), CompareCmd())
    parser.parse(args)
}