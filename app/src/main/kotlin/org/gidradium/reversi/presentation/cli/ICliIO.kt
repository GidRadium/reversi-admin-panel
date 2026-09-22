package org.gidradium.reversi.presentation.cli

interface ICliIO {
    fun readLine(): String?
    fun write(message: String)
    fun writeLine(message: String = "")
}
