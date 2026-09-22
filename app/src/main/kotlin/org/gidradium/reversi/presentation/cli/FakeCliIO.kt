package org.gidradium.reversi.presentation.cli

class FakeCliIO(
    private val inputs: MutableList<String>
) : ICliIO {

    val outputs = mutableListOf<String>()

    override fun readLine(): String? =
        inputs.removeFirstOrNull()

    override fun write(message: String) {
        outputs += message
    }

    override fun writeLine(message: String) {
        outputs += message
    }
}
