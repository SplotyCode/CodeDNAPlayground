package de.scandurra.codedna.fingerprint

import de.scandurra.codedna.core.Fingerprinter
import de.scandurra.codedna.core.Hashes
import de.scandurra.codedna.core.Hashes.MinHash
import de.scandurra.codedna.core.Hashes.MinHashFingerprint
import de.scandurra.codedna.core.ZipReader
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ApiMinHashFingerprinter(
    private val minHash: MinHash = MinHash(128),
) : Fingerprinter<MinHashFingerprint> {

    override val name = "api-minhash"

    data class Result(val jaccardEstimate: Double) : Fingerprinter.CompareResult

    private fun isRelevant(owner: String): Boolean {
        return !owner.startsWith("java/")
                && !owner.startsWith("kotlin/")
                && !owner.startsWith("kotlinx/")
    }

    override fun compute(zip: ZipReader): MinHashFingerprint {
        val apiFeatures = mutableSetOf<String>()

        zip.entries().filter { it.isClassFile() }.forEach { entry ->
            val bytes = zip.readBytes(entry.name)
            ClassReader(bytes).accept(object : ClassVisitor(Opcodes.ASM9) {
                override fun visit(
                    version: Int, access: Int, name: String?, signature: String?,
                    superName: String?, interfaces: Array<out String>?
                ) {
                    superName?.let { if (isRelevant(it)) apiFeatures += "S:$it" }
                    interfaces?.forEach { if (isRelevant(it)) apiFeatures += "I:$it" }
                }
                override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
                    return object : MethodVisitor(Opcodes.ASM9) {
                        override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
                            if (isRelevant(owner)) {
                                apiFeatures += "CALL:$owner.$name$descriptor"
                            }
                        }
                        override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
                            if (isRelevant(owner)) {
                                apiFeatures += "FACC:$owner.$name:$descriptor"
                            }
                        }
                    }
                }
            }, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        }
        return minHash.hash(apiFeatures.map { it.toByteArray() })
    }

    override fun compare(left: MinHashFingerprint, right: MinHashFingerprint): Result {
        return Result(Hashes.compareMinHash(left, right))
    }
}
