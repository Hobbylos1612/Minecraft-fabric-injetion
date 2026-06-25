package jooon.util

import java.util.LinkedHashSet
import java.util.Objects
import jooon.features.dojo.Mastery
import net.minecraft.class_9331
import net.minecraft.component.Component
import net.minecraft.component.ComponentMap
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

public object BowDrawResetFixes {
   private final val driftComponentTypes: Set<class_9331<*>> =
      SetsKt.setOf(
         arrayOf(
            DataComponentTypes.field_49629,
            DataComponentTypes.field_49632,
            DataComponentTypes.field_49628,
            DataComponentTypes.field_49649,
            DataComponentTypes.field_49641,
            DataComponentTypes.field_50239,
            DataComponentTypes.field_49631,
            DataComponentTypes.field_56400,
            DataComponentTypes.field_50073
         )
      )

   @JvmStatic
   fun shouldPreserveBowDraw(from: ItemStack, to: ItemStack): Boolean {
      Mastery.INSTANCE.isBowDrawResetFixActive()
         && (from.method_31574(Items.field_8102) || to.method_31574(Items.field_8102))
         && INSTANCE.visibleHandStacksEquivalent(from, to)
      }

   fun visibleHandStacksEquivalent(old: ItemStack, new: ItemStack): Boolean {
      if (old.method_7960() && new.method_7960()) {
         true
      } else if (!ItemStack.method_7984(old, new)) {
         false
      } else if (ItemStack.method_31577(old, new)) {
         true
      } else {
         val var10001: ComponentMap = old.method_57353()
         val var10002: ComponentMap = new.method_57353()
         this.hasOnlyAllowedComponentDrift(var10001, var10002)
      }
   }

   fun isDriftComponent(type: ComponentType<*>): Boolean {
      driftComponentTypes.contains(type)
   }

   fun hasOnlyAllowedComponentDrift(first: ComponentMap, second: ComponentMap): Boolean {
      val types: LinkedHashSet = LinkedHashSet()
      var var10000: ComponentType = first.iterator()
      var sawAllowedDrift: java.util.Iterator = var10000

      while (sawAllowedDrift.hasNext()) {
         var10000 = (Component)sawAllowedDrift.next()
         types.add(var10000.comp_2443())
      }

      val var14: java.util.Iterator = second.iterator()
      sawAllowedDrift = var14

      while (sawAllowedDrift.hasNext()) {
         var10000 = (Component)sawAllowedDrift.next()
         types.add(var10000.comp_2443())
      }

      var var10: Boolean = false
      val var16: java.util.Iterator = types.iterator()
      val var12: java.util.Iterator = var16

      while (var12.hasNext()) {
         var10000 = (ComponentType)var12.next()
         val type: ComponentType = var10000
         val left: Any = first.method_58694(var10000)
         val right: Any = second.method_58694(var10000)
         if (!(left == right) && !Objects.equals(left, right)) {
            if (type == DataComponentTypes.field_49629 || type == DataComponentTypes.field_49632) {
               var10 = true
            } else if (type == DataComponentTypes.field_49628) {
               if (!this.customDataEquivalent(left as NbtComponent, right as NbtComponent)) {
                  false
               }

               var10 = true
            } else {
               if (!this.isDriftComponent(type)) {
                  false
               }

               var10 = true
            }
         }
      }

      var10
   }

   fun customDataEquivalent(left: NbtComponent, right: NbtComponent): Boolean {
      if (left == null && right == null) {
         true
      } else if (left != null && right != null) {
         val var10000: java.lang.String = left.method_57461().method_68564("uuid", "")
         val var5: java.lang.String = right.method_57461().method_68564("uuid", "")
         if (var10000.length() > 0 && var5.length() > 0) var10000 == var5 else left == right
      } else {
         false
      }
   }
}
