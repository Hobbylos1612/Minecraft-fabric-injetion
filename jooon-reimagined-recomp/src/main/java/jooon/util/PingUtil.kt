package jooon.util

import java.util.Arrays
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import net.minecraft.util.Util

object PingUtil {
   private const val CONTROL_MAX_LEAD_PING_MS: Int = 500
   private const val CONTROL_MIN_TRUSTED_PING_MS: Int = 80
   private const val PING_REQUEST_INTERVAL_TICKS: Int = 10

   var currentPing: Int
      private set

   var averagePing: Int
      private set

   private val roundTripSamples: IntArray = IntArray(5)
   private var roundTripSampleCount: Int
   private var roundTripSampleIndex: Int
   private var timer: Int

   fun getMc(): MinecraftClient {
return var10000
   }

   fun onInitializeClient() {
      ClientPlayConnectionEvents.JOIN.register({ var0: ClientPlayNetworkHandler, var1: PacketSender, var2: MinecraftClient ->
         currentPing = 0
         averagePing = 0
         roundTripSampleCount = 0
         roundTripSampleIndex = 0
         timer = 0
      })
      ClientTickEvents.END_CLIENT_TICK
         .register(
            { it: MinecraftClient ->
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

   private fun sendPingRequest() {
      if (this.getMc().getNetworkHandler() != null) {

         var10000.sendPacket(QueryPingC2SPacket(Util.getMeasuringTimeMs()) as Packet)
      }
   }

   fun onPongResponse(packet: PingResultS2CPacket) {
      this.getMc()
         .execute(
            { 
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.IllegalStateException: No common supertype for ternary expression
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
            }
         )
      }

   private fun recordRoundTrip(sampleMs: Int) {
      roundTripSamples[roundTripSampleIndex % roundTripSamples.length] = sampleMs

      roundTripSampleCount = Math.min(roundTripSampleCount + 1, roundTripSamples.length)
   }

   fun getLatency(): Int {
      var var1: Int
      try {
         var var5: Int
         run label23@{

            if (var10000 != null) {


               if (var4 != null) {
                  var5 = var4.getLatency()
                  return@label23
               }
            }

            var5 = 0
         }

         var1 = var5
      } catch (var3: Exception) {
         var1 = 0
      }

      return var1
   }

   fun getBestPing(): Int {

      return if (averagePing > 0) averagePing else (if (currentPing > 0) currentPing else (if (latency >= 80) latency else latency))
   }

   fun getDynamicStatsPing(): Int {
      if (roundTripSampleCount <= 0) {
         return this.getBestPing()
      } else {

         sort(var10000)
         return var10000[roundTripSampleCount / 2]
      }
   }

   fun getControlLeadPing(): Int {
      return ((this.getBestPing()).coerceAtLeast(this.getDynamicStatsPing())).coerceIn(80, 500)
   }
}
