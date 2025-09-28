package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.Fingerprinter
import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Fingerprinter.Fingerprint
import de.scandurra.codedna.core.Hashes
import de.scandurra.codedna.core.ZipReader
import de.scandurra.codedna.fingerprint.StructureMinHashFingerprinter.MinHashFingerprint

class StructureMinHashFingerprinter(
    private val hashFunctions: Int = 128,
    private val seeds: IntArray = IntArray(hashFunctions) { 0x9e379b9 * (it + 1) }
) : Fingerprinter<MinHashFingerprint> {

    override val name = "structure-minhash"

    data class MinHashFingerprint(val signature: IntArray) : Fingerprint
    data class Result(val jaccardEstimate: Double) : CompareResult

    override fun compute(zip: ZipReader): MinHashFingerprint {
        val features = zip.entries().map { e ->
            val role = when {
                e.name.endsWith(".class") -> "C"
                e.name.endsWith(".xml") || e.name.endsWith(".properties") -> "R"
                else -> "O"
            }
            val path = e.name
            val sizeBucket = (e.size / 1024)
            "$role|$path|$sizeBucket"
        }.toSet()
        val signature = IntArray(hashFunctions) { Int.MAX_VALUE }
        for (feature in features) {
            for (hashFunction in 0 until hashFunctions) {
                val hash = Hashes.sha256 {
                    it.update(seeds[hashFunction].toLong().toString(16).toByteArray())
                    it.update(feature.toByteArray())
                }.substring(0, 8).toLong(16).toInt()
                if (hash < signature[hashFunction]) {
                    signature[hashFunction] = hash
                }
            }
        }
        return MinHashFingerprint(signature)
    }

    override fun compare(left: MinHashFingerprint, right: MinHashFingerprint): Result {
        val nEqual = left.signature.indices.count { left.signature[it] == right.signature[it] }
        val jaccard = nEqual.toDouble() / left.signature.size
        return Result(jaccard)
    }
}