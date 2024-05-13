package backend.grocery.FJDK.data

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class FileDataSource(private val tableName: String) {
    var filePath = ""

    init {
        // Check if the file exists, and create it if not
        filePath = "data/$tableName.json"
        val file = File(filePath)
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    fun writeData(data: String) {
        try {
            FileWriter(filePath, false).use { writer ->
                writer.write("$data\n")
            }
        } catch (e: IOException) {
            println("Error writing data to the file: $e")
        }
    }

    fun readData(): String {
        var data = ""
        try {
            BufferedReader(FileReader(filePath)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    data += line
                    line = reader.readLine()
                }
            }
        } catch (e: IOException) {
            println("Error reading data from the file: $e")
        }
        return data
    }
}
