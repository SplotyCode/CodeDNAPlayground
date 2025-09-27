package de.scandurra.codedna.core

interface Fingerprinter {
    val name: String

    fun compute(zip: ZipReader): Fingerprint

    interface Fingerprint
}