package de.scandurra.codedna.core

import java.io.Closeable
import java.nio.file.Path
import java.util.zip.ZipFile

interface ZipReader : Closeable {
    fun entries(): List<ZipEntryInfo>
    fun readBytes(name: String): ByteArray

    data class ZipEntryInfo(val name: String, val size: Long)

    class FileZipReader(path: Path) : ZipReader {
        private val zip = ZipFile(path.toFile())

        override fun entries(): List<ZipEntryInfo> =
            zip.entries().asSequence()
                .filter { !it.isDirectory }
                .map { ZipEntryInfo(it.name, it.size) }
                .toList()

        override fun readBytes(name: String): ByteArray {
            val entry = zip.getEntry(name) ?: error("Entry not found: $name")
            zip.getInputStream(entry).use { return it.readAllBytes() }
        }

        override fun close() = zip.close()
    }
}
