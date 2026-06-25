package jooon.config

import com.google.gson.JsonObject

private data class LoadResult(root: JsonObject?, migrated: Boolean) {
   val root: JsonObject?
   val migrated: Boolean

   init {
      this.root = root
      this.migrated = migrated
   }

   public operator fun component1(): JsonObject? {
      return this.root
   }

   public operator fun component2(): Boolean {
      return this.migrated
   }

   fun copy(root: JsonObject? = this.root, migrated: Boolean = this.migrated): LoadResult {
      return LoadResult(root, migrated)
   }

   override fun toString(): String {
      return "LoadResult(root=${this.root}, migrated=${this.migrated})"
   }

   override fun hashCode(): Int {
      return (if (this.root == null) 0 else this.root.hashCode()) * 31 + java.lang.Boolean.hashCode(this.migrated)
   }

   override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is LoadResult && this.root == (other as LoadResult).root && this.migrated == (other as LoadResult).migrated
      }
   }
}
