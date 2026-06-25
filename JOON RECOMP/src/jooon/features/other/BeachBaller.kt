package jooon.features.other

import com.mojang.authlib.GameProfile
import java.lang.reflect.Method
import java.time.Instant
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale
import java.util.NoSuchElementException
import jooon.JooonReimagined
import jooon.config.Config
import jooon.config.JooonConfigManager
import jooon.config.PersistentState
import jooon.mixins.InventoryAccessor
import jooon.util.MovableOverlay
import jooon.util.MovableOverlayManager
import jooon.util.PlayerController
import jooon.util.RenderUtils
import jooon.util.Utils
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.class_243
import net.minecraft.client.MinecraftClient
import net.minecraft.client.Mouse
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.option.KeyBinding.Category
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.client.world.ClientWorld
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.SlimeEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.message.SignedMessage
import net.minecraft.network.message.MessageType.Parameters
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nBeachBaller.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BeachBaller.kt\njooon/features/other/BeachBaller\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,928:1\n288#2,2:929\n800#2,11:932\n2333#2,14:943\n288#2,2:957\n800#2,11:959\n766#2:970\n857#2,2:971\n288#2,2:973\n288#2,2:975\n2333#2,14:977\n1#3:931\n1282#4,2:991\n1282#4,2:993\n1282#4,2:995\n1282#4,2:997\n1282#4,2:999\n*S KotlinDebug\n*F\n+ 1 BeachBaller.kt\njooon/features/other/BeachBaller\n*L\n556#1:929,2\n559#1:932,11\n560#1:943,14\n563#1:957,2\n566#1:959,11\n566#1:970\n566#1:971,2\n571#1:973,2\n572#1:975,2\n574#1:977,14\n619#1:991,2\n633#1:993,2\n640#1:995,2\n650#1:997,2\n922#1:999,2\n*E\n"])
public object BeachBaller {
   private final val colorRegex: Regex = Regex("(?i)§[0-9A-FK-OR]")
   private const val SMALL_BEACHBALL_BASE64: String =
      "ewogICJ0aW1lc3RhbXAiIDogMTczNjQyNzQ4ODAwNCwKICAicHJvZmlsZUlkIiA6ICIzN2JhNjRkYzkxOTg0OGI4YjZhNDdiYTg0ZDgwNDM3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTb3lLb3NhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJhZGY5ZDcxMzY3Y2Q2ZTUwNWZiNDhjYWFhNWFjZGNkZmYyYTA5ZjY2YzQ4OGRhZjA0ZDA0NWVlMGJmNTI4ZTEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      private const val LARGE_BEACHBALL_BASE64: String =
      "eyJ0aW1lc3RhbXAiOjE1ODY2NjcxNjgzNzksInByb2ZpbGVJZCI6ImJlY2RkYjI4YTJjODQ5YjRhOWIwOTIyYTU4MDUxNDIwIiwicHJvZmlsZU5hbWUiOiJTdFR2Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTllYTEyMGJkODNkMGM4MWEzYzQ2MjdmNWJjZTFiMTJmYjAzYmNiNTc3NzljNjNkY2M3N2UzZjRhZThhNzkzIn19fQ=="
      private const val TRAIL_MAX_POINTS: Int = 30
   private const val PREDICTION_STEPS: Int = 100
   private const val GRAVITY: Double = 0.03
   private const val DRAG: Double = 0.99
   private const val HEAD_HEIGHT_OFFSET: Double = 1.8
   private const val BALL_SEARCH_RADIUS: Double = 18.0
   private const val LAST_BALL_SPAWN_GRACE_TICKS: Int = 40
   @JvmStatic
   private KeyBinding toggleKey;
   private final var currentTick: Long
   private final val scheduledTasks: ArrayDeque<jooon.features.other.BeachBaller.ScheduledTask> = ArrayDeque()
   private final var wasEnabled: Boolean
   private final var state: jooon.features.other.BeachBaller.State = BeachBaller.State.WAITING
   private final var bounceCount: Int
   private final var tickCounter: Int
   private final var bounceTimer: Long
   private final var hasActiveRun: Boolean
   @JvmStatic
   private Vec3d startPos;
   @JvmStatic
   private Entity trackedBall;
   private final val trailHistory: ArrayDeque<class_243> = ArrayDeque()
   private final var predictedPath: MutableList<class_243> = ArrayList() as java.util.List
   @JvmStatic
   private Vec3d landingPoint;
   private final var lastVelocityY: Double
   private final var ballDescending: Boolean
   private final var awaitingDescendingTicks: Int
   private final var awaitingPlacedBallTicks: Int
   private final var serverBeachBallActiveUntil: Long
   private final var totalBallsBounced: Int
   private final var overlayReady: Boolean

   fun getMc(): MinecraftClient {
      val var10000: MinecraftClient = MinecraftClient.method_1551()
      var10000
   }

   public fun init() {
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.togglebeachballer", 66, Category.field_62556))
      ClientTickEvents.END_CLIENT_TICK.register(lambda_0@{ client: MinecraftClient ->
         val enabled: Int = currentTick++
         INSTANCE.ensureOverlayReady()

         while (true) {
            var var10000: KeyBinding = toggleKey
            if (toggleKey == null) {
               Intrinsics.throwUninitializedPropertyAccessException("toggleKey")
               var10000 = null
            }

            if (!var10000.method_1436()) {
               INSTANCE.runScheduledTasks()
               var var3: Boolean = Config.beachBallerEnabled
               if (Config.beachBallerEnabled && !wasEnabled) {
                  INSTANCE.onEnable()
                  var3 = Config.beachBallerEnabled
               } else if (!Config.beachBallerEnabled && wasEnabled) {
                  onDisable$default(INSTANCE, false, 1, null)
                  var3 = false
               }

               wasEnabled = var3
               if (!var3) {
                  return@lambda_0
               }

               if (client.field_1755 != null && client.field_1755 !is ChatScreen) {
                  disableFeature$default(INSTANCE, false, 1, null)
                  return@lambda_0
               }

               if (client.field_1724 == null) {
                  return@lambda_0
               }

               val player: ClientPlayerEntity = client.field_1724
               if (client.field_1687 == null) {
                  return@lambda_0
               }

               INSTANCE.updateTrajectory(client.field_1724)
               when (BeachBaller.WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
                  1 -> {}
                  2 -> INSTANCE.handleBounceState(player)
                  3 -> INSTANCE.handleReturnState(player)
                  4 -> INSTANCE.handlePlaceState(player)
                  else -> throw NoWhenBranchMatchedException()
               }

               return@lambda_0
            }

            if (Config.beachBallerEnabled) {
               disableFeature$default(INSTANCE, false, 1, null)
            } else {
               INSTANCE.enableFeature()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ context: WorldRenderContext ->
         if (Config.beachBallerEnabled && state != BeachBaller.State.WAITING) {
            INSTANCE.renderTrajectory(context)
         }
      })
      ClientReceiveMessageEvents.GAME
         .register(
            { message: Text, overlay: Boolean ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
         ClientReceiveMessageEvents.CHAT
         .register(
            { message: Text, var1: SignedMessage, var2: GameProfile, var3: Parameters, var4: Instant ->
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
               //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
               //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(Unknown Source)
               //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
               //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toArray(Unknown Source)
               //   at java.base/java.util.stream.ReferencePipeline.toList(Unknown Source)
               //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
            }
         )
      }

   public fun openHudEditor() {
      this.getMc().execute({ 
         INSTANCE.ensureOverlayReady()
         val var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("beachBallerHud")
         if (var10000 != null) {
            var10000.setPositionSilently(PersistentState.beachBallerHudX, PersistentState.beachBallerHudY)
            var10000.openPositioningGUI()
         }
      })
   }

   private fun enableFeature() {
      if (!Config.beachBallerEnabled) {
         Config.beachBallerEnabled = true
      }
   }

   private fun disableFeature(showMessage: Boolean = true) {
      if (Config.beachBallerEnabled) {
         Config.beachBallerEnabled = false
         this.onDisable(showMessage)
         wasEnabled = false
      }
   }

   private fun onEnable() {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 == null) {
         val `$this$onEnable_u24lambda_u246`: BeachBaller = this
         Config.beachBallerEnabled = false
      } else {
         this.setState(BeachBaller.State.PLACE)
         startPos = var10000.method_73189()
         trackedBall = null
         trailHistory.clear()
         predictedPath.clear()
         landingPoint = null
         ballDescending = false
         awaitingDescendingTicks = 0
         awaitingPlacedBallTicks = 0
         serverBeachBallActiveUntil = 0L
         lastVelocityY = 0.0
         hasActiveRun = false
         bounceCount = 0
         scheduledTasks.clear()
         this.trySetMouseGrabbed(false)
         JooonReimagined.Companion.sendMessage("§aBeach Baller enabled.")
         JooonReimagined.Companion.sendMessage("§7It's highly recommended to use Beach Baller in an open area, to avoid collisions.")
      }
   }

   private fun onDisable(showMessage: Boolean = true) {
      scheduledTasks.clear()
      this.releaseInputsNow()
      trackedBall = null
      state = BeachBaller.State.WAITING
      trailHistory.clear()
      predictedPath.clear()
      landingPoint = null
      ballDescending = false
      awaitingDescendingTicks = 0
      awaitingPlacedBallTicks = 0
      serverBeachBallActiveUntil = 0L
      lastVelocityY = 0.0
      hasActiveRun = false
      bounceCount = 0
      this.trySetMouseGrabbed(true)
      if (showMessage) {
         JooonReimagined.Companion.sendMessage("§cBeach Baller disabled.")
      }
   }

   private fun onActionBar(text: String) {
      val match: MatchResult = Regex.find$default(Regex("Bounces: (\\d{1,3})"), colorRegex.replace(text, ""), 0, 2, null)
      if (match != null) {
         val var10000: Int = StringsKt.toIntOrNull(match.getGroupValues().get(1) as java.lang.String)
         bounceCount = var10000 ?: 0
         bounceTimer = System.currentTimeMillis()
      }

      if (System.currentTimeMillis() - bounceTimer > 2000L) {
         bounceCount = 0
      }
   }

   private fun onBeachBallServerMessage(text: String) {
      val now: java.lang.String = colorRegex.replace(text, "")
      val var10000: Locale = Locale.US
      val var7: java.lang.String = now.toLowerCase(var10000)
      val var6: Long = System.currentTimeMillis()
      if (StringsKt.contains$default(var7, "bounce bonanza", false, 2, null)
         || StringsKt.contains$default(var7, "already have an active beach ball", false, 2, null)
         || StringsKt.contains$default(var7, "bouncy beach ball", false, 2, null) && StringsKt.contains$default(var7, "in the air", false, 2, null)
         || StringsKt.contains$default(var7, "giant bouncy beach ball", false, 2, null) && StringsKt.contains$default(var7, "in the air", false, 2, null)) {
         serverBeachBallActiveUntil = var6 + 65000L
         awaitingPlacedBallTicks = Math.max(awaitingPlacedBallTicks, 40)
      }

      if (StringsKt.contains$default(var7, "was kept in the air", false, 2, null)
         || StringsKt.contains$default(var7, "earned", false, 2, null) && StringsKt.contains$default(var7, "fishy treat", false, 2, null)) {
         serverBeachBallActiveUntil = 0L
      }
   }

   fun updateTrajectory(player: ClientPlayerEntity) {
      val ball: Entity = trackedBall
      if (trackedBall != null && trackedBall.method_5805() && !ball.method_31481()) {
         val currentPos: Vec3d = Vec3d(ball.method_23317(), ball.method_23318(), ball.method_23321())
         val velocity: Vec3d = Vec3d(ball.method_23317() - ball.field_6014, ball.method_23318() - ball.field_6036, ball.method_23321() - ball.field_5969)
         if (lastVelocityY > 0.0 && velocity.field_1351 <= 0.0) {
            awaitingDescendingTicks = 5
         }

         if (awaitingDescendingTicks > 0) {
            awaitingDescendingTicks += -1
            if (awaitingDescendingTicks == 0) {
               ballDescending = true
            }
         }

         if (velocity.field_1351 > 0.1) {
            ballDescending = false
            awaitingDescendingTicks = 0
         }

         lastVelocityY = velocity.field_1351
         trailHistory.addLast(currentPos)

         while (trailHistory.size() > 30) {
            trailHistory.removeFirst()
         }

         if (ballDescending && velocity.field_1351 <= 0.0) {
            val var6: Pair = this.predictParabola(player, currentPos, velocity)
            predictedPath = var6.getFirst() as MutableList<Vec3d>
            landingPoint = var6.getSecond() as Vec3d
         } else {
            predictedPath = CollectionsKt.toMutableList(this.simpleExtrapolation(currentPos, velocity))
            landingPoint = null
         }
      } else {
         trailHistory.clear()
         predictedPath.clear()
         landingPoint = null
         ballDescending = false
         awaitingDescendingTicks = 0
      }
   }

   fun simpleExtrapolation(startPos: Vec3d, velocity: Vec3d): MutableList<Vec3d> {
      val path: ArrayList = ArrayList(11)
      var x: Double = 0.0
      x = startPos.field_1352
      var y: Double = 0.0
      y = startPos.field_1351
      var z: Double = 0.0
      z = startPos.field_1350
      path.add(Vec3d(startPos.field_1352, startPos.field_1351, startPos.field_1350))
      val var4: Byte = 10

      repeat(var4) { var5 ->
         x += velocity.field_1352
         y += velocity.field_1351
         z += velocity.field_1350
         path.add(Vec3d(x, y, z))
      }

      path as java.util.List
   }

   fun predictParabola(player: ClientPlayerEntity, startPos: Vec3d, velocity: Vec3d): Pair<java.util.List<Vec3d>, Vec3d> {
      val path: java.util.List = ArrayList()
      var x: Double = startPos.field_1352
      var y: Double = startPos.field_1351
      var z: Double = startPos.field_1350
      var vx: Double = velocity.field_1352
      var vy: Double = velocity.field_1351
      var vz: Double = velocity.field_1350
      var landing: Vec3d = null
      path.add(Vec3d(startPos.field_1352, startPos.field_1351, startPos.field_1350))
      val bounceY: Double = player.method_23318() + 1.8

      repeat(99) { i ->
         val prevX: Double = x
         val prevY: Double = y
         val prevZ: Double = z
         val var33: Double = vy - 0.03
         vx *= 0.99
         vy = var33 * 0.99
         vz *= 0.99
         x += vx
         y += vy
         z += vz
         path.add(Vec3d(x, y, z))
         if (vy < 0.0 && prevY > bounceY && y <= bounceY) {
            landing = Vec3d(prevX + (prevY - bounceY) / (prevY - y) * (x - prevX), bounceY, prevZ + (prevY - bounceY) / (prevY - y) * (z - prevZ))
            break
         }

         if (y < bounceY - 10.0) {
            break
         }
      }

      TuplesKt.to(path, landing)
   }

   private fun renderTrajectory(context: WorldRenderContext) {
      val var10000: MatrixStack = context.matrices()
      if (var10000 != null) {
         val var20: VertexConsumerProvider = context.consumers()
         if (var20 != null) {
            val consumers: VertexConsumerProvider = var20
            val var21: Vec3d = context.gameRenderer().method_19418().method_71156()
            val cameraPos: Vec3d = var21
            val var22: Entry = var10000.method_23760()
            val var23: Matrix4f = var22.method_23761()
            val posMat: Matrix4f = var23
            val normalPose: Entry = var22
            val trail: java.util.List = CollectionsKt.toList(trailHistory as java.lang.Iterable)
            if (trail.size() >= 2) {
               var lp: Int = 0

               for (markerSize in CollectionsKt.getLastIndex(trail)..lp) {
                  RenderUtils.INSTANCE
                     .renderLineRobust(
                        consumers,
                        posMat,
                        normalPose,
                        cameraPos,
                        trail.get(lp) as Vec3d,
                        trail.get(lp + 1) as Vec3d,
                        0.0F,
                        1.0F,
                        1.0F,
                        (float)(80 + 120 * lp / trail.size()) / 255.0F,
                        0.05F
                     )
                  }
            }

            if (predictedPath.size() >= 2) {
               var var15: Int = 0

               for (var17 in CollectionsKt.getLastIndex(predictedPath)..var15) {
                  RenderUtils.INSTANCE
                     .renderLineRobust(
                        consumers,
                        posMat,
                        normalPose,
                        cameraPos,
                        predictedPath.get(var15),
                        predictedPath.get(var15 + 1),
                        1.0F,
                        0.647F,
                        0.0F,
                        RangesKt.coerceAtLeast(200.0F * (1.0F - (float)var15 / (float)predictedPath.size()), 20.0F) / 255.0F,
                        0.05F
                     )
                  }
            }

            if (landingPoint != null) {
               val var16: Vec3d = landingPoint
               RenderUtils.INSTANCE
                  .renderLineRobust(
                     consumers,
                     posMat,
                     normalPose,
                     cameraPos,
                     Vec3d(landingPoint.field_1352 - 0.3, landingPoint.field_1351, landingPoint.field_1350),
                     Vec3d(var16.field_1352 + 0.3, var16.field_1351, var16.field_1350),
                     0.196F,
                     1.0F,
                     0.196F,
                     1.0F,
                     0.07F
                  )
                  RenderUtils.INSTANCE
                  .renderLineRobust(
                     consumers,
                     posMat,
                     normalPose,
                     cameraPos,
                     Vec3d(var16.field_1352, var16.field_1351, var16.field_1350 - 0.3),
                     Vec3d(var16.field_1352, var16.field_1351, var16.field_1350 + 0.3),
                     0.196F,
                     1.0F,
                     0.196F,
                     1.0F,
                     0.07F
                  )
                  val var24: ClientPlayerEntity = this.getMc().field_1724
               val groundY: Double = Math.floor(if (var24 != null) var24.method_23318() else var16.field_1351)
               RenderUtils.INSTANCE
                  .renderBoxOutlineRobust(
                     consumers,
                     posMat,
                     normalPose,
                     cameraPos,
                     Box(
                        Math.floor(var16.field_1352),
                        groundY,
                        Math.floor(var16.field_1350),
                        Math.floor(var16.field_1352) + 1.0,
                        groundY + 1.0,
                        Math.floor(var16.field_1350) + 1.0
                     ),
                     0.196F,
                     1.0F,
                     0.196F,
                     1.0F,
                     0.03F
                  )
               }
         }
      }
   }

   fun handleBounceState(player: ClientPlayerEntity) {
      val ball: Entity = trackedBall
      val hasTrackedBall: Boolean = trackedBall != null && trackedBall.method_5805() && !ball.method_31481()
      val hasRecentBounceUpdate: Boolean = System.currentTimeMillis() - bounceTimer < 1500L
      if (hasTrackedBall && 1 <= bounceCount && bounceCount < 41) {
         hasActiveRun = true
      }

      if (bounceCount <= 40) {
         if (hasTrackedBall) {
            tickCounter = 0
            val var12: Double = ball.method_23317() + (ball.method_23317() - ball.field_6014) * 3.0
            val targetZ: Double = ball.method_23321() + (ball.method_23321() - ball.field_5969) * 3.0
            val distanceFlat: Double = this.horizontalDistance(player.method_23317(), player.method_23321(), var12, targetZ)
            this.setKey("shift", Config.beachBallerHoldShift)
            if (distanceFlat > 0.5) {
               this.setKeysForStraightLineCoords(var12, ball.method_23318(), targetZ)
            }

            if (distanceFlat < 0.2) {
               this.stopMovement()
            }
         } else {
            val var13: Int = tickCounter++
            if (tickCounter > 10) {
               this.setState(BeachBaller.State.RETURN)
               trackedBall = null
            }
         }
      } else {
         if (hasTrackedBall && hasActiveRun && hasRecentBounceUpdate) {
            val var6: Int = totalBallsBounced++
            this.setState(BeachBaller.State.RETURN)
         }

         bounceCount = 0
         hasActiveRun = false
         trackedBall = null
      }
   }

   fun handleReturnState(player: ClientPlayerEntity) {
      this.fullReleaseInputs()
      trackedBall = null
      if (player.method_73189().method_1022(startPos) < 2.0) {
         if (this.findBeachBallSlot() == -1) {
            this.noBallsAndDisable()
         } else {
            this.rightClick()
            this.setState(BeachBaller.State.PLACE)
         }
      } else {
         this.setKeysForStraightLineCoords(startPos.field_1352, startPos.field_1351, startPos.field_1350)
      }
   }

   fun findBeachBall(): Entity {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 == null) {
         null
      } else {
         val player: ClientPlayerEntity = var10000
         val var100: ClientWorld = this.getMc().field_1687
         if (var100 == null) {
            null
         } else {
            val world: ClientWorld = var100
            val var101: Box = var10000.method_5829().method_1014(18.0)
            val searchBox: Box = var101
            val stands: BeachBaller = this

            var `$this$minByOrNull$iv`: BeachBaller
            try {
               `$this$minByOrNull$iv` = stands
               `$this$minByOrNull$iv` = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(
                  world.method_8333(player as Entity, searchBox, { p0: Any ->
                     `$tmp0`(p0)
                  })
               )
            } catch (var18: java.lang.Throwable) {
               `$this$minByOrNull$iv` = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18))
            }

            var var102: Any
            if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$minByOrNull$iv`) == null) {
               var102 = `$this$minByOrNull$iv`
            } else {
               val var103: java.lang.Iterable = var100.method_18112()
               var102 = SequencesKt.toList(SequencesKt.filter(CollectionsKt.asSequence(var103), { entity: Entity ->
                  entity != `$player` && entity.method_5805() && !entity.method_31481() && `$searchBox`.method_994(entity.method_5829())
               }))
            }

            val entities: java.util.List = var102 as java.util.List
            var `iterator$iv`: java.util.Iterator = (var102 as java.util.List).iterator()

            while (true) {
               if (`iterator$iv`.hasNext()) {
                  val `minElem$iv`: Any = `iterator$iv`.next()
                  val `minValue$iv`: Entity = `minElem$iv` as Entity
                  val var105: BeachBaller = INSTANCE
                  if (!var105.hasBeachBallEntityName(`minValue$iv`)) {
                     continue
                  }

                  var102 = `minElem$iv`
                  break
               }

               var102 = null
               break
            }

            val var20: Entity = var102 as Entity
            if (var102 as Entity != null) {
               var20
            } else {
               if (this.isServerBeachBallActive()) {
                  val var47: java.lang.Iterable = entities
                  val var60: java.util.Collection = ArrayList()

                  for (`v$iv` in var47) {
                     if (`v$iv` is SlimeEntity) {
                        var60.add(`v$iv`)
                     }
                  }

                  `iterator$iv` = (var60 as java.util.List).iterator()
                  if (!`iterator$iv`.hasNext()) {
                     var102 = null
                  } else {
                     var var61: Any = `iterator$iv`.next()
                     if (!`iterator$iv`.hasNext()) {
                        var102 = var61
                     } else {
                        var var72: Double = (var61 as SlimeEntity).method_5858(var10000 as Entity)

                        do {
                           val var82: Any = `iterator$iv`.next()
                           val var91: Double = (var82 as SlimeEntity).method_5858(player as Entity)
                           if (java.lang.Double.compare(var72, var91) > 0) {
                              var61 = var82
                              var72 = var91
                           }
                        } while (`iterator$iv`.hasNext())

                        var102 = var61
                     }
                  }

                  val var21: SlimeEntity = var102 as SlimeEntity
                  if (var102 as SlimeEntity != null) {
                     var21 as Entity
                  }

                  `iterator$iv` = entities.iterator()

                  while (true) {
                     if (!`iterator$iv`.hasNext()) {
                        var102 = null
                        break
                     }

                     val var62: Any = `iterator$iv`.next()
                     val var73: Entity = var62 as Entity
                     val var107: BeachBaller = INSTANCE
                     if (var107.isDinnerboneBeachBallCarrier(var73)) {
                        var102 = var62
                        break
                     }
                  }

                  val var22: Entity = var102 as Entity
                  if (var102 as Entity != null) {
                     var22
                  }
               }

               val var50: java.lang.Iterable = entities
               var var63: java.util.Collection = ArrayList()

               for (var92 in var50) {
                  if (var92 is ArmorStandEntity) {
                     var63.add(var92)
                  }
               }

               val var51: java.lang.Iterable = var63 as java.util.List
               var63 = ArrayList()

               for (var93 in var51) {
                  val var109: ItemStack = (var93 as ArmorStandEntity).method_6118(EquipmentSlot.field_6169)
                  if (!var109.method_7960() && (INSTANCE.hasProfileHead(var109) || INSTANCE.hasBeachBallName(var109))) {
                     var63.add(var93)
                  }
               }

               val var23: java.util.List = var63 as java.util.List
               val var65: java.util.Iterator = (var63 as java.util.List).iterator()

               while (true) {
                  if (var65.hasNext()) {
                     val var76: Any = var65.next()
                     val var86: ArmorStandEntity = var76 as ArmorStandEntity
                     val var111: BeachBaller = INSTANCE
                     val var10001: ItemStack = var86.method_6118(EquipmentSlot.field_6169)
                     if (!var111.isBeachBall(var10001)) {
                        continue
                     }

                     var102 = var76
                     break
                  }

                  var102 = null
                  break
               }

               `$this$minByOrNull$iv` = var102 as ArmorStandEntity
               if (var102 as ArmorStandEntity != null) {
                  `$this$minByOrNull$iv` as Entity
               } else {
                  val var66: java.util.Iterator = var23.iterator()

                  while (true) {
                     if (var66.hasNext()) {
                        val var77: Any = var66.next()
                        val var87: ArmorStandEntity = var77 as ArmorStandEntity
                        val var113: BeachBaller = INSTANCE
                        val var115: ItemStack = var87.method_6118(EquipmentSlot.field_6169)
                        if (!var113.hasBeachBallName(var115)) {
                           continue
                        }

                        var102 = var77
                        break
                     }

                     var102 = null
                     break
                  }

                  `$this$minByOrNull$iv` = var102 as ArmorStandEntity
                  if (var102 as ArmorStandEntity != null) {
                     `$this$minByOrNull$iv` as Entity
                  } else {
                     `iterator$iv` = var23.iterator()
                     if (!`iterator$iv`.hasNext()) {
                        var102 = null
                     } else {
                        var var67: Any = `iterator$iv`.next()
                        if (!`iterator$iv`.hasNext()) {
                           var102 = var67
                        } else {
                           var var79: Double = (var67 as ArmorStandEntity).method_5858(player as Entity)

                           do {
                              val var89: Any = `iterator$iv`.next()
                              val var97: Double = (var89 as ArmorStandEntity).method_5858(player as Entity)
                              if (java.lang.Double.compare(var79, var97) > 0) {
                                 var67 = var89
                                 var79 = var97
                              }
                           } while (`iterator$iv`.hasNext())

                           var102 = var67
                        }
                     }

                     var102 as Entity
                  }
               }
            }
         }
      }
   }

   private fun isServerBeachBallActive(): Boolean {
      return System.currentTimeMillis() < serverBeachBallActiveUntil
   }

   fun hasBeachBallEntityName(entity: Entity): Boolean {
      val var10000: Regex = colorRegex
      var var10001: Text = entity.method_5797()
      if (var10001 == null) {
         var10001 = entity.method_5477()
      }

      val var3: java.lang.String = var10001.getString()
      val name: java.lang.String = var10000.replace(var3, "")
      StringsKt.contains(name, "Bouncy Beach Ball", true) || StringsKt.contains(name, "Beach Ball", true)
   }

   fun isDinnerboneBeachBallCarrier(entity: Entity): Boolean {
      val var10000: Regex = colorRegex
      var var10001: Text = entity.method_5797()
      if (var10001 == null) {
         var10001 = entity.method_5477()
      }

      val var3: java.lang.String = var10001.getString()
      StringsKt.equals(var10000.replace(var3, ""), "Dinnerbone", true)
   }

   fun isBeachBall(itemStack: ItemStack): Boolean {
      var profile: Boolean
      try {
         val var10000: ProfileComponent = itemStack.method_58694(DataComponentTypes.field_49617) as ProfileComponent
         if (var10000 == null) {
            false
         }

         val var3: java.lang.String = this.profileData(var10000)
         profile = StringsKt.contains$default(
               var3,
               "ewogICJ0aW1lc3RhbXAiIDogMTczNjQyNzQ4ODAwNCwKICAicHJvZmlsZUlkIiA6ICIzN2JhNjRkYzkxOTg0OGI4YjZhNDdiYTg0ZDgwNDM3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTb3lLb3NhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJhZGY5ZDcxMzY3Y2Q2ZTUwNWZiNDhjYWFhNWFjZGNkZmYyYTA5ZjY2YzQ4OGRhZjA0ZDA0NWVlMGJmNTI4ZTEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
               false,
               2,
               null
            )
            || StringsKt.contains$default(
               var3,
               "eyJ0aW1lc3RhbXAiOjE1ODY2NjcxNjgzNzksInByb2ZpbGVJZCI6ImJlY2RkYjI4YTJjODQ5YjRhOWIwOTIyYTU4MDUxNDIwIiwicHJvZmlsZU5hbWUiOiJTdFR2Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTllYTEyMGJkODNkMGM4MWEzYzQ2MjdmNWJjZTFiMTJmYjAzYmNiNTc3NzljNjNkY2M3N2UzZjRhZThhNzkzIn19fQ==",
               false,
               2,
               null
            )
         } catch (var4: java.lang.Throwable) {
         profile = false
      }

      profile
   }

   fun hasProfileHead(itemStack: ItemStack): Boolean {
      !itemStack.method_7960() && itemStack.method_58694(DataComponentTypes.field_49617) != null
   }

   fun hasBeachBallName(itemStack: ItemStack): Boolean {
      val var10000: Regex = colorRegex
      val var10001: java.lang.String = itemStack.method_7964().getString()
      val name: java.lang.String = var10000.replace(var10001, "")
      StringsKt.contains(name, "Bouncy Beach Ball", true) || StringsKt.contains(name, "Beach Ball", true)
   }

   private fun profileData(profile: Any): String {
      val out: StringBuilder = StringBuilder(profile.toString())

      for (methodName in CollectionsKt.listOf(arrayOf("getGameProfile", "gameProfile", "partialProfile"))) {
         val var7: BeachBaller = this

         var `$this$profileData_u24lambda_u2426`: BeachBaller
         try {
            `$this$profileData_u24lambda_u2426` = var7
            var var10000: Method = profile.getClass().getMethods()
            val `$this$firstOrNull$iv`: Array<Any> = var10000 as Array<Any>
            var var12: Int = 0
            val var13: Int = `$this$firstOrNull$iv`.length

            while (true) {
               if (var12 >= var13) {
                  var10000 = null
                  break
               }

               val `element$iv`: Any = `$this$firstOrNull$iv`[var12]
               if ((`$this$firstOrNull$iv`[var12] as Method).getName() == methodName && (`$this$firstOrNull$iv`[var12] as Method).getParameterCount() == 0) {
                  var10000 = (Method)`element$iv`
                  break
               }

               var12++
            }

            `$this$profileData_u24lambda_u2426` = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(
               if (var10000 as Method != null) var10000.invoke(profile) else null
            )
         } catch (var18: java.lang.Throwable) {
            `$this$profileData_u24lambda_u2426` = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18))
         }

         val var23: Any = if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$profileData_u24lambda_u2426`))
            null
            else
            `$this$profileData_u24lambda_u2426`
            if (var23 != null) {
            out.append(var23)
            this.appendProperties(out, var23)
         }
      }

      val var24: java.lang.String = out.toString()
      return var24
   }

   private fun appendProperties(out: StringBuilder, profile: Any) {
      val var5: BeachBaller = this

      var property: BeachBaller
      try {
         property = var5
         var var10000: Method = profile.getClass().getMethods()
         val methodName: Array<Any> = var10000 as Array<Any>
         var `$i$f$firstOrNull`: Int = 0
         val var11: Int = methodName.length

         while (true) {
            if (`$i$f$firstOrNull` >= var11) {
               var10000 = null
               break
            }

            val `$this$appendProperties_u24lambda_u2432`: Any = methodName[`$i$f$firstOrNull`]
            if ((methodName[`$i$f$firstOrNull`] as Method).getName() == "getProperties" && (methodName[`$i$f$firstOrNull`] as Method).getParameterCount() == 0) {
               var10000 = (Method)`$this$appendProperties_u24lambda_u2432`
               break
            }

            `$i$f$firstOrNull`++
         }

         property = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(if (var10000 as Method != null) var10000.invoke(profile) else null)
      } catch (var24: java.lang.Throwable) {
         property = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var24))
      }

      var var55: Any = if (Result.isFailure_impl/* $VF was: isFailure-impl */(property)) null else property
      if (var55 != null) {
         val properties: Any = var55
         out.append(var55)
         property = this

         var var31: BeachBaller
         try {
            var31 = property
            var55 = properties.getClass().getMethods()
            val var37: Array<Any> = var55 as Array<Any>
            var var40: Int = 0
            val var43: Int = var37.length

            while (true) {
               if (var40 >= var43) {
                  var55 = null
                  break
               }

               val var46: Any = var37[var40]
               if ((var37[var40] as Method).getName() == "values" && (var37[var40] as Method).getParameterCount() == 0) {
                  var55 = var46
                  break
               }

               var40++
            }

            val var17: Any = if (var55 as Method != null) (var55 as Method).invoke(properties) else null
            var31 = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(var17 as? java.lang.Iterable)
         } catch (var23: java.lang.Throwable) {
            var31 = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var23))
         }

         val var58: java.lang.Iterable = (if (Result.isFailure_impl/* $VF was: isFailure-impl */(var31)) null else var31) as java.lang.Iterable
         if (var58 != null) {
            for (var30 in var58) {
               if (var30 != null) {
                  out.append(var30)

                  for (var36 in CollectionsKt.listOf(arrayOf("value", "getValue", "signature", "getSignature", "name", "getName"))) {
                     val var41: BeachBaller = this

                     var var44: BeachBaller
                     try {
                        var44 = var41
                        val var59: Array<Method> = var30.getClass().getMethods()
                        val var49: Array<Any> = var59
                        var var52: Int = 0
                        val var53: Int = var49.length

                        while (true) {
                           if (var52 >= var53) {
                              var55 = null
                              break
                           }

                           val `element$iv`: Any = var49[var52]
                           if ((var49[var52] as Method).getName() == var36 && (var49[var52] as Method).getParameterCount() == 0) {
                              var55 = `element$iv`
                              break
                           }

                           var52++
                        }

                        var44 = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(
                           if (var55 as Method != null) (var55 as Method).invoke(var30) else null
                        )
                     } catch (var22: java.lang.Throwable) {
                        var44 = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var22))
                     }

                     var55 = if (Result.isFailure_impl/* $VF was: isFailure-impl */(var44)) null else var44
                     if (var55 != null) {
                        out.append(var55)
                     }
                  }
               }
            }
         }
      }
   }

   private fun findBeachBallSlot(): Int {
      val bouncy: Int = Utils.INSTANCE.findItemInHotbar("Bouncy Beach Ball")
      return if (bouncy != -1) bouncy else Utils.INSTANCE.findItemInHotbar("Beach Ball")
   }

   private fun setState(newState: jooon.features.other.BeachBaller.State) {
      state = newState
      tickCounter = 0
      if (newState != BeachBaller.State.BOUNCE) {
         hasActiveRun = false
      }

      if (newState === BeachBaller.State.WAITING || newState === BeachBaller.State.RETURN) {
         trackedBall = null
         trailHistory.clear()
         predictedPath.clear()
         landingPoint = null
         ballDescending = false
         awaitingDescendingTicks = 0
      }
   }

   private fun schedule(delayTicks: Int = 0, action: () -> Unit) {
      scheduledTasks.addLast(BeachBaller.ScheduledTask(currentTick + (long)Math.max(1, delayTicks), action))
   }

   private fun runScheduledTasks() {
      if (!scheduledTasks.isEmpty()) {
         val remaining: ArrayDeque = ArrayDeque()
         val due: ArrayList = ArrayList()

         while (!(scheduledTasks as java.util.Collection).isEmpty()) {
            val task: BeachBaller.ScheduledTask = scheduledTasks.removeFirst() as BeachBaller.ScheduledTask
            if (task.runAtTick <= currentTick) {
               due.add(task)
            } else {
               remaining.addLast(task)
            }
         }

         scheduledTasks.addAll(remaining as java.util.Collection)
         var var10000: BeachBaller.ScheduledTask = due.iterator()
         val var9: java.util.Iterator = var10000

         while (var9.hasNext()) {
            var10000 = (BeachBaller.ScheduledTask)var9.next()
            val taskx: BeachBaller.ScheduledTask = var10000
            val var5: BeachBaller = this

            try {
               var var10: BeachBaller = var5
               taskx.action()
               var10 = (BeachBaller)Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
            } catch (var8: java.lang.Throwable) {
               val `$this$runScheduledTasks_u24lambda_u2433`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8))
            }
         }
      }
   }

   fun setItemSlot(player: ClientPlayerEntity, slot: Int) {
      if (0 <= slot && slot < 9) {
         val var10000: PlayerInventory = player.method_31548()
         if ((var10000 as InventoryAccessor).selected != slot) {
            schedule$default(this, 0, { 
               val var10000: PlayerInventory = `$player`.method_31548()
               (var10000 as InventoryAccessor).selected = `$slot`
               Unit.INSTANCE
            }, 1, null)
         }
      }
   }

   private fun rightClick() {
      if (!this.isGuiOpen()) {
         schedule$default(this, 0, { 
            PlayerController.INSTANCE.rightClick()
            Unit.INSTANCE
         }, 1, null)
      }
   }

   private fun setKey(key: String, down: Boolean): Boolean {
      if (this.isGuiOpen() && down) {
         return false
      } else {
         schedule$default(this, 0, { 
            when (`$key`.hashCode()) {
               97 -> {
                  if (`$key`.equals("a")) {
                     PlayerController.INSTANCE.pressLeft(`$down`)
                  }
               }
               100 -> {
                  if (`$key`.equals("d")) {
                     PlayerController.INSTANCE.pressRight(`$down`)
                  }
               }
               115 -> {
                  if (`$key`.equals("s")) {
                     PlayerController.INSTANCE.pressBack(`$down`)
                  }
               }
               119 -> {
                  if (`$key`.equals("w")) {
                     PlayerController.INSTANCE.pressForward(`$down`)
                  }
               }
               109407362 -> {
                  if (`$key`.equals("shift")) {
                     PlayerController.INSTANCE.pressSneak(`$down`)
                  }
               }
               109637894 -> {
                  if (`$key`.equals("space")) {
                     PlayerController.INSTANCE.pressJump(`$down`)
                  }
               }
               1745424865 -> {
                  if (`$key`.equals("leftclick")) {
                     PlayerController.INSTANCE.pressAttack(`$down`)
                  }
               }
               else -> {}
            }

            Unit.INSTANCE
         }, 1, null)
         return true
      }
   }

   private fun setKeysForStraightLineCoords(targetX: Double, targetY: Double, targetZ: Double) {
      val var10000: ClientPlayerEntity = this.getMc().field_1724
      if (var10000 != null) {
         var angle: Double = -Math.toDegrees(Math.atan2(targetX - var10000.method_23317(), targetZ - var10000.method_23321())) - var10000.method_36454()

         while (angle < -180.0) {
            angle += 360.0
         }

         while (angle > 180.0) {
            angle -= 360.0
         }

         this.setCardinalMovement(angle)
      }
   }

   private fun setCardinalMovement(yaw: Double) {
      this.stopMovement()
      if (!this.isGuiOpen()) {
         for (key in if (yaw >= -22.5 && yaw <= 22.5)
            CollectionsKt.listOf("w")
            else
            (
               if (yaw >= -67.5 && yaw <= -22.5)
                  CollectionsKt.listOf(arrayOf("w", "a"))
                  else
                  (
                     if (yaw >= -112.5 && yaw <= -67.5)
                        CollectionsKt.listOf("a")
                        else
                        (
                           if (yaw >= -157.5 && yaw <= -112.5)
                              CollectionsKt.listOf(arrayOf("a", "s"))
                              else
                              (
                                 if (yaw >= -180.0 && yaw <= -157.5)
                                    CollectionsKt.listOf("s")
                                    else
                                    (
                                       if (yaw >= 157.5 && yaw <= 180.0)
                                          CollectionsKt.listOf("s")
                                          else
                                          (
                                             if (yaw >= 22.5 && yaw <= 67.5)
                                                CollectionsKt.listOf(arrayOf("w", "d"))
                                                else
                                                (
                                                   if (yaw >= 67.5 && yaw <= 112.5)
                                                      CollectionsKt.listOf("d")
                                                      else
                                                      (if (yaw >= 112.5 && yaw <= 157.5) CollectionsKt.listOf(arrayOf("s", "d")) else CollectionsKt.emptyList())
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )) {
            this.setKey(key, true)
         }

         this.setKey("space", false)
      }
   }

   private fun stopMovement() {
      this.setKey("w", false)
      this.setKey("a", false)
      this.setKey("s", false)
      this.setKey("d", false)
      this.setKey("space", false)
   }

   private fun fullReleaseInputs() {
      this.stopMovement()
      this.setKey("shift", false)
      this.setKey("leftclick", false)
   }

   private fun releaseInputsNow() {
      PlayerController.INSTANCE.pressForward(false)
      PlayerController.INSTANCE.pressBack(false)
      PlayerController.INSTANCE.pressLeft(false)
      PlayerController.INSTANCE.pressRight(false)
      PlayerController.INSTANCE.pressJump(false)
      PlayerController.INSTANCE.pressSneak(false)
      PlayerController.INSTANCE.pressAttack(false)
   }

   private fun isGuiOpen(): Boolean {
      val screen: Screen = this.getMc().field_1755
      return screen != null && screen !is ChatScreen
   }

   private fun horizontalDistance(x1: Double, z1: Double, x2: Double, z2: Double): Double {
      return Math.hypot(x1 - x2, z1 - z2)
   }

   private fun noBallsAndDisable() {
      JooonReimagined.Companion.sendMessage("§cNo Bouncy Beach Ball found in your hotbar.")
      this.disableFeature(false)
   }

   private fun ensureOverlayReady() {
      if (!overlayReady) {
         overlayReady = true
         if (!PersistentState.beachBallerHudInitDone) {
            PersistentState.beachBallerHudX = 10
            PersistentState.beachBallerHudY = 10
            PersistentState.beachBallerHudInitDone = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
         }

         var var10000: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("beachBallerHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay("beachBallerHud", "Beach Baller HUD", PersistentState.beachBallerHudX, PersistentState.beachBallerHudY, 185, 68)
            }

         var10000.renderFunction = lambda_37@{ context: DrawContext, x: Int, y: Int, var3: Float ->
            if (!INSTANCE.shouldRenderHudContent()) {
               return@lambda_37 Unit.INSTANCE
            } else {
               INSTANCE.renderHud(context, x, y)
               return@lambda_37 Unit.INSTANCE
            }
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.beachBallerHudX = x
            PersistentState.beachBallerHudY = y
            PersistentState.beachBallerHudInitDone = true
            JooonConfigManager.INSTANCE.write("jooonreimagined_state")
            Unit.INSTANCE
         }
         var10000.register()
      }
   }

   private fun shouldRenderHudContent(): Boolean {
      if (Config.beachBallerEnabled && state != BeachBaller.State.WAITING) {
         return true
      } else {
         val var10000: Screen = this.getMc().field_1755
         if (var10000 != null) {
            val var1: Class = var10000.getClass()
            if (var1 != null) {
               return var1.getSimpleName() == "MovableOverlayScreen"
            }
         }

         return null == "MovableOverlayScreen"
      }
   }

   private fun stateName(): String {
      var var10000: java.lang.String
      when (BeachBaller.WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
         1 -> var10000 = "Waiting"
         2 -> var10000 = "Bouncing"
         3 -> var10000 = "Returning"
         4 -> var10000 = "Placing"
         else -> throw NoWhenBranchMatchedException()
      }

      return var10000
   }

   fun renderHud(context: DrawContext, x: Int, y: Int) {
      var var38: Boolean
      run label77@{
         if (!Config.beachBallerEnabled) {
            run label73@{
               val var10000: Screen = this.getMc().field_1755
               if (var10000 != null) {
                  val var36: Class = var10000.getClass()
                  if (var36 != null) {
                     var37 = var36.getSimpleName()
                     return@label73
                  }
               }

               var37 = null
            }

            if (var37 == "MovableOverlayScreen") {
               var38 = true
               return@label77
            }
         }

         var38 = false
      }

      val bodyWidth: Array<java.lang.String> = arrayOf(
         "State: ${if (var38) "Waiting" else this.stateName()}",
         "Bounces: ${if (var38) "0/40" else "${bounceCount}/40"}",
         null
      )
      val var10002: java.lang.String
      if (var38) {
         var10002 = "0"
      } else {
         val panelHeight: Locale = Locale.US
         val var26: Array<Any> = arrayOf(totalBallsBounced)
         var10002 = java.lang.String.format(panelHeight, "%,d", Arrays.copyOf(var26, var26.length))
      }

      bodyWidth[2] = "Total completed: $var10002"
      val lines: java.util.List = CollectionsKt.listOf(bodyWidth)
      val var24: java.util.Iterator = lines.iterator()
      if (!var24.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var28: Int = INSTANCE.getMc().field_1772.method_1727(var24.next() as java.lang.String)

         while (var24.hasNext()) {
            val var31: Int = INSTANCE.getMc().field_1772.method_1727(var24.next() as java.lang.String)
            if (var28 < var31) {
               var28 = var31
            }
         }

         val var23: Int = Math.max(this.getMc().field_1772.method_1727("Beach Baller") + 24, var28 + 18)
         val var25: Int = 14 + this.getMc().field_1772.field_2000 + 10 + lines.size() * (this.getMc().field_1772.field_2000 + 3) + 8
         val var39: MovableOverlay = MovableOverlayManager.INSTANCE.getOverlay("beachBallerHud")
         if (var39 != null) {
            var39.width = var23
            var39.height = var25
         }

         val textColor: Int = -18340
         val textShadow: Int = -9749750
         context.method_25294(x, y, x + var23, y + var25, -870706680)
         context.method_25294(x + 2, y + 2, x + var23 - 2, y + var25 - 2, -869918969)
         context.method_25294(x + 3, y + 3, x + var23 - 3, y + 18, -1439030772)
         context.method_73198(x, y, var23, var25, -22979)
         context.method_73198(x + 1, y + 1, var23 - 2, var25 - 2, -6595306)
         this.renderHudSweep(context, x + 3, y + 3, var23 - 6, 15)
         this.drawHudText(context, "Beach Baller", x + (var23 - this.getMc().field_1772.method_1727("Beach Baller")) / 2, y + 5, -11635, -7385592)
         var lineY: Int = y + 24

         for (line in lines) {
            this.drawHudText(context, line, x + 8, lineY, textColor, textShadow)
            lineY += this.getMc().field_1772.field_2000 + 3
         }
      }
   }

   fun drawHudText(context: DrawContext, text: java.lang.String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.method_51439(this.getMc().field_1772, Text.method_43470(text) as Text, x + 1, y + 1, shadowColor, false)
      context.method_51439(this.getMc().field_1772, Text.method_43470(text) as Text, x, y, color, false)
   }

   fun renderHudSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {
      val sweepWidth: Int = Math.max(18, width / 5)
      val sweepX: Int = x - sweepWidth + (int)((width + sweepWidth) * ((float)(System.currentTimeMillis() % 2300L) / (float)2300L))
      val left: Int = RangesKt.coerceAtLeast(sweepX, x)
      val right: Int = RangesKt.coerceAtMost(sweepX + sweepWidth, x + width)
      if (right > left) {
         context.method_25294(left, y, right, y + height, 872398443)
      }
   }

   private fun trySetMouseGrabbed(grab: Boolean) {
      val var10000: Mouse = this.getMc().field_1729
      if (var10000 != null) {
         val handler: Mouse = var10000

         for (name in if (grab) CollectionsKt.listOf(arrayOf("grabMouse", "grab")) else CollectionsKt.listOf(arrayOf("releaseMouse", "release"))) {
            val var23: Array<Method> = handler.getClass().getMethods()
            var `$this$trySetMouseGrabbed_u24lambda_u2442`: Array<Any> = var23
            var var10: Int = 0
            val var11: Int = `$this$trySetMouseGrabbed_u24lambda_u2442`.length

            while (true) {
               if (var10 >= var11) {
                  var24 = null
                  break
               }

               val `element$iv`: Any = `$this$trySetMouseGrabbed_u24lambda_u2442`[var10]
               if ((`$this$trySetMouseGrabbed_u24lambda_u2442`[var10] as Method).getName() == name
                  && (`$this$trySetMouseGrabbed_u24lambda_u2442`[var10] as Method).getParameterCount() == 0) {
                  var24 = `element$iv`
                  break
               }

               var10++
            }

            val var25: Method = var24 as Method
            if (var24 as Method != null) {
               val method: Method = var25
               val var7: BeachBaller = this

               try {
                  val var19: BeachBaller = var7
                  `$this$trySetMouseGrabbed_u24lambda_u2442` = (Object[])Result.constructor_impl/* $VF was: constructor-impl */(method.invoke(handler))
               } catch (var15: java.lang.Throwable) {
                  `$this$trySetMouseGrabbed_u24lambda_u2442` = (Object[])Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
               }

               return
            }
         }
      }
   }

   @JvmStatic
   fun {
      val var10000: Vec3d = Vec3d.field_1353
      startPos = var10000
   }

   private data class ScheduledTask(runAtTick: Long, action: () -> Unit) {
      public final val runAtTick: Long
      public final val action: () -> Unit

      init {
         this.runAtTick = runAtTick
         this.action = action
      }

      public operator fun component1(): Long {
         return this.runAtTick
      }

      public operator fun component2(): () -> Unit {
         return this.action
      }

      public fun copy(runAtTick: Long = this.runAtTick, action: () -> Unit = this.action): jooon.features.other.BeachBaller.ScheduledTask {
         return BeachBaller.ScheduledTask(runAtTick, action)
      }

      public override fun toString(): String {
         return "ScheduledTask(runAtTick=${this.runAtTick}, action=${this.action})"
      }

      public override fun hashCode(): Int {
         return java.lang.Long.hashCode(this.runAtTick) * 31 + this.action.hashCode()
      }

      public override operator fun equals(other: Any?): Boolean {
         label28@
         if (this === other) {
            return true
         } else {
            return other is BeachBaller.ScheduledTask
               && this.runAtTick == (other as BeachBaller.ScheduledTask).runAtTick
               && this.action == (other as BeachBaller.ScheduledTask).action
            }
      }
   }

   private enum class State {
      WAITING,
      BOUNCE,
      RETURN,
      PLACE;

      @JvmStatic
      fun getEntries(): EnumEntries<BeachBaller.State> {
         $ENTRIES
      }
   }
}
