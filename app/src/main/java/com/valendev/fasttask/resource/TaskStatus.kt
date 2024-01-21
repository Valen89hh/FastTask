package com.valendev.fasttask.resource

enum class TaskStatus(val state: String) {
    NEW("New"),
    PENDING("Pending"),
    COMPLETED("Completed")
}