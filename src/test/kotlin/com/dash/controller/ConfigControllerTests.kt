package com.dash.controller

import com.dash.entity.ImportData
import com.dash.repository.TabDataset
import com.dash.repository.WidgetDataset
import com.dash.utils.IntegrationTestsUtils
import io.restassured.RestAssured.defaultParser
import io.restassured.RestAssured.given
import io.restassured.http.Header
import io.restassured.http.Headers
import io.restassured.parsing.Parser
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TabDataset
@WidgetDataset
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigControllerTests {

    @LocalServerPort
    private val port: Int = 0

    private var jwtToken: String? = null

    private val CONFIG_ENDPOINT = "/config/"

    @BeforeAll
    fun testUp() {
        defaultParser = Parser.JSON
        jwtToken = IntegrationTestsUtils.authenticateAdmin(port).accessToken
    }

    @Test
    fun testExportConfig() {
        val exportData = given()
            .port(port)
            .header(Header("Authorization", "Bearer $jwtToken"))
            .`when`()
            .get("${CONFIG_ENDPOINT}export")
            .then().log().all()
            .statusCode(200)
            .log().all()
            .body("$", Matchers.notNullValue())
            .extract().`as`(ImportData::class.java)
        assertEquals(2, exportData.tabs.size)
        assertEquals(1, exportData.widgets.size)

        val exportedWidget = exportData.widgets[0]
        assertEquals(1, exportedWidget.type)
        assertEquals(1, exportedWidget.widgetOrder)
    }

    @Test
    fun testImportConfig() {
        val response = given()
            .multiPart("file", ClassPathResource("./files/dashboardConfigTest.json").file)
            .port(port)
            .headers(Headers(Header("Authorization", "Bearer $jwtToken"), Header("content-type", "multipart/form-data")))
            .`when`()
            .post("${CONFIG_ENDPOINT}import").then().log().all()
            .statusCode(200)
            .extract().`as`(Boolean::class.java)
        assertTrue(response)
    }
}
