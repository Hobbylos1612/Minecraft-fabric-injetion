package jooon.config

internal sealed interface ConfigNode {
   public val fieldName: String

   public val category: String?
}
