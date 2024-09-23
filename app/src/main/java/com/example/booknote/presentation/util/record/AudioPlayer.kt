package com.example.booknote.presentation.util.record

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}