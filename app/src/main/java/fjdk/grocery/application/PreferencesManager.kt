package fjdk.grocery.application

import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME = "FJDK_GROCERY_APP_PREFERENCES"
const val PREFERENCES_LOGGED_IN_NAME = "USER_LOGGED_IN"

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setLoginState(state: Boolean = false) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(PREFERENCES_LOGGED_IN_NAME, state)
        editor.apply()
    }

    fun getLoginState(): Boolean {
        return sharedPreferences.getBoolean(PREFERENCES_LOGGED_IN_NAME, false)
    }
}
