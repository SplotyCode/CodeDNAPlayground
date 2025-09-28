package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.ZipReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ContentHashFingerprinterTest {

    private val fingerprinter = ContentHashFingerprinter()

    @Test
    fun identical() {
        val zipA = ZipReader.FakeZipReader(
            mapOf(
                "a.txt" to "hello".toByteArray(),
                "b.txt" to "world".toByteArray(),
            )
        )
        val zipB = ZipReader.FakeZipReader(
            mapOf(
                "a.txt" to "hello".toByteArray(),
                "b.txt" to "world".toByteArray(),
            )
        )

        zipA.use { a ->
            zipB.use { b ->
                val fp1 = fingerprinter.compute(a)
                val fp2 = fingerprinter.compute(b)
                assertEquals(fp1.hash, fp2.hash, "Hashes should be equal for same files regardless of order")

                val cmp = fingerprinter.compare(fp1, fp2)
                assertTrue(cmp.equals, "Compare should indicate equality for same content")
            }
        }
    }

    @Test
    fun `not identical when content changes`() {
        val zipBase = ZipReader.FakeZipReader(
            mapOf(
                "a.txt" to "hello".toByteArray(),
                "b.txt" to "world".toByteArray(),
            )
        )
        val zipChanged = ZipReader.FakeZipReader(
            mapOf(
                "a.txt" to "hello".toByteArray(),
                // change content of b.txt
                "b.txt" to "WORLD".toByteArray(),
            )
        )

        zipBase.use { a ->
            zipChanged.use { b ->
                val fp1 = fingerprinter.compute(a)
                val fp2 = fingerprinter.compute(b)
                assertNotEquals(fp1.hash, fp2.hash, "Hashes should differ when any file content changes")

                val cmp = fingerprinter.compare(fp1, fp2)
                assertFalse(cmp.equals, "Compare should indicate inequality for different content")
            }
        }
    }
}
