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

    class FakeZipReader(
        private val files: Map<String, ByteArray>,
    ) : ZipReader {
        override fun entries(): List<ZipEntryInfo> =
            files.map { (name, bytes) -> ZipEntryInfo(name, bytes.size.toLong()) }
                .sortedBy { it.name }

        override fun readBytes(name: String): ByteArray =
            files[name] ?: error("Entry not found: $name")

        override fun close() {}
    }
}
