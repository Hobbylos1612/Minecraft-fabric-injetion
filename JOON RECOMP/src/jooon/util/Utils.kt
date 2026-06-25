package jooon.util

import java.io.File
import jooon.features.dojo.AutoDojo
import kotlin.jvm.internal.SourceDebugExtension
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

@SourceDebugExtension(["SMAP\nUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Utils.kt\njooon/util/Utils\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,208:1\n1747#2,3:209\n1747#2,3:212\n*S KotlinDebug\n*F\n+ 1 Utils.kt\njooon/util/Utils\n*L\n50#1:209,3\n56#1:212,3\n*E\n"])
public object Utils {
   private final val STRIP_COLOR_PATTERN: Regex = Regex("(?i)§[0-9A-FK-OR]")

   @JvmField
   public final var skyblock: Boolean
      private set

   public final val inSkyblock: Boolean
      public final get() {
         var var10000: java.lang.String = ScoreboardUtil.INSTANCE.getSidebarTitle()
         if (var10000 == null) {
            var10000 = ""
         }

         if (StringsKt.contains(var10000, "SKYBLOCK", true)) {
            return true
         } else {
            val `$this$any$iv`: java.lang.Iterable = ScoreboardUtil.INSTANCE.getSidebarLines()
            var var9: Boolean
            if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
               var9 = false
            } else {
               val var5: java.util.Iterator = `$this$any$iv`.iterator()

               while (true) {
                  if (!var5.hasNext()) {
                     var9 = false
                     break
                  }

                  if (StringsKt.contains(var5.next() as java.lang.String, "SKYBLOCK", true)) {
                     var9 = true
                     break
                  }
               }
            }

            return var9
         }
      }


   public final val inDungeon: Boolean
      public final get() {
         val `$this$any$iv`: java.lang.Iterable = ScoreboardUtil.INSTANCE.getSidebarLines()
         var var10000: Boolean
         if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
            var10000 = false
         } else {
            val var4: java.util.Iterator = `$this$any$iv`.iterator()

            while (true) {
               if (!var4.hasNext()) {
                  var10000 = false
                  break
               }

               if (StringsKt.contains(var4.next() as java.lang.String, "The Catacombs", true)) {
                  var10000 = true
                  break
               }
            }
         }

         return var10000
      }


   public final val inDojo: Boolean
      public final get() {
         return AutoDojo.INSTANCE.isDojoActive
      }


   public fun addMessage(message: String) {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         var10000.method_7353(Text.method_43470(message) as Text, false)
      }
   }

   public fun sendItemTags() {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         val var3: ItemStack = var10000.method_6047()
         val var10001: java.lang.String = var3.toString()
         this.addMessage(var10001)
      }
   }

   fun PlayerEntity?.isOtherPlayer(): Boolean {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      var10000 != null && `$this$isOtherPlayer` != null && `$this$isOtherPlayer` != var10000 && `$this$isOtherPlayer`.method_5667().version() != 2
   }

   fun getGuiName(gui: Screen?): java.lang.String {
      val var10000: java.lang.String
      if (gui is HandledScreen) {
         var10000 = (gui as HandledScreen).method_25440().getString()
      } else if (gui is InventoryScreen) {
         var10000 = (gui as InventoryScreen).method_25440().getString()
      } else {
         var10000 = ""
      }

      var10000
   }

   public fun screenTitle(): String? {
      val var1: Screen = MinecraftClient.method_1551().field_1755
      val var10000: HandledScreen = var1 as? HandledScreen
      if ((var1 as? HandledScreen) != null) {
         val var2: Text = var10000.method_25440()
         if (var2 != null) {
            return var2.getString()
         }
      }

      return null
   }

   public fun getSidebarTitle(): String? {
      val var10000: ClientWorld = MinecraftClient.method_1551().field_1687
      label16@
      if (var10000 == null) {
         return null
      } else {
         val var3: ScoreboardObjective = var10000.method_8428().method_1189(ScoreboardDisplaySlot.field_45157)
         return if (var3 == null) null else var3.method_1114().getString()
      }
   }

   public fun fullInventory(): Boolean {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         val var3: PlayerInventory = var10000.method_31548()
         if (var3 != null) {
            val inv: PlayerInventory = var3

            repeat(35) { i ->
               if (inv.method_5438(i).method_7960()) {
                  return false
               }
            }

            return true
         }
      }

      return false
   }

   public fun findItemInHotbar(name: String): Int {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         val var5: PlayerInventory = var10000.method_31548()
         if (var5 != null) {
            val inv: PlayerInventory = var5

            repeat(8) { slot ->
               val var6: ItemStack = inv.method_5438(slot)
               if (!var6.method_7960()) {
                  val var7: java.lang.String = var6.method_7964().getString()
                  if (StringsKt.contains(stripColor(var7), name, true)) {
                     return slot
                  }
               }
            }

            return -1
         }
      }

      return -1
   }

   public fun findItemInInventory(name: String): Int {
      val var10000: ClientPlayerEntity = MinecraftClient.method_1551().field_1724
      if (var10000 != null) {
         val var5: PlayerInventory = var10000.method_31548()
         if (var5 != null) {
            val inv: PlayerInventory = var5

            for (slot in 9..35) {
               val var6: ItemStack = inv.method_5438(slot)
               if (!var6.method_7960()) {
                  val var7: java.lang.String = var6.method_7964().getString()
                  if (StringsKt.contains(stripColor(var7), name, true)) {
                     return slot
                  }
               }
            }

            return -1
         }
      }

      return -1
   }

   fun blockPosToYawPitch(pos: BlockPos, from: Vec3d): Pair<java.lang.Float, java.lang.Float> {
      this.vecToYawPitch(Vec3d((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), from)
   }

   fun vecToYawPitch(vec: Vec3d, from: Vec3d): Pair<java.lang.Float, java.lang.Float> {
      val dx: Double = vec.field_1352 - from.field_1352
      val dy: Double = vec.field_1351 - from.field_1351
      val dz: Double = vec.field_1350 - from.field_1350
      TuplesKt.to(
         MathHelper.method_15393((float)(Math.toDegrees(Math.atan2(vec.field_1350 - from.field_1350, dx)) - 90.0)),
         MathHelper.method_15393((float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)))))
      )
   }

   public fun checkThreadAndQueue(run: () -> Unit) {
      this.runOnClientThread(run)
   }

   public fun runOnClientThread(run: () -> Unit) {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      if (var10000.method_18854()) {
         run()
      } else {
         var10000.execute({ 
            `$tmp0`()
         })
      }
   }

   @JvmStatic
   public fun String.stripColor(): String {
      return STRIP_COLOR_PATTERN.replace(`$this$stripColor`, "")
   }

   @JvmStatic
   fun BlockPos.toVec3(): Vec3d {
      Vec3d(`$this$toVec3`.method_10263() + 0.0, `$this$toVec3`.method_10264() + 0.0, `$this$toVec3`.method_10260() + 0.0)
   }

   public fun File.ensureFile(): Boolean {
      return (`$this$ensureFile`.getParentFile().exists() || `$this$ensureFile`.getParentFile().mkdirs())
         && (`$this$ensureFile`.exists() || `$this$ensureFile`.createNewFile())
      }

   public object UseController {
      private final var lastRightClickNs: Long

      public fun canUse(): Boolean {
         return System.nanoTime() - lastRightClickNs > 100000000L
      }

      public fun markUsed() {
         lastRightClickNs = System.nanoTime()
      }
   }
}
