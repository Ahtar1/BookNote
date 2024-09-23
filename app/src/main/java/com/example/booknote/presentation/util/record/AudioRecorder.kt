package com.example.booknote.presentation.util.record

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun pause()
    fun resume()
    fun stop()
}