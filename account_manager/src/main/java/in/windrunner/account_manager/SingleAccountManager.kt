package `in`.windrunner.account_manager

import `in`.windrunner.account_manager.impl.UsernamePasswordAccountManager
import android.accounts.AccountManager
import android.content.Context

/* *
 * Account manager supporting only one account of the same type.
 * */
interface SingleAccountManager {

    fun getCurrentAccountToken(): String?

    fun getCurrentAccountName(): String?

    suspend fun createAccount(userName: String, password: String)

    fun clearAccounts()

    suspend fun refreshCurrentAccountToken()


    companion object {
        internal const val LOG_TAG = "SingleAccountManager"

        fun createUserNamePasswordAuth(
            context: Context,
            api: TokenRequestApi.WithUsernamePassword,
            accountType: String,
            tokenType: String
        ): SingleAccountManager = UsernamePasswordAccountManager(
            accountManager = AccountManager.get(context),
            api = api,
            accountType = accountType,
            tokenType = tokenType
        )
    }
}