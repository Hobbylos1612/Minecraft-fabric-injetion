package jooon.config

internal data class ConfigCommentNode(fieldName: String, category: String, centered: Boolean, label: String) : ConfigNode {
   open val fieldName: String
   open val category: String
   val centered: Boolean
   val label: String

   init {
      this.fieldName = fieldName
      this.category = category
      this.centered = centered
      this.label = label
   }

   public operator fun component1(): String {
      return this.fieldName
   }

   public operator fun component2(): String {
      return this.category
   }

   public operator fun component3(): Boolean {
      return this.centered
   }

   public operator fun component4(): String {
      return this.label
   }

   fun copy(fieldName: String = this.fieldName, category: String = this.category, centered: Boolean = this.centered, label: String = this.label): ConfigCommentNode {
      return ConfigCommentNode(fieldName, category, centered, label)
   }

   override fun toString(): String {
      return "ConfigCommentNode(fieldName=${this.fieldName}, category=${this.category}, centered=${this.centered}, label=${this.label})"
   }

   override fun hashCode(): Int {
      return ((this.fieldName.hashCode() * 31 + this.category.hashCode()) * 31 + java.lang.Boolean.hashCode(this.centered)) * 31 + this.label.hashCode()
   }

   override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is ConfigCommentNode
            && this.fieldName == (other as ConfigCommentNode).fieldName
            && this.category == (other as ConfigCommentNode).category
            && this.centered == (other as ConfigCommentNode).centered
            && this.label == (other as ConfigCommentNode).label
         }
   }
}
