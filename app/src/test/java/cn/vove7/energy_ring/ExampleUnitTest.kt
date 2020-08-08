package cn.vove7.energy_ring

import cn.vove7.energy_ring.util.inTimeRange
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun timeRangeTest() {

        assertEquals(inTimeRange(1, 0, 3), true)
        assertEquals(inTimeRange(1, 1, 3), true)
        assertEquals(inTimeRange(1, 1, 23), true)
        assertEquals(inTimeRange(0, 1, 5), false)
        assertEquals(inTimeRange(0, 23, 5), true)
        assertEquals(inTimeRange(0, 23, 1), true)
        assertEquals(inTimeRange(0, 1, 1), false)
        assertEquals(inTimeRange(1, 1, 1), false)

        assertEquals(inTimeRange(1, 22, 3), true)
        assertEquals(inTimeRange(2, 22, 3), true)
        assertEquals(inTimeRange(3, 22, 3), false)
        assertEquals(inTimeRange(21, 22, 3), false)

        assertEquals(inTimeRange(3, 2, 10), true)
        assertEquals(inTimeRange(4, 2, 10), true)
        assertEquals(inTimeRange(11, 2, 10), false)
        assertEquals(inTimeRange(23, 2, 10), false)


    }
}
