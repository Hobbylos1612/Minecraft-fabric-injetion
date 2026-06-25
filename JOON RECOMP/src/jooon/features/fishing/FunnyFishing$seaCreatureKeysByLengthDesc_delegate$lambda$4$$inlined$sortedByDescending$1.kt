package jooon.features.fishing

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((b as java.lang.String).length(), (a as java.lang.String).length())
}