package ru.ovm.abin.db.utils

data class Notification<T>(val type: ChangeType, val id: Int, val entity: T) {

    enum class ChangeType { CREATE, UPDATE, DELETE }
}
