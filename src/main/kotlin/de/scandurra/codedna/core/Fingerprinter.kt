package de.scandurra.codedna.core

import de.scandurra.codedna.core.Fingerprinter.Fingerprint

interface Fingerprinter<F : Fingerprint> {
    val name: String

    fun compute(zip: ZipReader): F

    fun compare(left: F, right: F): CompareResult

    interface Fingerprint

    interface CompareResult
}