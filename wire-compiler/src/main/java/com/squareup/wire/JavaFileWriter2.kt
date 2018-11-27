package com.squareup.wire

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import com.squareup.wire.java.JavaGenerator2
import com.squareup.wire.java.Utils
import com.squareup.wire.schema.ProtoFile
import com.squareup.wire.schema.Type
import java.io.IOException
import java.nio.file.FileSystem
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentLinkedQueue
import javax.lang.model.element.Modifier

internal class JavaFileWriter2(
        private val destination: String,
        private val protoFile: ProtoFile,
        private val javaGenerator: JavaGenerator2,
        private val queue: ConcurrentLinkedQueue<Type>,
        private val dryRun: Boolean,
        private val fs: FileSystem,
        private val log: WireLogger
) : Callable<Unit> {

  @Throws(IOException::class)
  override fun call() {
    var protoFileTypeBuilder = TypeSpec.classBuilder(Utils.getProtoFileClassName(protoFile))
    protoFileTypeBuilder.addModifiers(Modifier.PUBLIC)

    while (!queue.isEmpty()) {
      val type = queue.poll() ?: break

      val typeSpec = javaGenerator.generateType(type)
      protoFileTypeBuilder.addType(typeSpec.toBuilder()
              .addModifiers(Modifier.STATIC).build())
    }

    val javaFile = JavaFile.builder(protoFile.javaPackage(), protoFileTypeBuilder.build())
            .addFileComment("\$L", WireCompiler.CODE_GENERATED_BY_WIRE)
            /*.apply {
              val location = type.location()
              if (location != null) {
                addFileComment("\nSource file: \$L", location.withPathOnly())
              }
            }*/.build()

    val path = fs.getPath(destination)
    log.artifact(path, javaFile)
    if (dryRun) return

    try {
      javaFile.writeTo(path)
    } catch (e: IOException) {
      throw IOException(
              "Error emitting ${javaFile.packageName}.${javaFile.typeSpec.name} to $destination", e)
    }
  }
}