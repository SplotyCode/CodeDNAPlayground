package de.scandurra.codedna.core

import java.security.MessageDigest
import java.util.HexFormat

object Hashes {
    fun sha256(update: (digest: MessageDigest) -> Unit): String {
        val md = MessageDigest.getInstance("SHA-256")
        update(md)
        return HexFormat.of().formatHex(md.digest())
    }
}