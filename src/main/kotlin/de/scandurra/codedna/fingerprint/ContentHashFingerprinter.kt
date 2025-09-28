package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.Fingerprinter
import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Fingerprinter.Fingerprint
import de.scandurra.codedna.core.Hashes
import de.scandurra.codedna.core.ZipReader
import de.scandurra.codedna.fingerprint.ContentHashFingerprinter.ContentHashFingerprint

class ContentHashFingerprinter : Fingerprinter<ContentHashFingerprint> {
    override val name: String = "content-hash"

    override fun compute(zip: ZipReader): ContentHashFingerprint {
        val files = zip.entries()
        val hash = Hashes.sha256 { digest ->
            files.forEach { digest.update(zip.readBytes(it.name)) }
        }
        return ContentHashFingerprint(hash)
    }

    override fun compare(
        left: ContentHashFingerprint,
        right: ContentHashFingerprint
    ): ContentHashResult {
        return ContentHashResult(left.hash == right.hash)
    }

    data class ContentHashFingerprint (val hash: String) : Fingerprint

    data class ContentHashResult(val equals: Boolean) : CompareResult
}