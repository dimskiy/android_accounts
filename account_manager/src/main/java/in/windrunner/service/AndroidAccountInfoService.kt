package `in`.windrunner.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * This service allows to implement simple Android accounts integration allowing.
 * <application>
 *     <service
 *          android:name="`in`.windrunner.service.AndroidAccountInfoService">
 *          <meta-data
 *              android:name="android.accounts.AccountAuthenticator"
 *              android:resource="@xml/authenticator" />
 *     </service>
 *</application>
 *
 * You should also create and setup res/xml/authenticator.xml file following this guide:
 * https://developer.android.com/training/sync-adapters/creating-authenticator#CreateAuthenticatorFile
 * */
class AndroidAccountInfoService : Service() {

    private lateinit var authenticator: InternalAndroidAuthenticator

    override fun onCreate() {
        authenticator = InternalAndroidAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder? = authenticator.iBinder
}