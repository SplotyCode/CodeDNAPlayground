package de.scandurra.codedna.core

import de.scandurra.codedna.core.Fingerprinter.Fingerprint
import java.security.MessageDigest
import java.util.HexFormat

object Hashes {
    fun sha256(update: (digest: MessageDigest) -> Unit): String {
        val md = MessageDigest.getInstance("SHA-256")
        update(md)
        return HexFormat.of().formatHex(md.digest())
    }

    class MinHash(
        hashFunctions: Int,
    ) {
        private val seeds: IntArray = IntArray(hashFunctions) { 0x9e379b9 * (it + 1) }

        fun hash(features: List<ByteArray>): MinHashFingerprint {
            val signature = IntArray(seeds.size) { Int.MAX_VALUE }
            for (feature in features) {
                for (hashFunction in 0 until seeds.size) {
                    val hash = sha256 {
                        it.update(seeds[hashFunction].toLong().toString(16).toByteArray())
                        it.update(feature)
                    }.substring(0, 8).toLong(16).toInt()
                    if (hash < signature[hashFunction]) {
                        signature[hashFunction] = hash
                    }
                }
            }
            return MinHashFingerprint(signature)
        }
    }

    data class MinHashFingerprint(val signature: IntArray) : Fingerprint

    fun compareMinHash(left: MinHashFingerprint, right: MinHashFingerprint): Double {
        require(left.signature.size == right.signature.size) { "Signature sizes must match" }
        val equal = left.signature.indices.count { left.signature[it] == right.signature[it] }
        return equal.toDouble() / left.signature.size
    }
}
