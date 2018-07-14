package com.example.jim.myapplication

import com.squareup.moshi.Moshi

// wrapers for raw commands
// todo: add unit tests

class CarCommand{

    private fun getJson(raw:RawCommand): String? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(RawCommand::class.java)
        return jsonAdapter.toJson(raw)
    }

    fun goForward(speed:Int=100, duration:Int=10): String? {
        var raw = RawCommand()
        raw.command ="forward"
        raw.left_speed = speed
        raw.right_speed = speed
        raw.duration = duration
        return getJson(raw)
    }

    fun goBackward (speed: Int=100, duration:Int=5): String? {
        var raw = RawCommand()
        raw.command = "backwards"
        raw.left_speed = speed
        raw.left_forward = false
        raw.right_speed = speed
        raw.right_forward = false
        raw.duration = duration
        return getJson(raw)
    }

    fun turnLeft(speed: Int=75, duration:Int=5) : String? {
        var raw = RawCommand()
        raw.command = "left"
        raw.left_speed = 0
        raw.left_forward = false
        raw.right_speed = speed
        raw.right_forward = true
        return getJson(raw)
    }

    fun turnRight(speed: Int=75, duration:Int=5) : String? {
        var raw = RawCommand()
        raw.command = "right"
        raw.left_speed = speed
        raw.left_forward = true
        raw.right_speed = 0
        return getJson(raw)
    }

    fun stop() : String? {
        var raw = RawCommand()
        raw.command = "stop"
        raw.left_speed = 0
        raw.right_speed = 0
        raw.left_forward = true
        raw.right_forward = true
        return getJson(raw)
    }



}