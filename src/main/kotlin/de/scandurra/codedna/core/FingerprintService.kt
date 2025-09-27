package de.scandurra.codedna.core

import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Fingerprinter.Fingerprint

class FingerprintService() {
    private val methods: MutableMap<String, RegisteredMethod<*>> = mutableMapOf()

    fun <F : Fingerprint> register(method: Fingerprinter<F>) {
        methods[method.name] = RegisteredMethod(method)
    }

    fun names(): Set<String> = methods.keys

    private fun require(name: String) = methods[name]
        ?: throw IllegalArgumentException("Unknown fingerprinter $name (available: ${names()})")

    data class FingerprintResult<F : Fingerprint>(val strategy: Fingerprinter<F>, val fingerprint: F)

    fun compute(name: String, zip: ZipReader): FingerprintResult<*> =
        require(name).compute(zip)

    fun computeMultiple(names: List<String>, zip: ZipReader): List<FingerprintResult<*>> =
        names.map { compute(it, zip) }

    data class Comparison<F : Fingerprint>(
        val strategy: Fingerprinter<F>,
        val left: F,
        val right: F,
        val result: CompareResult
    )

    fun compare(name: String, left: ZipReader, right: ZipReader): Comparison<*> =
        require(name).compare(left, right)

    private class RegisteredMethod<F : Fingerprint>(val strategy: Fingerprinter<F>) {
        fun compute(zip: ZipReader): FingerprintResult<F> =
            FingerprintResult(strategy, strategy.compute(zip))

        fun compare(leftZip: ZipReader, rightZip: ZipReader): Comparison<F> {
            val left = strategy.compute(leftZip)
            val right = strategy.compute(rightZip)
            val result = strategy.compare(left, right)
            return Comparison(strategy, left, right, result)
        }
    }
}