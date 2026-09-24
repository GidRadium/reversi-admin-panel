package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.GameRecord
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.database.GameSnapshotCodec
import org.gidradium.reversi.application.database.GamesTable
import org.gidradium.reversi.application.database.MovesTable
import org.gidradium.reversi.game.GameSnapshot
import org.gidradium.reversi.game.Move
import org.gidradium.reversi.game.PlayerColor
import org.gidradium.reversi.game.Position
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class ExposedGameRepository(
    private val database: Database
) : IGameRepository {

    override fun create(
        whitePlayerId: PlayerId,
        blackPlayerId: PlayerId,
        snapshot: GameSnapshot
    ): GameId {
        val gameId = transaction(database) {
            val id = GamesTable
                .insert {
                    it[GamesTable.whitePlayerId] = whitePlayerId.value
                    it[GamesTable.blackPlayerId] = blackPlayerId.value
                    it[GamesTable.board] =
                        GameSnapshotCodec.encodeBoard(snapshot.board)
                    it[GamesTable.currentPlayer] = snapshot.currentPlayer
                    it[GamesTable.status] = snapshot.status
                    it[GamesTable.winner] = snapshot.winner
                }[GamesTable.id]

            saveMoves(
                gameId = id,
                moves = snapshot.history
            )

            id
        }

        return GameId(gameId)
    }

    override fun update(
        gameId: GameId,
        snapshot: GameSnapshot
    ) {
        transaction(database) {
            val updatedRows = GamesTable.update(
                where = {
                    GamesTable.id eq gameId.value
                }
            ) {
                it[GamesTable.board] =
                    GameSnapshotCodec.encodeBoard(snapshot.board)
                it[GamesTable.currentPlayer] = snapshot.currentPlayer
                it[GamesTable.status] = snapshot.status
                it[GamesTable.winner] = snapshot.winner
            }

            require(updatedRows == 1) {
                "Game with id ${gameId.value} does not exist"
            }

            MovesTable.deleteWhere {
                MovesTable.gameId eq gameId.value
            }

            saveMoves(
                gameId = gameId.value,
                moves = snapshot.history
            )
        }
    }

    override fun findById(gameId: GameId): GameRecord? =
        transaction(database) {
            val gameRow = GamesTable
                .selectAll()
                .where {
                    GamesTable.id eq gameId.value
                }
                .singleOrNull()
                ?: return@transaction null

            gameRow.toGameRecord(
                history = loadMoves(gameId.value)
            )
        }

    override fun findAll(): List<GameRecord> =
        transaction(database) {
            val rows = GamesTable
                .selectAll()
                .toList()
                .sortedBy { it[GamesTable.id] }

            rows.map { row ->
                val gameId = row[GamesTable.id]

                row.toGameRecord(
                    history = loadMoves(gameId)
                )
            }
        }

    override fun delete(gameId: GameId) {
        transaction(database) {
            val deletedRows = GamesTable.deleteWhere {
                GamesTable.id eq gameId.value
            }

            require(deletedRows == 1) {
                "Game with id ${gameId.value} does not exist"
            }
        }
    }

    private fun saveMoves(
        gameId: Int,
        moves: List<Move>
    ) {
        moves.forEachIndexed { index, move ->
            MovesTable.insert {
                it[MovesTable.gameId] = gameId
                it[MovesTable.moveNumber] = index + 1
                it[MovesTable.row] = move.position.row
                it[MovesTable.column] = move.position.column
                it[MovesTable.player] = move.player
            }
        }
    }

    private fun loadMoves(gameId: Int): List<Move> =
        MovesTable
            .selectAll()
            .where {
                MovesTable.gameId eq gameId
            }
            .toList()
            .sortedBy { it[MovesTable.moveNumber] }
            .map {
                Move(
                    position = Position(
                        row = it[MovesTable.row],
                        column = it[MovesTable.column]
                    ),
                    player = it[MovesTable.player]
                )
            }

    private fun org.jetbrains.exposed.v1.core.ResultRow.toGameRecord(
        history: List<Move>
    ): GameRecord {
        val gameId = this[GamesTable.id]

        return GameRecord(
            id = GameId(gameId),
            whitePlayerId = PlayerId(
                this[GamesTable.whitePlayerId]
            ),
            blackPlayerId = PlayerId(
                this[GamesTable.blackPlayerId]
            ),
            snapshot = GameSnapshot(
                board = GameSnapshotCodec.decodeBoard(
                    this[GamesTable.board]
                ),
                currentPlayer = this[GamesTable.currentPlayer],
                history = history,
                status = this[GamesTable.status],
                winner = this[GamesTable.winner]
            )
        )
    }
}
