package org.gidradium.reversi.presentation.cli

class CliIO : ICliIO {

    override fun readLine(): String? =
        readlnOrNull()

    override fun write(message: String) =
        print(message)

    override fun writeLine(message: String) =
        println(message)
}
