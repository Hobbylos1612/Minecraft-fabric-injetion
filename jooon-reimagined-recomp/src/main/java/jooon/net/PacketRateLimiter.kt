package jooon.net

import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import net.minecraft.network.packet.Packet
import net.minecraft.network.ClientConnection
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket

object PacketRateLimiter {
   private const val MIN_GAP_MS: Long = 51L
   private var lastSwapMs: Long
   private val allowlist: MutableSet<Packet>
   private val queue: ConcurrentLinkedQueue<jooon.net.PacketRateLimiter.Queued> = ConcurrentLinkedQueue()

   
   fun intercept(connection: ClientConnection, packet: Packet): Boolean {
      if (allowlist.remove(packet)) {
return false
      } else {

         if (packet is UpdateSelectedSlotC2SPacket) {
            lastSwapMs = now
return false
         } else if (packet is PlayerInteractItemC2SPacket && now - lastSwapMs < 51L) {
            queue.add(PacketRateLimiter.Queued(connection, packet, lastSwapMs + 51L))
return true
         } else {
return false
         }
      }
   }

   
   fun onTick() {


      while (true) {
         val var10000: PacketRateLimiter.Queued = queue.peek()
         if (var10000 == null) {
break
         }

         if (now < var10000.deadline) {
break
         }

         queue.poll()
         allowlist.add(var10000.getPacket())
         var10000.getConnection().send(var10000.getPacket())
      }
   }

   
   fun {
      val var10000: java.util.Set = Collections.newSetFromMap(IdentityHashMap())
      allowlist = var10000
   }

   private data class Queued {
      private ClientConnection connection;
      private Packet<?> packet;
      val deadline: Long

      fun Queued(connection: ClientConnection, packet: Packet, deadline: Long) {
         this.connection = connection
         this.packet = packet
         this.deadline = deadline
      }

      fun getConnection(): ClientConnection {
         this.connection
      }

      fun getPacket(): Packet {
         this.packet
      }

      fun component1(): ClientConnection {
         this.connection
      }

      fun component2(): Packet {
         this.packet
      }

      public operator fun component3(): Long {
         return this.deadline
      }

      fun copy(connection: ClientConnection, packet: Packet, deadline: Long): PacketRateLimiter.Queued {
         PacketRateLimiter.Queued(connection, packet, deadline)
      }

      override fun toString(): String {
         return "Queued(connection=${this.connection}, packet=${this.packet}, deadline=${this.deadline})"
      }

      override fun hashCode(): Int {
         return (this.connection.hashCode() * 31 + this.packet.hashCode()) * 31 + java.lang.Long.hashCode(this.deadline)
      }

      override operator fun equals(other: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is PacketRateLimiter.Queued
               && this.connection == (other as PacketRateLimiter.Queued).connection
               && this.packet == (other as PacketRateLimiter.Queued).packet
               && this.deadline == (other as PacketRateLimiter.Queued).deadline
            }
      }
   }
}
