package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.ZipReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StructureMinHashFingerprinterTest {

    private val fingerprinter = StructureMinHashFingerprinter()

    @Test
    fun `identical regardless of map order`() {
        val zipA = ZipReader.FakeZipReader(
            mapOf(
                "b.txt" to "world".toByteArray(),
                "a.txt" to "hello".toByteArray(),
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

                // Same features should yield exactly the same signature
                assertEquals(128, fp1.signature.size, "Default signature size should be 128")
                assertEquals(fp1.signature.toList(), fp2.signature.toList(), "Signatures should be equal for same files regardless of order")

                val cmp = fingerprinter.compare(fp1, fp2)
                assertEquals(1.0, cmp.jaccardEstimate, "Jaccard estimate should be 1.0 for identical structures")
            }
        }

        // Also verify the name for completeness
        assertEquals("structure-minhash", fingerprinter.name)
    }

    @Test
    fun `lower similarity when structure changes`() {
        val base = ZipReader.FakeZipReader(
            mapOf(
                "a.txt" to "hello".toByteArray(),
                "b.txt" to "world".toByteArray(),
            )
        )
        // Rename b.txt -> c.txt so the structural feature set differs
        val changed = ZipReader.FakeZipReader(
            mapOf(
                "a.txt" to "hello".toByteArray(),
                "c.txt" to "world".toByteArray(),
            )
        )

        base.use { a ->
            changed.use { b ->
                val fp1 = fingerprinter.compute(a)
                val fp2 = fingerprinter.compute(b)
                val cmp = fingerprinter.compare(fp1, fp2)

                // With one common feature out of three total, the true Jaccard would be ~1/3.
                // MinHash gives an estimate in [0,1]; ensure it's strictly less than 1 and greater than 0.
                assertTrue(cmp.jaccardEstimate < 1.0, "Jaccard estimate should be less than 1.0 when structures differ")
                assertTrue(cmp.jaccardEstimate > 0.0, "Jaccard estimate should be greater than 0.0 when there is partial overlap")
            }
        }
    }
}
