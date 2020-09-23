package com.wang17.myclock.model

class PostArgument {
    var name: String
    var value: String

    constructor(name: String, value: Any) {
        this.name = name
        this.value = value.toString()
    }
}