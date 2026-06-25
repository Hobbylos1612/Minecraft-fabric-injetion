package jooon.config

internal sealed interface ConfigNode {
   val fieldName: String

   val category: String?
}
