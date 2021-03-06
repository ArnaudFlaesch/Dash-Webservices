package com.dash.service

import com.google.gson.Gson
import org.springframework.stereotype.Service

@Service
class JsonExporter {
    fun export(entities: Any): String {
        val gson = Gson()
        return gson.toJson(entities)
    }
}
