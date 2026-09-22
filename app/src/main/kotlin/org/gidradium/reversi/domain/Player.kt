package org.gidradium.reversi.domain

data class Player(
    val id: Int,
    val name: String
) {
    init {
        require(name.isNotBlank()) {
            "Player name must not be blank"
        }
    }
}
