package jooon.config

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.RUNTIME)
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.FIELD])
annotation class Entry(
   val category: String = "",
   val min: Double = 0.0,
   val max: Double = 1.0,
   val isSlider: Boolean = false,
   val isColor: Boolean = false
)
