@file:SourceDebugExtension(["SMAP\nJooonConfigManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonConfigManager.kt\njooon/config/JooonConfigManagerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,480:1\n766#2:481\n857#2,2:482\n*S KotlinDebug\n*F\n+ 1 JooonConfigManager.kt\njooon/config/JooonConfigManagerKt\n*L\n473#1:481\n473#1:482,2\n*E\n"])

package jooon.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedWriter
import java.io.Closeable
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.ArrayList
import java.util.Locale
import kotlin.jvm.internal.SourceDebugExtension

private fun readJsonObject(path: Path, gson: Gson): JsonObject? {
   var var2: Any
   try {
      val var3: Closeable = Files.newBufferedReader(path, StandardCharsets.UTF_8)
      var var4: java.lang.Throwable = null

      var var15: JsonObject
      try {
         var15 = JsonParser.parseReader(var3 as Reader).getAsJsonObject()
      } catch (var10: java.lang.Throwable) {
         var4 = var10
         throw var10
      } finally {
         CloseableKt.closeFinally(var3, var4)
      }

      var2 = Result.constructor_impl/* $VF was: constructor-impl */(var15)
   } catch (var12: java.lang.Throwable) {
      var2 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var12))
   }

   return (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var2)) null else var2) as JsonObject
}

private fun writeJsonAtomically(path: Path, json: String) {
   Files.createDirectories(path.getParent())
   val tempPath: Path = path.resolveSibling("${path.getFileName()}.tmp")
   val var3: Closeable = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)
   var var4: java.lang.Throwable = null

   try {
      (var3 as BufferedWriter).write(json)
   } catch (var11: java.lang.Throwable) {
      var4 = var11
      throw var11
   } finally {
      CloseableKt.closeFinally(var3, var4)
   }

   try {
      Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
   } catch (var10: Exception) {
      Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING)
   }
}

private fun prettifyName(raw: String): String {
   val var15: java.lang.Iterable = StringsKt.split$default(
      Regex("([a-z0-9])([A-Z])").replace(StringsKt.replace$default(raw, '_', ' ', false, 4, null), "$1 $2"), charArrayOf(' '), false, 0, 6, null
   )
   val `destination$iv$iv`: java.util.Collection = ArrayList()

   for (`element$iv$iv` in var15) {
      if (!StringsKt.isBlank(`element$iv$iv` as java.lang.String)) {
         `destination$iv$iv`.add(`element$iv$iv`)
      }
   }

   return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, " ", null, null, 0, null, { word: java.lang.String ->
      var var10000: java.lang.String = word.toLowerCase(Locale.ROOT)
      if (var10000.length() > 0) {
         val var7: StringBuilder = StringBuilder()
         val first: Char = var10000.charAt(0)
         val var8: StringBuilder = var7.append((Object)(if (Character.isLowerCase(first)) CharsKt.titlecase(first) else java.lang.String.valueOf(first)))
         val var10001: java.lang.String = var10000.substring(1)
         var10000 = var8.append(var10001).toString()
      } else {
         var10000 = var10000
      }

      var10000 as java.lang.CharSequence
   }, 30, null)
}
