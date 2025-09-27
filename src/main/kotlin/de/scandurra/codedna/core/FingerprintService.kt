package de.scandurra.codedna.core

import de.scandurra.codedna.core.Fingerprinter.Fingerprint

class FingerprintService() {
    private val methods: MutableMap<String, Fingerprinter> = mutableMapOf()

    fun names(): Set<String> = methods.keys

    fun compute(name: String, zip: ZipReader): FingerprintResult {
        val method = methods[name]
            ?: throw IllegalArgumentException("Unknown fingerprinter $name (available: ${names()})")
        return FingerprintResult(method, method.compute(zip))
    }

    fun computeMultiple(names: List<String>, zip: ZipReader): List<FingerprintResult> =
        names.map { compute(it, zip) }

    data class FingerprintResult(val strategy: Fingerprinter, val fingerprint: Fingerprint)

    fun register(method: Fingerprinter) {
        methods[method.name] = method
    }
}