package cn.vove7.energy_ring.util

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * # WeakLazy
 *
 * @author Vove
 * 2020/5/9
 */

fun <T> weakLazy(lock: Any? = null, valueBuilder: () -> T) = WeakLazy(valueBuilder, lock)

class WeakLazy<T>(val valueBuilder: () -> T, lock: Any? = null) {
    private val lock = lock ?: this

    private var weakValue: WeakReference<T>? = null

    //gc times
    var gcCount = -1
        private set

    //may be not init
    val hasValue get() = weakValue?.get() != null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = synchronized(lock) {
        val wv = weakValue
        return wv?.get().let {
            if (it == null) {
                gcCount++
                val v = valueBuilder()
                weakValue = WeakReference(v)
                v
            } else it
        }
    }

    fun clearWeakValue() {
        weakValue?.clear()
    }

}