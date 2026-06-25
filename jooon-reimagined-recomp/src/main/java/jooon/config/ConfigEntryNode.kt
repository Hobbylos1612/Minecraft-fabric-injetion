package jooon.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.Arrays
import kotlin.math.MathKt

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
   val field: Field
   open val category: String?
   val label: String
   val tooltip: String?
   val kind: ConfigControlKind
   val min: Double?
   val max: Double?
   val enumValues: List<Enum<*>>
   open val fieldName: String

   init {
      this.field = field
      this.category = category
      this.label = label
      this.tooltip = tooltip
      this.kind = kind
      this.min = min
      this.max = max
      this.enumValues = enumValues

      this.fieldName = var10001
   }

   fun currentValue(): Any? {
      return this.field.get(null)
   }

   fun currentBoolean(): Boolean {

      return (var1 as? Boolean) != null && var1 as? Boolean
   }

   fun toggleBoolean() {
      this.field.set(null, !this.currentBoolean())
   }

   fun currentNumber(): Double {

      return if ((var1 as? java.lang.Number) != null) (var1 as? java.lang.Number).doubleValue() else 0.0
   }

   fun currentRatio(): Double {
      if (this.min != null) {

         if (this.max != null) {

            return if (max <= min) 0.0 else ((this.currentNumber() - min) / (max - min)).coerceIn(0.0, 1.0)
         } else {
            return 0.0
         }
      } else {
         return 0.0
      }
   }

   fun setFromRatio(ratio: Double) {
      if (this.min != null) {

         if (this.max != null) {


            this.field
               .set(
                  null,
                  if (var10 == Int::class.javaPrimitiveType || var10 == Integer::class.javaObjectType)
                     (raw).roundToInt()
return else
                     (
                        if (var10 == Long::class.javaPrimitiveType || var10 == Long::class.javaObjectType)
                           (raw).roundToLong()
return else
                           (
                              if (!(var10 == Float::class.javaPrimitiveType) && !(var10 == Float::class.javaObjectType))
                                 (raw * 100.0).roundToInt().toDouble() / 100.0
return else
                                 ((raw * 100.0).roundToInt().toDouble() / 100.0).toFloat()
                           )
                     )
               )
            }
      }
   }

   fun formatValue(): String {
      var var10000: Any = this.currentValue()
      if (var10000 == null) {
         return ""
      } else {
         if (var10000 is Float) {
            val var10: Array<Any> = arrayOf(var10000)
            var10000 = java.lang.String.format("%,.2f", Arrays.copyOf(var10, var10.length))
            var10000 = trimEnd(trimEnd((String)var10000, charArrayOf('0')), charArrayOf('.'))
         } else if (var10000 is Double) {
            val var12: Array<Any> = arrayOf(var10000)
            var10000 = java.lang.String.format("%,.2f", Arrays.copyOf(var12, var12.length))
            var10000 = trimEnd(trimEnd((String)var10000, charArrayOf('0')), charArrayOf('.'))
         } else {
            var10000 = if (var10000 is java.lang.Enum) (var10000 as java.lang.Enum).name() else var10000.toString()
         }

         return (String)var10000
      }
   }

   fun currentEnum(): Enum<*>? {

      return var1 as? java.lang.Enum
   }

   fun cycleEnum(step: Int = 1) {
      if (!this.enumValues.isEmpty()) {
         var var10000: java.lang.Enum = this.currentEnum()
         if (var10000 == null) {
            var10000 = first(this.enumValues) as java.lang.Enum
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

         `index$iv` = this.enumValues.size()
         this.field
            .set(
               null,
               this.enumValues
                  .get(var12 % `index$iv` + (`index$iv` and ((var12 % `index$iv` xor `index$iv`) and (var12 % `index$iv` or -(var12 % `index$iv`))) shr 31))
            )
         }
   }

   fun triggerAction() {
      val var4: java.util.Iterator = this.enumValues.iterator()

      var var10000: java.lang.Enum
      while (true) {
         if (var4.hasNext()) {
            val `element$iv`: Any = var4.next()
            if (!equals((`element$iv` as java.lang.Enum).name(), "CLICK", true)) {
return continue
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

   fun currentColor(): String {

      var var10000: String = var1 as? String
      if ((var1 as? String) == null) {
         var10000 = "#FFFFFF"
      }

      return var10000
   }

   fun currentText(): String {

      var var10000: String = var1 as? String
      if ((var1 as? String) == null) {
         var10000 = ""
      }

      return var10000
   }

   fun setText(value: String) {
      this.field.set(null, value)
   }

   fun setColor(hex: String) {
      this.field.set(null, hex)
   }

   fun toJson(): JsonElement {

      return if (value == null)
         JsonPrimitive("") as JsonElement
return else
         (
            if (value is Boolean)
               JsonPrimitive(value as Boolean) as JsonElement
return else
               (
                  if (value is java.lang.Number)
                     JsonPrimitive(value as java.lang.Number) as JsonElement
return else
                     (
                        if (value is String)
                           JsonPrimitive(value as String) as JsonElement
return else
                           (
                              if (value is java.lang.Enum)
                                 (
                                    if (this.kind === ConfigControlKind.ACTION)
                                       JsonPrimitive(equals((value as java.lang.Enum).name(), "CLICK", true))
return else
                                       JsonPrimitive((value as java.lang.Enum).name())
                                 ) as JsonElement
return else
                                 JsonPrimitive(value.toString()) as JsonElement
                           )
                     )
               )
         )
      }

   fun applyJson(element: JsonElement) {
      if (!element.isJsonNull()) {


         try {
            var var19: ConfigEntryNode = var2

            if (var5 == Boolean::class.javaPrimitiveType || var5 == Boolean::class.javaObjectType) {
               var19.field.set(null, element.getAsBoolean())
            } else if (var5 == Int::class.javaPrimitiveType || var5 == Integer::class.javaObjectType) {
               var19.field.set(null, element.getAsInt())
            } else if (var5 == Long::class.javaPrimitiveType || var5 == Long::class.javaObjectType) {
               var19.field.set(null, element.getAsLong())
            } else if (var5 == Float::class.javaPrimitiveType || var5 == Float::class.javaObjectType) {
               var19.field.set(null, element.getAsFloat())
            } else if (var5 == Double::class.javaPrimitiveType || var5 == Double::class.javaObjectType) {
               var19.field.set(null, element.getAsDouble())
            } else if (var5 == String::class.java) {
               var19.field.set(null, element.getAsString())
            } else if (var19.field.getType().isEnum()) {
               var var10000: Array<Any> = var19.field.getType().getEnumConstants()
               val ``: java.util.Collection = ArrayList(var10000.length)

               for (var13 in var10000) {
                  ``.add(var13 as java.lang.Enum)
               }

               val constants: java.util.List = `` as java.util.List
               if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {

                  val var29: java.util.Iterator = constants.iterator()

                  while (true) {
                     if (!var29.hasNext()) {
                        var10000 = null
break
                     }

                     if (equals((var31 as java.lang.Enum).name(), var23, true)) {
                        var10000 = (Object[])var31
break
                     }
                  }

                  var10000 = var10000 as java.lang.Enum
               } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                  var10000 = getOrNull(constants, element.getAsInt()) as java.lang.Enum
               } else {
                  val var26: java.util.Iterator = constants.iterator()

                  while (true) {
                     if (!var26.hasNext()) {
                        var10000 = null
break
                     }

                     if (equals((var28 as java.lang.Enum).name(), element.getAsString(), true)) {
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

            var19 = Result(Unit)
         } catch (var18: java.lang.Throwable) {
            val `this24lambda_u245`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18))
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

   fun copy(
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

   override fun toString(): String {
      return "ConfigEntryNode(field=${this.field}, category=${this.category}, label=${this.label}, tooltip=${this.tooltip}, kind=${this.kind}, min=${this.min}, max=${this.max}, enumValues=${this.enumValues})"
   }

   override fun hashCode(): Int {
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

   override operator fun equals(other: Any?): Boolean {
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
