package backend.grocery.FJDK.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonParser {
    private val gson = Gson()

    fun parseToMap(jsonString: String): Map<String, Any>? {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            null
        }
    }

    fun parseToObject(jsonString: String, clazz: Class<*>): Any? {
        return try {
            gson.fromJson(jsonString, clazz)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> parseToObjectList(jsonString: String, clazz: Class<T>): List<T>? {
        return try {
            val type = TypeToken.getParameterized(List::class.java, clazz).type
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            null
        }
    }
}
