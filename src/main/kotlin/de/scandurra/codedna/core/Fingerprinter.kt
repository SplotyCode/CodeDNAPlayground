package de.scandurra.codedna.core

import de.scandurra.codedna.core.Fingerprinter.Fingerprint

interface Fingerprinter<F : Fingerprint> {
    val name: String

    fun compute(zip: ZipReader): F

    fun compare(left: F, right: F): CompareResult

    /* Ensure the fingerprint structure could be efficiently compared against millions of other fingerprints */
    interface Fingerprint

    interface CompareResult
}
