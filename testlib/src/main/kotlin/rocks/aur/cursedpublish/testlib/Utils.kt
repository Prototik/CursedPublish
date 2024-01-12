package rocks.aur.cursedpublish.testlib

import kotlin.reflect.*

operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()