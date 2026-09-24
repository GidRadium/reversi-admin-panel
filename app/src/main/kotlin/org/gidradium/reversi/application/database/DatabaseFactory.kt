package org.gidradium.reversi.application.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.sqlite.SQLiteConfig
import java.nio.file.Files
import java.nio.file.Path

object DatabaseFactory {

    private const val DATABASE_DIRECTORY = "data"
    private const val DATABASE_FILE = "reversi.db"

    fun connect(
        databasePath: Path = Path.of(
            DATABASE_DIRECTORY,
            DATABASE_FILE
        )
    ): Database {
        databasePath.parent?.let(Files::createDirectories)

        val sqliteConfig = SQLiteConfig().apply {
            enforceForeignKeys(true)
        }

        return Database.connect(
            url = "jdbc:sqlite:$databasePath",
            driver = "org.sqlite.JDBC",
            setupConnection = { connection ->
                sqliteConfig.apply(connection)
            }
        )
    }

    fun initialize(database: Database) {
        transaction(database) {
            SchemaUtils.create(
                PlayersTable,
                GamesTable,
                MovesTable
            )
        }
    }
}
