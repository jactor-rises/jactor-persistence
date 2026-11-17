package com.github.jactor.rises.persistence.test

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.parallel.ResourceLock
import java.util.UUID

@ResourceLock(RandomUsernameExtension.RESOURCE_LOCK)
class RandomUsernameExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {
    companion object {
        const val RESOURCE_LOCK = "random-username-test-state"
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        RandomUsernameProvider.usernameHolder.set(UUID.randomUUID().toString())
    }

    override fun afterTestExecution(context: ExtensionContext) {
        RandomUsernameProvider.usernameHolder.set(null)
    }
}
