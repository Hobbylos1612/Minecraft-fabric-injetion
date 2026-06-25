package jooon.util

import java.io.File
import jooon.features.dojo.AutoDojo
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object Utils {
   private val STRIP_COLOR_PATTERN: Regex = Regex("(?i)§[0-9A-FK-OR]")

   @JvmField
   var skyblock: Boolean
      private set

   val inSkyblock: Boolean
      public get() {
         var var10000: String = ScoreboardUtil.getSidebarTitle()
         if (var10000 == null) {
            var10000 = ""
         }

         if (var10000.contains("SKYBLOCK", true)) {
            return true
         } else {
            val `this$iv`: java.lang.Iterable = ScoreboardUtil.getSidebarLines()
            var var9: Boolean
            if (`this$iv` is java.util.Collection && (`this$iv` as java.util.Collection).isEmpty()) {
               var9 = false
            } else {
               val var5: java.util.Iterator = `this$iv`.iterator()

               while (true) {
                  if (!var5.hasNext()) {
                     var9 = false
break
                  }

                  if (contains(var5.next() as String, "SKYBLOCK", true)) {
                     var9 = true
break
                  }
               }
            }

            return var9
         }
      }


   val inDungeon: Boolean
      public get() {
         val `this$iv`: java.lang.Iterable = ScoreboardUtil.getSidebarLines()
         var var10000: Boolean
         if (`this$iv` is java.util.Collection && (`this$iv` as java.util.Collection).isEmpty()) {
            var10000 = false
         } else {
            val var4: java.util.Iterator = `this$iv`.iterator()

            while (true) {
               if (!var4.hasNext()) {
                  var10000 = false
break
               }

               if (contains(var4.next() as String, "The Catacombs", true)) {
                  var10000 = true
break
               }
            }
         }

         return var10000
      }


   val inDojo: Boolean
      public get() {
         return AutoDojo.isDojoActive
      }


   fun addMessage(message: String) {

      if (var10000 != null) {
         var10000.sendMessage(Text.literal(message) as Text, false)
      }
   }

   fun sendItemTags() {

      if (var10000 != null) {


         this.addMessage(var10001)
      }
   }

   fun PlayerEntity?.isOtherPlayer(): Boolean {

      var10000 != null && this != null && this != var10000 && this.getUuid().version() != 2
   }

   fun getGuiName(gui: Screen?): String {
      val var10000: String
      if (gui is HandledScreen) {
         var10000 = (gui as HandledScreen).getTitle().getString()
      } else if (gui is InventoryScreen) {
         var10000 = (gui as InventoryScreen).getTitle().getString()
      } else {
         var10000 = ""
      }
return var10000
   }

   fun screenTitle(): String? {

      val var10000: HandledScreen = var1 as? HandledScreen
      if ((var1 as? HandledScreen) != null) {

         if (var2 != null) {
            return var2.getString()
         }
      }

      return null
   }

   fun getSidebarTitle(): String? {

      label16@
      if (var10000 == null) {
         return null
      } else {

         return if (var3 == null) null else var3.getDisplayName().getString()
      }
   }

   fun fullInventory(): Boolean {

      if (var10000 != null) {

         if (var3 != null) {


            repeat(35) { i ->
               if (inv.getStack(i).isEmpty()) {
                  return false
               }
            }

            return true
         }
      }

      return false
   }

   fun findItemInHotbar(name: String): Int {

      if (var10000 != null) {

         if (var5 != null) {


            repeat(8) { slot ->

               if (!var6.isEmpty()) {

                  if (contains(stripColor(var7), name, true)) {
                     return slot
                  }
               }
            }

            return -1
         }
      }

      return -1
   }

   fun findItemInInventory(name: String): Int {

      if (var10000 != null) {

         if (var5 != null) {


            for (slot in 9..35) {

               if (!var6.isEmpty()) {

                  if (contains(stripColor(var7), name, true)) {
                     return slot
                  }
               }
            }

            return -1
         }
      }

      return -1
   }

   fun blockPosToYawPitch(pos: BlockPos, from: Vec3d): Pair<Float, Float> {
      this.vecToYawPitch(Vec3d(pos.getX().toDouble() + 0.5, pos.getY().toDouble() + 0.5, pos.getZ().toDouble() + 0.5), from)
   }

   fun vecToYawPitch(vec: Vec3d, from: Vec3d): Pair<Float, Float> {



      Pair(MathHelper.wrapDegrees((Math.toDegrees(Math.atan2(vec.z - from.z, dx)) - 90.0).toFloat()), MathHelper.wrapDegrees((-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))).toFloat()))
   }

   fun checkThreadAndQueue(run: () -> Unit) {
      this.runOnClientThread(run)
   }

   fun runOnClientThread(run: () -> Unit) {

      if (var10000.isOnThread()) {
         run()
      } else {
         var10000.execute({ 
            ``()
         })
      }
   }

   
   fun String.stripColor(): String {
      return STRIP_COLOR_PATTERN.replace(this, "")
   }

   
   fun BlockPos.toVec3(): Vec3d {
      Vec3d(this.getX() + 0.0, this.getY() + 0.0, this.getZ() + 0.0)
   }

   fun File.ensureFile(): Boolean {
      return (this.getParentFile().exists() || this.getParentFile().mkdirs())
         && (this.exists() || this.createNewFile())
      }

   object UseController {
      private var lastRightClickNs: Long

      fun canUse(): Boolean {
         return System.nanoTime() - lastRightClickNs > 100000000L
      }

      fun markUsed() {
         lastRightClickNs = System.nanoTime()
      }
   }
}
