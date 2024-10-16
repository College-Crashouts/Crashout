package org.acjn.crashout

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform