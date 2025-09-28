package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.Fingerprinter
import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Hashes
import de.scandurra.codedna.core.Hashes.MinHash
import de.scandurra.codedna.core.Hashes.MinHashFingerprint
import de.scandurra.codedna.core.ZipReader

class StructureMinHashFingerprinter(
    private val minHash: MinHash = MinHash(128),
) : Fingerprinter<MinHashFingerprint> {

    override val name = "structure-minhash"

    data class Result(val jaccardEstimate: Double) : CompareResult

    override fun compute(zip: ZipReader): MinHashFingerprint {
        val features = zip.entries()
            .map { e -> "${e.name}|${(e.size / 1024)}" }
            .toSet()
        return minHash.hash(features.map { it.toByteArray() })
    }

    override fun compare(left: MinHashFingerprint, right: MinHashFingerprint): Result {
        return Result(Hashes.compareMinHash(left, right))
    }
}