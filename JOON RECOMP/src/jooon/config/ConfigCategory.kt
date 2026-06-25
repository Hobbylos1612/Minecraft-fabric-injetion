package jooon.config

internal data class ConfigCategory(key: String, displayName: String, items: List<ConfigNode>) {
   public final val key: String
   public final val displayName: String
   public final val items: List<ConfigNode>

   init {
      this.key = key
      this.displayName = displayName
      this.items = items
   }

   public operator fun component1(): String {
      return this.key
   }

   public operator fun component2(): String {
      return this.displayName
   }

   public operator fun component3(): List<ConfigNode> {
      return this.items
   }

   public fun copy(key: String = this.key, displayName: String = this.displayName, items: List<ConfigNode> = this.items): ConfigCategory {
      return ConfigCategory(key, displayName, items)
   }

   public override fun toString(): String {
      return "ConfigCategory(key=${this.key}, displayName=${this.displayName}, items=${this.items})"
   }

   public override fun hashCode(): Int {
      return (this.key.hashCode() * 31 + this.displayName.hashCode()) * 31 + this.items.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is ConfigCategory
            && this.key == (other as ConfigCategory).key
            && this.displayName == (other as ConfigCategory).displayName
            && this.items == (other as ConfigCategory).items
         }
   }
}
