package org.gidradium.reversi.application.database

import org.jetbrains.exposed.v1.jdbc.Database
import java.nio.file.Files
import java.nio.file.Path

object DatabaseFactory {

    private const val DATABASE_DIRECTORY = "data"
    private const val DATABASE_FILE = "reversi.db"

    fun connect(): Database {
        val databaseDirectory = Path.of(DATABASE_DIRECTORY)
        Files.createDirectories(databaseDirectory)

        return Database.connect(
            url = "jdbc:sqlite:${databaseDirectory.resolve(DATABASE_FILE)}",
            driver = "org.sqlite.JDBC"
        )
    }
}
