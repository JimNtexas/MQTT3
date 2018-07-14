package com.example.jim.myapplication

// used to send json to robocar

//@JsonClass(generateAdapter = true)
data class RawCommand (
        var command : String = "",
        var duration : Int = 10,
        var left_speed: Int = 100, //m1 is left motor
        var left_forward: Boolean = true, //one for forward, 0 for backwards
        var right_speed: Int = 100, //m2 is right motor
        var right_forward: Boolean = true)

