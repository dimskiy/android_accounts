package `in`.windrunner.account_manager

sealed class AuthError : Throwable() {

    abstract override val message: String?

    data class ServerAuthError(override val message: String? = null) : AuthError()

    data class AndroidError(override val message: String? = null) : AuthError()

}