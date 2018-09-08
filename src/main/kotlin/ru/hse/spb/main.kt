package ru.hse.spb

import java.lang.Math.*
import java.util.*

data class Point(var x: Double, var y: Double)

operator fun Point.plus(p: Point) = Point(x + p.x, y + p.y)
operator fun Point.minus(p: Point) = Point(x - p.x, y - p.y)
operator fun Point.times(a: Double) = Point(x * a, y * a)

fun distance(p1: Point, p2: Point): Double {
    return sqrt(Math.pow(p1.x - p2.x, 2.0) + Math.pow(p1.y - p2.y, 2.0))
}

fun solver(source: Point, destination: Point, v: Double, t: Double, speedBefore: Point, speedAfter: Point): Double {
    var left = 0.0
    var right = 1000000000.0
    val absoluteDestination = destination - source

    for (i in 0..1000) {
        val middle: Double = (left + right) / 2
        val pos =
                if (middle < t) {
                    speedBefore * middle
                } else {
                    val dif = middle - t
                    speedBefore * t + speedAfter * dif
                }
        if (distance(pos, absoluteDestination) < v * middle) {
            right = middle
        } else {
            left = middle
        }
    }
    return right
}


// http://codeforces.com/problemset/problem/590/B
fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val source = Point(input.nextDouble(), input.nextDouble())
    val destination = Point(input.nextDouble(), input.nextDouble())
    val v = input.nextDouble()
    val t = input.nextDouble()
    val speedBefore = Point(input.nextDouble(), input.nextDouble())
    val speedAfter = Point(input.nextDouble(), input.nextDouble())
    val result = solver(source, destination, v, t, speedBefore, speedAfter)
    println(result)
}