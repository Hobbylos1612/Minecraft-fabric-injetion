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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.util.math.Vec3d
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

object BeachBaller {
   private val colorRegex: Regex = Regex("(?i)§[0-9A-FK-OR]")
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
   
   private KeyBinding toggleKey;
   private var currentTick: Long
   private val scheduledTasks: ArrayDeque<jooon.features.other.BeachBaller.ScheduledTask> = ArrayDeque()
   private var wasEnabled: Boolean
   private var state: jooon.features.other.BeachBaller.State = BeachBaller.State.WAITING
   private var bounceCount: Int
   private var tickCounter: Int
   private var bounceTimer: Long
   private var hasActiveRun: Boolean
   
   private Vec3d startPos;
   
   private Entity trackedBall;
   private val trailHistory: ArrayDeque<Vec3d> = ArrayDeque()
   private var predictedPath: MutableList<Vec3d> = ArrayList() as java.util.List
   
   private Vec3d landingPoint;
   private var lastVelocityY: Double
   private var ballDescending: Boolean
   private var awaitingDescendingTicks: Int
   private var awaitingPlacedBallTicks: Int
   private var serverBeachBallActiveUntil: Long
   private var totalBallsBounced: Int
   private var overlayReady: Boolean

   fun getMc(): MinecraftClient {
return var10000
   }

   fun init() {
      toggleKey = KeyBindingHelper.registerKeyBinding(KeyBinding("key.jooonreimagined.togglebeachballer", 66, Category.MISC))
      ClientTickEvents.END_CLIENT_TICK.register(lambda_0@{ client: MinecraftClient ->

         ensureOverlayReady()

         while (true) {
            var var10000: KeyBinding = toggleKey
            if (toggleKey == null) {
               throwUninitializedPropertyAccessException("toggleKey")
               var10000 = null
            }

            if (!var10000.wasPressed()) {
               runScheduledTasks()
               var var3: Boolean = Config.beachBallerEnabled
               if (Config.beachBallerEnabled && !wasEnabled) {
                  onEnable()
                  var3 = Config.beachBallerEnabled
               } else if (!Config.beachBallerEnabled && wasEnabled) {
                  onDisable$default(INSTANCE, false, 1, null)
                  var3 = false
               }

               wasEnabled = var3
               if (!var3) {
                  return@lambda_0
               }

               if (client.currentScreen != null && client.currentScreen !is ChatScreen) {
                  disableFeature$default(INSTANCE, false, 1, null)
                  return@lambda_0
               }

               if (client.player == null) {
                  return@lambda_0
               }

               if (client.world == null) {
                  return@lambda_0
               }

               updateTrajectory(client.player)
               when (BeachBaller.WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
                  1 -> {}
                  2 -> handleBounceState(player)
                  3 -> handleReturnState(player)
                  4 -> handlePlaceState(player)
                  else -> throw NoWhenBranchMatchedException()
               }

               return@lambda_0
            }

            if (Config.beachBallerEnabled) {
               disableFeature$default(INSTANCE, false, 1, null)
            } else {
               enableFeature()
            }
         }
      })
      WorldRenderEvents.END_MAIN.register({ context: WorldRenderContext ->
         if (Config.beachBallerEnabled && state != BeachBaller.State.WAITING) {
            renderTrajectory(context)
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

   fun openHudEditor() {
      this.getMc().execute({ 
         ensureOverlayReady()

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

      if (var10000 == null) {
         val `this24lambda_u246`: BeachBaller = this
         Config.beachBallerEnabled = false
      } else {
         this.setState(BeachBaller.State.PLACE)
         startPos = var10000.getEntityPos()
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

      if (match != null) {

         bounceCount = var10000 ?: 0
         bounceTimer = System.currentTimeMillis()
      }

      if (System.currentTimeMillis() - bounceTimer > 2000L) {
         bounceCount = 0
      }
   }

   private fun onBeachBallServerMessage(text: String) {




      if (contains$default(var7, "bounce bonanza", false, 2, null)
         || contains$default(var7, "already have an active beach ball", false, 2, null)
         || contains$default(var7, "bouncy beach ball", false, 2, null) && contains$default(var7, "in the air", false, 2, null)
         || contains$default(var7, "giant bouncy beach ball", false, 2, null) && contains$default(var7, "in the air", false, 2, null)) {
         serverBeachBallActiveUntil = var6 + 65000L
         awaitingPlacedBallTicks = Math.max(awaitingPlacedBallTicks, 40)
      }

      if (contains$default(var7, "was kept in the air", false, 2, null)
         || contains$default(var7, "earned", false, 2, null) && contains$default(var7, "fishy treat", false, 2, null)) {
         serverBeachBallActiveUntil = 0L
      }
   }

   fun updateTrajectory(player: ClientPlayerEntity) {

      if (trackedBall != null && trackedBall.isAlive() && !ball.isRemoved()) {


         if (lastVelocityY > 0.0 && velocity.y <= 0.0) {
            awaitingDescendingTicks = 5
         }

         if (awaitingDescendingTicks > 0) {
            awaitingDescendingTicks += -1
            if (awaitingDescendingTicks == 0) {
               ballDescending = true
            }
         }

         if (velocity.y > 0.1) {
            ballDescending = false
            awaitingDescendingTicks = 0
         }

         lastVelocityY = velocity.y
         trailHistory.addLast(currentPos)

         while (trailHistory.size() > 30) {
            trailHistory.removeFirst()
         }

         if (ballDescending && velocity.y <= 0.0) {

            predictedPath = var6.getFirst() as MutableList<Vec3d>
            landingPoint = var6.getSecond() as Vec3d
         } else {
            predictedPath = toMutableList(this.simpleExtrapolation(currentPos, velocity))
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

      var x: Double = 0.0
      x = startPos.x
      var y: Double = 0.0
      y = startPos.y
      var z: Double = 0.0
      z = startPos.z
      path.add(Vec3d(startPos.x, startPos.y, startPos.z))


      repeat(var4) { var5 ->
         x += velocity.x
         y += velocity.y
         z += velocity.z
         path.add(Vec3d(x, y, z))
      }

      path as java.util.List
   }

   fun predictParabola(player: ClientPlayerEntity, startPos: Vec3d, velocity: Vec3d): Pair<java.util.List<Vec3d>, Vec3d> {
      val path: java.util.List = ArrayList()
      var x: Double = startPos.x
      var y: Double = startPos.y
      var z: Double = startPos.z
      var vx: Double = velocity.x
      var vy: Double = velocity.y
      var vz: Double = velocity.z
      var landing: Vec3d = null
      path.add(Vec3d(startPos.x, startPos.y, startPos.z))


      repeat(99) { i ->




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

      Pair(path, landing)
   }

   private fun renderTrajectory(context: WorldRenderContext) {

      if (var10000 != null) {

         if (var20 != null) {







            val trail: java.util.List = toList(trailHistory as java.lang.Iterable)
            if (trail.size() >= 2) {
               var lp: Int = 0

               for (markerSize in getLastIndex(trail)..lp) {
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
                        (80 + 120 * lp / trail.size()).toFloat() / 255.0F,
                        0.05F
                     )
                  }
            }

            if (predictedPath.size() >= 2) {
               var var15: Int = 0

               for (var17 in getLastIndex(predictedPath)..var15) {
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
                        (200.0F * (1.0F - var15.toFloat() / predictedPath.size().toFloat())).coerceAtLeast(20.0F) / 255.0F,
                        0.05F
                     )
                  }
            }

            if (landingPoint != null) {

               RenderUtils.INSTANCE
                  .renderLineRobust(
                     consumers,
                     posMat,
                     normalPose,
                     cameraPos,
                     Vec3d(landingPoint.x - 0.3, landingPoint.y, landingPoint.z),
                     Vec3d(var16.x + 0.3, var16.y, var16.z),
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
                     Vec3d(var16.x, var16.y, var16.z - 0.3),
                     Vec3d(var16.x, var16.y, var16.z + 0.3),
                     0.196F,
                     1.0F,
                     0.196F,
                     1.0F,
                     0.07F
                  )


               RenderUtils.INSTANCE
                  .renderBoxOutlineRobust(
                     consumers,
                     posMat,
                     normalPose,
                     cameraPos,
                     Box(
                        Math.floor(var16.x),
                        groundY,
                        Math.floor(var16.z),
                        Math.floor(var16.x) + 1.0,
                        groundY + 1.0,
                        Math.floor(var16.z) + 1.0
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



      if (hasTrackedBall && 1 <= bounceCount && bounceCount < 41) {
         hasActiveRun = true
      }

      if (bounceCount <= 40) {
         if (hasTrackedBall) {
            tickCounter = 0



            this.setKey("shift", Config.beachBallerHoldShift)
            if (distanceFlat > 0.5) {
               this.setKeysForStraightLineCoords(var12, ball.getY(), targetZ)
            }

            if (distanceFlat < 0.2) {
               this.stopMovement()
            }
         } else {

            if (tickCounter > 10) {
               this.setState(BeachBaller.State.RETURN)
               trackedBall = null
            }
         }
      } else {
         if (hasTrackedBall && hasActiveRun && hasRecentBounceUpdate) {

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
      if (player.getEntityPos().distanceTo(startPos) < 2.0) {
         if (this.findBeachBallSlot() == -1) {
            this.noBallsAndDisable()
         } else {
            this.rightClick()
            this.setState(BeachBaller.State.PLACE)
         }
      } else {
         this.setKeysForStraightLineCoords(startPos.x, startPos.y, startPos.z)
      }
   }

   fun findBeachBall(): Entity {

      if (var10000 == null) {
return null
      } else {


         if (var100 == null) {
return null
         } else {





            var `this$iv`: BeachBaller
            try {
               `this$iv` = stands
               `this$iv` = Result(
                  world.getOtherEntities(player as Entity, searchBox, { p0: Any ->
                     ``(p0)
                  })
               )
            } catch (var18: java.lang.Throwable) {
               `this$iv` = Result(ResultKt.createFailure(var18))
            }

            var var102: Any
            if (Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`this$iv`) == null) {
               var102 = `this$iv`
            } else {
               val var103: java.lang.Iterable = var100.getEntities()
               var102 = toList(filter(asSequence(var103), { entity: Entity ->
                  entity != `$player` && entity.isAlive() && !entity.isRemoved() && `$searchBox`.intersects(entity.getBoundingBox())
               }))
            }

            val entities: java.util.List = var102 as java.util.List
            var `iterator$iv`: java.util.Iterator = (var102 as java.util.List).iterator()

            while (true) {
               if (`iterator$iv`.hasNext()) {
                  val `minElem$iv`: Any = `iterator$iv`.next()
                  val `minValue$iv`: Entity = `minElem$iv` as Entity

                  if (!var105.hasBeachBallEntityName(`minValue$iv`)) {
return continue
                  }

                  var102 = `minElem$iv`
break
               }

               var102 = null
break
            }

            if (var102 as Entity != null) {
return var20
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
                        var var72: Double = (var61 as SlimeEntity).squaredDistanceTo(var10000 as Entity)

                        do {


                           if (java.lang.Double.compare(var72, var91) > 0) {
                              var61 = var82
                              var72 = var91
                           }
                        } while (`iterator$iv`.hasNext())

                        var102 = var61
                     }
                  }

                  if (var102 as SlimeEntity != null) {
                     var21 as Entity
                  }

                  `iterator$iv` = entities.iterator()

                  while (true) {
                     if (!`iterator$iv`.hasNext()) {
                        var102 = null
break
                     }



                     if (var107.isDinnerboneBeachBallCarrier(var73)) {
                        var102 = var62
break
                     }
                  }

                  if (var102 as Entity != null) {
return var22
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

                  if (!var109.isEmpty() && (hasProfileHead(var109) || hasBeachBallName(var109))) {
                     var63.add(var93)
                  }
               }

               val var23: java.util.List = var63 as java.util.List
               val var65: java.util.Iterator = (var63 as java.util.List).iterator()

               while (true) {
                  if (var65.hasNext()) {




                     if (!var111.isBeachBall(var10001)) {
return continue
                     }

                     var102 = var76
break
                  }

                  var102 = null
break
               }

               `this$iv` = var102 as ArmorStandEntity
               if (var102 as ArmorStandEntity != null) {
                  `this$iv` as Entity
               } else {
                  val var66: java.util.Iterator = var23.iterator()

                  while (true) {
                     if (var66.hasNext()) {




                        if (!var113.hasBeachBallName(var115)) {
return continue
                        }

                        var102 = var77
break
                     }

                     var102 = null
break
                  }

                  `this$iv` = var102 as ArmorStandEntity
                  if (var102 as ArmorStandEntity != null) {
                     `this$iv` as Entity
                  } else {
                     `iterator$iv` = var23.iterator()
                     if (!`iterator$iv`.hasNext()) {
                        var102 = null
                     } else {
                        var var67: Any = `iterator$iv`.next()
                        if (!`iterator$iv`.hasNext()) {
                           var102 = var67
                        } else {
                           var var79: Double = (var67 as ArmorStandEntity).squaredDistanceTo(player as Entity)

                           do {


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

      var var10001: Text = entity.getCustomName()
      if (var10001 == null) {
         var10001 = entity.getName()
      }


      name.contains("Bouncy Beach Ball", true) || name.contains("Beach Ball", true)
   }

   fun isDinnerboneBeachBallCarrier(entity: Entity): Boolean {

      var var10001: Text = entity.getCustomName()
      if (var10001 == null) {
         var10001 = entity.getName()
      }

      equals(var10000.replace(var3, ""), "Dinnerbone", true)
   }

   fun isBeachBall(itemStack: ItemStack): Boolean {
      var profile: Boolean
      try {

         if (var10000 == null) {
return false
         }

         profile = contains$default(
               var3,
               "ewogICJ0aW1lc3RhbXAiIDogMTczNjQyNzQ4ODAwNCwKICAicHJvZmlsZUlkIiA6ICIzN2JhNjRkYzkxOTg0OGI4YjZhNDdiYTg0ZDgwNDM3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTb3lLb3NhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJhZGY5ZDcxMzY3Y2Q2ZTUwNWZiNDhjYWFhNWFjZGNkZmYyYTA5ZjY2YzQ4OGRhZjA0ZDA0NWVlMGJmNTI4ZTEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
               false,
               2,
return null
            )
            || contains$default(
               var3,
               "eyJ0aW1lc3RhbXAiOjE1ODY2NjcxNjgzNzksInByb2ZpbGVJZCI6ImJlY2RkYjI4YTJjODQ5YjRhOWIwOTIyYTU4MDUxNDIwIiwicHJvZmlsZU5hbWUiOiJTdFR2Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTllYTEyMGJkODNkMGM4MWEzYzQ2MjdmNWJjZTFiMTJmYjAzYmNiNTc3NzljNjNkY2M3N2UzZjRhZThhNzkzIn19fQ==",
               false,
               2,
return null
            )
         } catch (var4: java.lang.Throwable) {
         profile = false
      }
return profile
   }

   fun hasProfileHead(itemStack: ItemStack): Boolean {
      !itemStack.isEmpty() && itemStack.get(DataComponentTypes.PROFILE) != null
   }

   fun hasBeachBallName(itemStack: ItemStack): Boolean {



      name.contains("Bouncy Beach Ball", true) || name.contains("Beach Ball", true)
   }

   private fun profileData(profile: Any): String {


      for (methodName in listOf(arrayOf("getGameProfile", "gameProfile", "partialProfile"))) {


         var `this24lambda_u2426`: BeachBaller
         try {
            `this24lambda_u2426` = var7
            var var10000: Method = profile.getClass().getMethods()
            val `this$iv`: Array<Any> = var10000 as Array<Any>
            var var12: Int = 0


            while (true) {
               if (var12 >= var13) {
                  var10000 = null
break
               }

               val `element$iv`: Any = `this$iv`[var12]
               if ((`this$iv`[var12] as Method).getName() == methodName && (`this$iv`[var12] as Method).getParameterCount() == 0) {
                  var10000 = (Method)`element$iv`
break
               }

               var12++
            }

            `this24lambda_u2426` = Result(
               if (var10000 as Method != null) var10000.invoke(profile) else null
            )
         } catch (var18: java.lang.Throwable) {
            `this24lambda_u2426` = Result(ResultKt.createFailure(var18))
         }
return null
return else
            `this24lambda_u2426`
            if (var23 != null) {
            out.append(var23)
            this.appendProperties(out, var23)
         }
      }

      return var24
   }

   private fun appendProperties(out: StringBuilder, profile: Any) {


      var property: BeachBaller
      try {
         property = var5
         var var10000: Method = profile.getClass().getMethods()
         val methodName: Array<Any> = var10000 as Array<Any>
         var ``: Int = 0


         while (true) {
            if (`` >= var11) {
               var10000 = null
break
            }

            val `this24lambda_u2432`: Any = methodName[``]
            if ((methodName[``] as Method).getName() == "getProperties" && (methodName[``] as Method).getParameterCount() == 0) {
               var10000 = (Method)`this24lambda_u2432`
break
            }

            ``++
         }

         property = Result(if (var10000 as Method != null) var10000.invoke(profile) else null)
      } catch (var24: java.lang.Throwable) {
         property = Result(ResultKt.createFailure(var24))
      }

      var var55: Any = if (Result.isFailure/* $VF was: isFailure-impl */(property)) null else property
      if (var55 != null) {

         out.append(var55)
         property = this

         var var31: BeachBaller
         try {
            var31 = property
            var55 = properties.getClass().getMethods()
            val var37: Array<Any> = var55 as Array<Any>
            var var40: Int = 0


            while (true) {
               if (var40 >= var43) {
                  var55 = null
break
               }

               if ((var37[var40] as Method).getName() == "values" && (var37[var40] as Method).getParameterCount() == 0) {
                  var55 = var46
break
               }

               var40++
            }

            var31 = Result(var17 as? java.lang.Iterable)
         } catch (var23: java.lang.Throwable) {
            var31 = Result(ResultKt.createFailure(var23))
         }

         val var58: java.lang.Iterable = (if (Result.isFailure/* $VF was: isFailure-impl */(var31)) null else var31) as java.lang.Iterable
         if (var58 != null) {
            for (var30 in var58) {
               if (var30 != null) {
                  out.append(var30)

                  for (var36 in listOf(arrayOf("value", "getValue", "signature", "getSignature", "name", "getName"))) {


                     var var44: BeachBaller
                     try {
                        var44 = var41
                        val var59: Array<Method> = var30.getClass().getMethods()
                        val var49: Array<Any> = var59
                        var var52: Int = 0


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

                        var44 = Result(
                           if (var55 as Method != null) (var55 as Method).invoke(var30) else null
                        )
                     } catch (var22: java.lang.Throwable) {
                        var44 = Result(ResultKt.createFailure(var22))
                     }

                     var55 = if (Result.isFailure/* $VF was: isFailure-impl */(var44)) null else var44
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

      return if (bouncy != -1) bouncy else Utils.findItemInHotbar("Beach Ball")
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
      scheduledTasks.addLast(BeachBaller.ScheduledTask(currentTick + Math.max(1, delayTicks).toLong(), action))
   }

   private fun runScheduledTasks() {
      if (!scheduledTasks.isEmpty()) {



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


            try {
               var var10: BeachBaller = var5
               taskx.action()
               var10 = Result(Unit)
            } catch (var8: java.lang.Throwable) {
               val `this24lambda_u2433`: Any = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8))
            }
         }
      }
   }

   fun setItemSlot(player: ClientPlayerEntity, slot: Int) {
      if (0 <= slot && slot < 9) {

         if ((var10000 as InventoryAccessor).selected != slot) {
            schedule$default(this, 0, { 

               (var10000 as InventoryAccessor).selected = `$slot`
return Unit
            }, 1, null)
         }
      }
   }

   private fun rightClick() {
      if (!this.isGuiOpen()) {
         schedule$default(this, 0, { 
            PlayerController.rightClick()
return Unit
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
                     PlayerController.pressLeft(`$down`)
                  }
               }
               100 -> {
                  if (`$key`.equals("d")) {
                     PlayerController.pressRight(`$down`)
                  }
               }
               115 -> {
                  if (`$key`.equals("s")) {
                     PlayerController.pressBack(`$down`)
                  }
               }
               119 -> {
                  if (`$key`.equals("w")) {
                     PlayerController.pressForward(`$down`)
                  }
               }
               109407362 -> {
                  if (`$key`.equals("shift")) {
                     PlayerController.pressSneak(`$down`)
                  }
               }
               109637894 -> {
                  if (`$key`.equals("space")) {
                     PlayerController.pressJump(`$down`)
                  }
               }
               1745424865 -> {
                  if (`$key`.equals("leftclick")) {
                     PlayerController.pressAttack(`$down`)
                  }
               }
               else -> {}
            }
return Unit
         }, 1, null)
         return true
      }
   }

   private fun setKeysForStraightLineCoords(targetX: Double, targetY: Double, targetZ: Double) {

      if (var10000 != null) {
         var angle: Double = -Math.toDegrees(Math.atan2(targetX - var10000.getX(), targetZ - var10000.getZ())) - var10000.getYaw()

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
            listOf("w")
return else
            (
               if (yaw >= -67.5 && yaw <= -22.5)
                  listOf(arrayOf("w", "a"))
return else
                  (
                     if (yaw >= -112.5 && yaw <= -67.5)
                        listOf("a")
return else
                        (
                           if (yaw >= -157.5 && yaw <= -112.5)
                              listOf(arrayOf("a", "s"))
return else
                              (
                                 if (yaw >= -180.0 && yaw <= -157.5)
                                    listOf("s")
return else
                                    (
                                       if (yaw >= 157.5 && yaw <= 180.0)
                                          listOf("s")
return else
                                          (
                                             if (yaw >= 22.5 && yaw <= 67.5)
                                                listOf(arrayOf("w", "d"))
return else
                                                (
                                                   if (yaw >= 67.5 && yaw <= 112.5)
                                                      listOf("d")
return else
                                                      (if (yaw >= 112.5 && yaw <= 157.5) listOf(arrayOf("s", "d")) else emptyList())
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
      PlayerController.pressForward(false)
      PlayerController.pressBack(false)
      PlayerController.pressLeft(false)
      PlayerController.pressRight(false)
      PlayerController.pressJump(false)
      PlayerController.pressSneak(false)
      PlayerController.pressAttack(false)
   }

   private fun isGuiOpen(): Boolean {

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
            JooonConfigManager.write("jooonreimagined_state")
         }

         var var10000: MovableOverlay = MovableOverlayManager.getOverlay("beachBallerHud")
         if (var10000 == null) {
            var10000 = MovableOverlayManager.INSTANCE
               .createOverlay("beachBallerHud", "Beach Baller HUD", PersistentState.beachBallerHudX, PersistentState.beachBallerHudY, 185, 68)
            }

         var10000.renderFunction = lambda_37@{ context: DrawContext, x: Int, y: Int, var3: Float ->
            if (!shouldRenderHudContent()) {
               return@lambda_37 Unit
            } else {
               renderHud(context, x, y)
               return@lambda_37 Unit
            }
         }
         var10000.onPositionChanged = { x: Int, y: Int ->
            PersistentState.beachBallerHudX = x
            PersistentState.beachBallerHudY = y
            PersistentState.beachBallerHudInitDone = true
            JooonConfigManager.write("jooonreimagined_state")
return Unit
         }
         var10000.register()
      }
   }

   private fun shouldRenderHudContent(): Boolean {
      if (Config.beachBallerEnabled && state != BeachBaller.State.WAITING) {
         return true
      } else {

         if (var10000 != null) {

            if (var1 != null) {
               return var1.getSimpleName() == "MovableOverlayScreen"
            }
         }

         return null == "MovableOverlayScreen"
      }
   }

   private fun stateName(): String {
      var var10000: String
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

               if (var10000 != null) {

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

      val bodyWidth: Array<String> = arrayOf(
         "State: ${if (var38) "Waiting" else this.stateName()}",
         "Bounces: ${if (var38) "0/40" else "${bounceCount}/40"}",
return null
      )
      val var10002: String
      if (var38) {
         var10002 = "0"
      } else {

         val var26: Array<Any> = arrayOf(totalBallsBounced)
         var10002 = java.lang.String.format(panelHeight, "%,d", Arrays.copyOf(var26, var26.length))
      }

      bodyWidth[2] = "Total completed: $var10002"
      val lines: java.util.List = listOf(bodyWidth)
      val var24: java.util.Iterator = lines.iterator()
      if (!var24.hasNext()) {
         throw NoSuchElementException()
      } else {
         var var28: Int = getMc().textRenderer.getWidth(var24.next() as String)

         while (var24.hasNext()) {

            if (var28 < var31) {
               var28 = var31
            }
         }



         if (var39 != null) {
            var39.width = var23
            var39.height = var25
         }


         context.fill(x, y, x + var23, y + var25, -870706680)
         context.fill(x + 2, y + 2, x + var23 - 2, y + var25 - 2, -869918969)
         context.fill(x + 3, y + 3, x + var23 - 3, y + 18, -1439030772)
         context.drawStrokedRectangle(x, y, var23, var25, -22979)
         context.drawStrokedRectangle(x + 1, y + 1, var23 - 2, var25 - 2, -6595306)
         this.renderHudSweep(context, x + 3, y + 3, var23 - 6, 15)
         this.drawHudText(context, "Beach Baller", x + (var23 - this.getMc().textRenderer.getWidth("Beach Baller")) / 2, y + 5, -11635, -7385592)
         var lineY: Int = y + 24

         for (line in lines) {
            this.drawHudText(context, line, x + 8, lineY, textColor, textShadow)
            lineY += this.getMc().textRenderer.fontHeight + 3
         }
      }
   }

   fun drawHudText(context: DrawContext, text: String, x: Int, y: Int, color: Int, shadowColor: Int) {
      context.drawText(this.getMc().textRenderer, Text.literal(text) as Text, x + 1, y + 1, shadowColor, false)
      context.drawText(this.getMc().textRenderer, Text.literal(text) as Text, x, y, color, false)
   }

   fun renderHudSweep(context: DrawContext, x: Int, y: Int, width: Int, height: Int) {




      if (right > left) {
         context.fill(left, y, right, y + height, 872398443)
      }
   }

   private fun trySetMouseGrabbed(grab: Boolean) {

      if (var10000 != null) {


         for (name in if (grab) listOf(arrayOf("grabMouse", "grab")) else listOf(arrayOf("releaseMouse", "release"))) {
            val var23: Array<Method> = handler.getClass().getMethods()
            var `this24lambda_u2442`: Array<Any> = var23
            var var10: Int = 0


            while (true) {
               if (var10 >= var11) {
                  var24 = null
break
               }

               val `element$iv`: Any = `this24lambda_u2442`[var10]
               if ((`this24lambda_u2442`[var10] as Method).getName() == name
                  && (`this24lambda_u2442`[var10] as Method).getParameterCount() == 0) {
                  var24 = `element$iv`
break
               }

               var10++
            }

            if (var24 as Method != null) {



               try {

                  `this24lambda_u2442` = (Object[])Result.constructor_impl/* $VF was: constructor-impl */(method.invoke(handler))
               } catch (var15: java.lang.Throwable) {
                  `this24lambda_u2442` = (Object[])Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var15))
               }
return return
            }
         }
      }
   }

   
   fun {

      startPos = var10000
   }

   private data class ScheduledTask(runAtTick: Long, action: () -> Unit) {
      val runAtTick: Long
      val action: () -> Unit

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

      fun copy(runAtTick: Long = this.runAtTick, action: () -> Unit = this.action): jooon.features.other.BeachBaller.ScheduledTask {
         return BeachBaller.ScheduledTask(runAtTick, action)
      }

      override fun toString(): String {
         return "ScheduledTask(runAtTick=${this.runAtTick}, action=${this.action})"
      }

      override fun hashCode(): Int {
         return java.lang.Long.hashCode(this.runAtTick) * 31 + this.action.hashCode()
      }

      override operator fun equals(other: Any?): Boolean {
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

      
      fun getEntries(): EnumEntries<BeachBaller.State> {
         $ENTRIES
      }
   }
}
