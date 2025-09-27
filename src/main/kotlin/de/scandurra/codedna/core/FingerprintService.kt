package de.scandurra.codedna.core

import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Fingerprinter.Fingerprint

class FingerprintService() {
    private val methods: MutableMap<String, Fingerprinter<Fingerprint>> = mutableMapOf()

    fun register(method: Fingerprinter<Fingerprint>) {
        methods[method.name] = method
    }

    fun names(): Set<String> = methods.keys

    fun require(name: String) = methods[name]
        ?: throw IllegalArgumentException("Unknown fingerprinter $name (available: ${names()})")

    fun compute(name: String, zip: ZipReader): FingerprintResult<Fingerprint> {
        val method = require(name)
        return FingerprintResult(method, method.compute(zip))
    }

    fun computeMultiple(names: List<String>, zip: ZipReader): List<FingerprintResult<Fingerprint>> =
        names.map { compute(it, zip) }

    data class FingerprintResult<F : Fingerprint>(val strategy: Fingerprinter<F>, val fingerprint: F)

    fun compare(name: String, left: ZipReader, right: ZipReader): Comparison<Fingerprint> {
        val method = require(name)
        val leftFingerprint = method.compute(left)
        val rightFingerprint = method.compute(right)
        val result = method.compare(leftFingerprint, rightFingerprint)
        return Comparison(method, leftFingerprint, rightFingerprint, result)
    }

    data class Comparison<F : Fingerprint>(
        val strategy: Fingerprinter<F>,
        val left: F,
        val right: F,
        val result: CompareResult
    )
}