package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testFromCodeforces1() {
        assertEquals(3.729935587093555327,
                solver(Point(0.0, 0.0),
                        Point(5.0, 5.0),
                        3.0,
                        2.0,
                        Point(-1.0, -1.0),
                        Point(-1.0, 0.0)), 0.000001)
    }

    @Test
    fun testFromCodeforces2() {
        assertEquals(11.547005383792516398,
                solver(Point(0.0, 0.0),
                        Point(0.0, 1000.0),
                        100.0,
                        1000.0,
                        Point(-50.0, 0.0),
                        Point(50.0, 0.0)), 0.000001)
    }
}