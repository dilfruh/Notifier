package com.dil.notifier

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NotificationDataUnitTest {
    @Test
    fun twoEqualWork() {
        assertEquals(NotificationData("a", "b", true) { val x = 2 }, NotificationData("a", "b", true) { val x = 2 })
    }
    // TODO Function equality and reference vs structural equality
    @Test
    fun namesNotEqual() {
        assertNotEquals(NotificationData("a", "b", true) { val x = 2 }, NotificationData("c", "b", true) { val x = 2 })
    }
    @Test
    fun vibrationsNotEqual() {
        assertNotEquals(NotificationData("a", "b", true) { val x = 2 }, NotificationData("a", "c", true) { val x = 2 })
    }
    @Test
    fun edgesNotEqual() {
        assertNotEquals(NotificationData("a", "b", true) { val x = 2 }, NotificationData("a", "b", false) { val x = 2 })
    }

    @Test
    fun functionsNotEqual() {
        assertNotEquals(NotificationData("a", "b", true) { val x = 2 }, NotificationData("a", "b", true) { val x = 3 })
    }

    @Test
    fun everythingNotEqual() {
        assertNotEquals(NotificationData("a", "b", true) { val x = 2 }, NotificationData("c", "c", false) { val x = 3 })
    }
}