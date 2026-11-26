package com.github.jactor.rises.persistence.test

import com.github.jactor.rises.persistence.UsernameForPersistence
import com.github.jactor.rises.shared.whenFalse
import org.junit.platform.launcher.LauncherInterceptor

class UsernameContextTestLauncherInterceptor : LauncherInterceptor {
    companion object {
        @Volatile
        private var initialized = false
    }

    override fun <T> intercept(invocation: LauncherInterceptor.Invocation<T>): T {
        initialized.whenFalse {
            synchronized(this) {
                initialized.whenFalse {
                    useRandomUsernameProviderClient()
                    initialized = true
                }
            }
        }

        return invocation.proceed()
    }

    private fun useRandomUsernameProviderClient() {
        println(">>> UsernameForPersistence.usernameProvider is using RandomUsernameClient <<<")
        val randomUsernameClient = RandomUsernameClient()
        randomUsernameClient.fetchUsernames(total = 100)

        UsernameForPersistence.usernameProvider = randomUsernameClient
    }

    override fun close() {}
}
