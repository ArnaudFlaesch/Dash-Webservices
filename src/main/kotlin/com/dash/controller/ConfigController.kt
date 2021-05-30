package com.dash.controller

import com.dash.entity.Tab
import com.dash.service.JsonExporter
import com.dash.service.TabService
import com.dash.service.WidgetService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/config")
class ConfigController {

    @Autowired
    private lateinit var jsonExporter: JsonExporter

    @Autowired
    private lateinit var tabService: TabService

    @Autowired
    private lateinit var widgetService: WidgetService

    private val logger = LoggerFactory.getLogger(this::class.java.name)

    @GetMapping("/export")
    fun downloadJsonFile(): ResponseEntity<ByteArray?>? {
        val widgets: List<Any> = widgetService.getAllWidgets()
        val tabs: List<Tab> = tabService.getTabs()
        val customerJsonString: String = jsonExporter.export(widgets.plus(tabs))
        val customerJsonBytes = customerJsonString.toByteArray()
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=dashboardConfig.json")
            .contentType(MediaType.APPLICATION_JSON)
            .contentLength(customerJsonBytes.size.toLong())
            .body(customerJsonBytes)
    }
}
