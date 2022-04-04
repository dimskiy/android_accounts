package `in`.windrunner.account_manager

sealed class TokenRequestApi {

    abstract class WithUsernamePassword : TokenRequestApi() {

        abstract suspend fun getToken(userName: String, password: String): String

    }
}