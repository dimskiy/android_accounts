package `in`.windrunner.account_manager.impl

import `in`.windrunner.account_manager.SingleAccountManager
import `in`.windrunner.account_manager.TokenRequestApi
import `in`.windrunner.account_manager.AuthError
import android.accounts.Account
import android.accounts.AccountManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class UsernamePasswordAccountManager(
    private val accountManager: AccountManager,
    private val api: TokenRequestApi.WithUsernamePassword,
    private val accountType: String,
    private val tokenType: String
) : SingleAccountManager {

    override fun getCurrentAccountToken(): String? = getCurrentAccount()?.let {
        accountManager.peekAuthToken(it, tokenType)
    }

    override fun getCurrentAccountName(): String? = getCurrentAccount()?.name

    override suspend fun createAccount(userName: String, password: String) {
        clearAccounts()

        val account = Account(userName, accountType)
        val token = getApiToken(userName, password)

        saveAccount(
            newAccount = account,
            password = password,
            token = token
        )
    }

    override fun clearAccounts() {
        try {
            accountManager.getAccountsByType(accountType)
                .forEach(accountManager::removeAccountExplicitly)

        } catch (e: Throwable) {
            Log.w(SingleAccountManager.LOG_TAG, e)
        }
    }

    override suspend fun refreshCurrentAccountToken() {
        getCurrentAccount()?.let { account ->
            val password = accountManager.getPassword(account)
            val token = getApiToken(account.name, password)
            saveAccount(token = token)
        } ?: throw AuthError.AndroidError("No account found to refresh the token")
    }

    private suspend fun getApiToken(userName: String, password: String): String = try {
        api.getToken(userName, password)
    } catch (e: Throwable) {
        Log.w(SingleAccountManager.LOG_TAG, e)
        throw AuthError.ServerAuthError(e.message)
    }

    private suspend fun saveAccount(
        newAccount: Account? = null,
        password: String? = null,
        token: String
    ) {
        try {
            withContext(Dispatchers.IO) {
                val account = newAccount?.let {
                    accountManager.addAccountExplicitly(it, password, null)
                    it
                } ?: getCurrentAccount()

                accountManager.setAuthToken(account, tokenType, token)
                accountManager.notifyAccountAuthenticated(account)
            }
        } catch (e: Throwable) {
            Log.w(SingleAccountManager.LOG_TAG, e)
            throw AuthError.AndroidError(e.message)
        }
    }

    private fun getCurrentAccount(): Account? = try {
        accountManager.getAccountsByType(accountType).firstOrNull()
    } catch (e: Throwable) {
        Log.w(SingleAccountManager.LOG_TAG, e)
        null
    }
}