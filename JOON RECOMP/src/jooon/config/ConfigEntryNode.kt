package jooon.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

@SourceDebugExtension(["SMAP\nJooonConfigManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JooonConfigManager.kt\njooon/config/ConfigEntryNode\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,480:1\n350#2,7:481\n288#2,2:488\n288#2,2:494\n288#2,2:496\n11065#3:490\n11400#3,3:491\n*S KotlinDebug\n*F\n+ 1 JooonConfigManager.kt\njooon/config/ConfigEntryNode\n*L\n353#1:481,7\n359#1:488,2\n409#1:494,2\n415#1:496,2\n405#1:490\n405#1:491,3\n*E\n"])
internal data class ConfigEntryNode(field: Field,
      category: String?,
      label: String,
      tooltip: String?,
      kind: ConfigControlKind,
      min: Double?,
      max: Double?,
      enumValues: List<Enum<*>>
   ) :
   ConfigNode {
   public final val field: Field
   public open val category: String?
   public final val label: String
   public final val tooltip: String?
   public final val kind: ConfigControlKind
   public final val min: Double?
   public final val max: Double?
   public final val enumValues: List<Enum<*>>
   public open val fieldName: String

   init {
      this.field = field
      this.category = category
      this.label = label
      this.tooltip = tooltip
      this.kind = kind
      this.min = min
      this.max = max
      this.enumValues = enumValues
      val var10001: java.lang.String = this.field.getName()
      this.fieldName = var10001
   }

   public fun currentValue(): Any? {
      return this.field.get(null)
   }

   public fun currentBoolean(): Boolean {
      val var1: Any = this.currentValue()
      return (var1 as? java.lang.Boolean) != null && var1 as? java.lang.Boolean
   }

   public fun toggleBoolean() {
      this.field.set(null, !this.currentBoolean())
   }

   public fun currentNumber(): Double {
      val var1: Any = this.currentValue()
      return if ((var1 as? java.lang.Number) != null) (var1 as? java.lang.Number).doubleValue() else 0.0
   }

   public fun currentRatio(): Double {
      if (this.min != null) {
         val min: Double = this.min
         if (this.max != null) {
            val max: Double = this.max
            return if (max <= min) 0.0 else RangesKt.coerceIn((this.currentNumber() - min) / (max - min), 0.0, 1.0)
         } else {
            return 0.0
         }
      } else {
         return 0.0
      }
   }

   public fun setFromRatio(ratio: Double) {
      if (this.min != null) {
         val min: Double = this.min
         if (this.max != null) {
            val raw: Double = min + (this.max - min) * RangesKt.coerceIn(ratio, 0.0, 1.0)
            val var10: Class = this.field.getType()
            this.field
               .set(
                  null,
                  if (var10 == Int::class.javaPrimitiveType || var10 == Integer::class.javaObjectType)
                     MathKt.roundToInt(raw)
                     else
                     (
                        if (var10 == java.lang.Long::class.javaPrimitiveType || var10 == java.lang.Long::class.javaObjectType)
                           MathKt.roundToLong(raw)
                           else
                           (
                              if (!(var10 == java.lang.Float::class.javaPrimitiveType) && !(var10 == java.lang.Float::class.javaObjectType))
                                 (double)MathKt.roundToInt(raw * 100.0) / 100.0
                                 else
                                 (float)((double)MathKt.roundToInt(raw * 100.0) / 100.0)
                           )
                     )
               )
            }
      }
   }

   public fun formatValue(): String {
      var var10000: Any = this.currentValue()
      if (var10000 == null) {
         return ""
      } else {
         if (var10000 is java.lang.Float) {
            val var10: Array<Any> = arrayOf(var10000)
            var10000 = java.lang.String.format("%,.2f", Arrays.copyOf(var10, var10.length))
            var10000 = StringsKt.trimEnd(StringsKt.trimEnd((java.lang.String)var10000, charArrayOf('0')), charArrayOf('.'))
         } else if (var10000 is java.lang.Double) {
            val var12: Array<Any> = arrayOf(var10000)
            var10000 = java.lang.String.format("%,.2f", Arrays.copyOf(var12, var12.length))
            var10000 = StringsKt.trimEnd(StringsKt.trimEnd((java.lang.String)var10000, charArrayOf('0')), charArrayOf('.'))
         } else {
            var10000 = if (var10000 is java.lang.Enum) (var10000 as java.lang.Enum).name() else var10000.toString()
         }

         return (java.lang.String)var10000
      }
   }

   public fun currentEnum(): Enum<*>? {
      val var1: Any = this.currentValue()
      return var1 as? java.lang.Enum
   }

   public fun cycleEnum(step: Int = 1) {
      if (!this.enumValues.isEmpty()) {
         var var10000: java.lang.Enum = this.currentEnum()
         if (var10000 == null) {
            var10000 = CollectionsKt.first(this.enumValues) as java.lang.Enum
         }

         val current: java.lang.Enum = var10000
         var `index$iv`: Int = 0
         val var7: java.util.Iterator = this.enumValues.iterator()

         while (true) {
            if (!var7.hasNext()) {
               var15 = -1
               break
            }

            if ((var7.next() as java.lang.Enum).name() == current.name()) {
               var15 = `index$iv`
               break
            }

            `index$iv`++
         }

         val var12: Int = RangesKt.coerceAtLeast(var15, 0) + step
         `index$iv` = this.enumValues.size()
         this.field
            .set(
               null,
               this.enumValues
                  .get(var12 % `index$iv` + (`index$iv` and ((var12 % `index$iv` xor `index$iv`) and (var12 % `index$iv` or -(var12 % `index$iv`))) shr 31))
            )
         }
   }

   public fun triggerAction() {
      val var4: java.util.Iterator = this.enumValues.iterator()

      var var10000: java.lang.Enum
      while (true) {
         if (var4.hasNext()) {
            val `element$iv`: Any = var4.next()
            if (!StringsKt.equals((`element$iv` as java.lang.Enum).name(), "CLICK", true)) {
               continue
            }

            var10000 = (java.lang.Enum)`element$iv`
            break
         }

         var10000 = null
         break
      }

      var10000 = var10000
      if (var10000 != null) {
         this.field.set(null, var10000)
      }
   }

   public fun currentColor(): String {
      val var1: Any = this.currentValue()
      var var10000: java.lang.String = var1 as? java.lang.String
      if ((var1 as? java.lang.String) == null) {
         var10000 = "#FFFFFF"
      }

      return var10000
   }

   public fun currentText(): String {
      val var1: Any = this.currentValue()
      var var10000: java.lang.String = var1 as? java.lang.String
      if ((var1 as? java.lang.String) == null) {
         var10000 = ""
      }

      return var10000
   }

   public fun setText(value: String) {
      this.field.set(null, value)
   }

   public fun setColor(hex: String) {
      this.field.set(null, hex)
   }

   public fun toJson(): JsonElement {
      val value: Any = this.currentValue()
      return if (value == null)
         JsonPrimitive("") as JsonElement
         else
         (
            if (value is java.lang.Boolean)
               JsonPrimitive(value as java.lang.Boolean) as JsonElement
               else
               (
                  if (value is java.lang.Number)
                     JsonPrimitive(value as java.lang.Number) as JsonElement
                     else
                     (
                        if (value is java.lang.String)
                           JsonPrimitive(value as java.lang.String) as JsonElement
                           else
                           (
                              if (value is java.lang.Enum)
                                 (
                                    if (this.kind === ConfigControlKind.ACTION)
                                       JsonPrimitive(StringsKt.equals((value as java.lang.Enum).name(), "CLICK", true))
                                       else
                                       JsonPrimitive((value as java.lang.Enum).name())
                                 ) as JsonElement
                                 else
                                 JsonPrimitive(value.toString()) as JsonElement
                           )
                     )
               )
         )
      }

   public fun applyJson(element: JsonElement) {
      if (!element.isJsonNull()) {
         val var2: ConfigEntryNode = this

         try {
            var var19: ConfigEntryNode = var2
            val var5: Class = var2.field.getType()
            if (var5 == java.lang.Boolean::class.javaPrimitiveType || var5 == java.lang.Boolean::class.javaObjectType) {
               var19.field.set(null, element.getAsBoolean())
            } else if (var5 == Int::class.javaPrimitiveType || var5 == Integer::class.javaObjectType) {
               var19.field.set(null, element.getAsInt())
            } else if (var5 == java.lang.Long::class.javaPrimitiveType || var5 == java.lang.Long::class.javaObjectType) {
               var19.field.set(null, element.getAsLong())
            } else if (var5 == java.lang.Float::class.javaPrimitiveType || var5 == java.lang.Float::class.javaObjectType) {
               var19.field.set(null, element.getAsFloat())
            } else if (var5 == java.lang.Double::class.javaPrimitiveType || var5 == java.lang.Double::class.javaObjectType) {
               var19.field.set(null, element.getAsDouble())
            } else if (var5 == java.lang.String::class.java) {
               var19.field.set(null, element.getAsString())
            } else if (var19.field.getType().isEnum()) {
               var var10000: Array<Any> = var19.field.getType().getEnumConstants()
               val `$i$f$firstOrNull`: java.util.Collection = ArrayList(var10000.length)

               for (var13 in var10000) {
                  `$i$f$firstOrNull`.add(var13 as java.lang.Enum)
               }

               val constants: java.util.List = `$i$f$firstOrNull` as java.util.List
               if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                  val var23: java.lang.String = if (element.getAsBoolean()) "CLICK" else "IDLE"
                  val var29: java.util.Iterator = constants.iterator()

                  while (true) {
                     if (!var29.hasNext()) {
                        var10000 = null
                        break
                     }

                     val var31: Any = var29.next()
                     if (StringsKt.equals((var31 as java.lang.Enum).name(), var23, true)) {
                        var10000 = (Object[])var31
                        break
                     }
                  }

                  var10000 = var10000 as java.lang.Enum
               } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                  var10000 = CollectionsKt.getOrNull(constants, element.getAsInt()) as java.lang.Enum
               } else {
                  val var26: java.util.Iterator = constants.iterator()

                  while (true) {
                     if (!var26.hasNext()) {
                        var10000 = null
                        break
                     }

                     val var28: Any = var26.next()
                     if (StringsKt.equals((var28 as java.lang.Enum).name(), element.getAsString(), true)) {
                        var10000 = (Object[])var28
                        break
                     }
                  }

                  var10000 = var10000 as java.lang.Enum
               }

               if (var10000 != null) {
                  var19.field.set(null, var10000)
               }
            }

            var19 = (ConfigEntryNode)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var18: java.lang.Throwable) {
            val `$this$applyJson_u24lambda_u245`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18))
         }
      }
   }

   public operator fun component1(): Field {
      return this.field
   }

   public operator fun component2(): String? {
      return this.category
   }

   public operator fun component3(): String {
      return this.label
   }

   public operator fun component4(): String? {
      return this.tooltip
   }

   public operator fun component5(): ConfigControlKind {
      return this.kind
   }

   public operator fun component6(): Double? {
      return this.min
   }

   public operator fun component7(): Double? {
      return this.max
   }

   public operator fun component8(): List<Enum<*>> {
      return this.enumValues
   }

   public fun copy(
      field: Field = this.field,
      category: String? = this.category,
      label: String = this.label,
      tooltip: String? = this.tooltip,
      kind: ConfigControlKind = this.kind,
      min: Double? = this.min,
      max: Double? = this.max,
      enumValues: List<Enum<*>> = this.enumValues
   ): ConfigEntryNode {
      return ConfigEntryNode(field, category, label, tooltip, kind, min, max, enumValues)
   }

   public override fun toString(): String {
      return "ConfigEntryNode(field=${this.field}, category=${this.category}, label=${this.label}, tooltip=${this.tooltip}, kind=${this.kind}, min=${this.min}, max=${this.max}, enumValues=${this.enumValues})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (this.field.hashCode() * 31 + (if (this.category == null) 0 else this.category.hashCode())) * 31
                                                      + this.label.hashCode()
                                                )
                                                * 31
                                             + (if (this.tooltip == null) 0 else this.tooltip.hashCode())
                                       )
                                       * 31
                                    + this.kind.hashCode()
                              )
                              * 31
                           + (if (this.min == null) 0 else this.min.hashCode())
                     )
                     * 31
                  + (if (this.max == null) 0 else this.max.hashCode())
            )
            * 31
         + this.enumValues.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label64@
      if (this === other) {
         return true
      } else {
         return other is ConfigEntryNode
            && this.field == (other as ConfigEntryNode).field
            && this.category == (other as ConfigEntryNode).category
            && this.label == (other as ConfigEntryNode).label
            && this.tooltip == (other as ConfigEntryNode).tooltip
            && this.kind === (other as ConfigEntryNode).kind
            && this.min == (other as ConfigEntryNode).min
            && this.max == (other as ConfigEntryNode).max
            && this.enumValues == (other as ConfigEntryNode).enumValues
         }
   }
}
