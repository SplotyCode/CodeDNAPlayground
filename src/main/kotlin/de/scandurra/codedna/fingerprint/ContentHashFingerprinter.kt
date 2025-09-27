package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.Fingerprinter
import de.scandurra.codedna.core.Fingerprinter.CompareResult
import de.scandurra.codedna.core.Fingerprinter.Fingerprint
import de.scandurra.codedna.core.ZipReader
import de.scandurra.codedna.fingerprint.ContentHashFingerprinter.ContentHashFingerprint
import java.security.MessageDigest
import java.util.HexFormat

class ContentHashFingerprinter : Fingerprinter<ContentHashFingerprint> {
    override val name: String = "content-hash"

    override fun compute(zip: ZipReader): ContentHashFingerprint {
        val files = zip.entries()
        val hash = sha256 { digest ->
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

    private fun sha256(update: (digest: MessageDigest) -> Unit): String {
        val md = MessageDigest.getInstance("SHA-256")
        update(md)
        return HexFormat.of().formatHex(md.digest())
    }
}