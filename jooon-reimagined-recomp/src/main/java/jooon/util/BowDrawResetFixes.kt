package jooon.util

import java.util.LinkedHashSet
import java.util.Objects
import jooon.features.dojo.Mastery
import net.minecraft.component.ComponentType
import net.minecraft.component.Component
import net.minecraft.component.ComponentMap
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object BowDrawResetFixes {
   private val driftComponentTypes: Set<ComponentType<*>> =
      SetsKt.setOf(
         arrayOf(
            DataComponentTypes.DAMAGE,
            DataComponentTypes.LORE,
            DataComponentTypes.CUSTOM_DATA,
            DataComponentTypes.CHARGED_PROJECTILES,
            DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,
            DataComponentTypes.ITEM_NAME,
            DataComponentTypes.CUSTOM_NAME,
            DataComponentTypes.TOOLTIP_DISPLAY,
            DataComponentTypes.RARITY
         )
      )

   
   fun shouldPreserveBowDraw(from: ItemStack, to: ItemStack): Boolean {
      Mastery.isBowDrawResetFixActive()
         && (from.isOf(Items.BOW) || to.isOf(Items.BOW))
         && visibleHandStacksEquivalent(from, to)
      }

   fun visibleHandStacksEquivalent(old: ItemStack, new: ItemStack): Boolean {
      if (old.isEmpty() && new.isEmpty()) {
return true
      } else if (!ItemStack.areItemsEqual(old, new)) {
return false
      } else if (ItemStack.areItemsAndComponentsEqual(old, new)) {
return true
      } else {


         this.hasOnlyAllowedComponentDrift(var10001, var10002)
      }
   }

   fun isDriftComponent(type: ComponentType<*>): Boolean {
      driftComponentTypes.contains(type)
   }

   fun hasOnlyAllowedComponentDrift(first: ComponentMap, second: ComponentMap): Boolean {

      var var10000: ComponentType = first.iterator()
      var sawAllowedDrift: java.util.Iterator = var10000

      while (sawAllowedDrift.hasNext()) {
         var10000 = (Component)sawAllowedDrift.next()
         types.add(var10000.type())
      }

      val var14: java.util.Iterator = second.iterator()
      sawAllowedDrift = var14

      while (sawAllowedDrift.hasNext()) {
         var10000 = (Component)sawAllowedDrift.next()
         types.add(var10000.type())
      }

      var var10: Boolean = false
      val var16: java.util.Iterator = types.iterator()
      val var12: java.util.Iterator = var16

      while (var12.hasNext()) {
         var10000 = (ComponentType)var12.next()



         if (!(left == right) && !Objects.equals(left, right)) {
            if (type == DataComponentTypes.DAMAGE || type == DataComponentTypes.LORE) {
               var10 = true
            } else if (type == DataComponentTypes.CUSTOM_DATA) {
               if (!this.customDataEquivalent(left as NbtComponent, right as NbtComponent)) {
return false
               }

               var10 = true
            } else {
               if (!this.isDriftComponent(type)) {
return false
               }

               var10 = true
            }
         }
      }
return var10
   }

   fun customDataEquivalent(left: NbtComponent, right: NbtComponent): Boolean {
      if (left == null && right == null) {
return true
      } else if (left != null && right != null) {


         if (var10000.length() > 0 && var5.length() > 0) var10000 == var5 else left == right
      } else {
return false
      }
   }
}
