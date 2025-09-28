package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.Fingerprinter
import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Fingerprinter.Fingerprint
import de.scandurra.codedna.core.Hashes
import de.scandurra.codedna.core.ZipReader
import de.scandurra.codedna.fingerprint.StructureMinHashFingerprinter.MinHashFingerprint

class StructureMinHashFingerprinter(
    private val minHash: Hashes.MinHash = Hashes.MinHash(128),
) : Fingerprinter<MinHashFingerprint> {

    override val name = "structure-minhash"

    data class MinHashFingerprint(val signature: IntArray) : Fingerprint
    data class Result(val jaccardEstimate: Double) : CompareResult

    override fun compute(zip: ZipReader): MinHashFingerprint {
        val features = zip.entries()
            .map { e -> "${e.name}|${(e.size / 1024)}" }
            .toSet()
        val signature = minHash.hash(features.map { it.toByteArray() })
        return MinHashFingerprint(signature)
    }

    override fun compare(left: MinHashFingerprint, right: MinHashFingerprint): Result {
        val nEqual = left.signature.indices.count { left.signature[it] == right.signature[it] }
        val jaccard = nEqual.toDouble() / left.signature.size
        return Result(jaccard)
    }
}