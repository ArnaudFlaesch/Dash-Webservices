package com.dash.controller

import com.dash.utils.IntegrationTestsUtils
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.Header
import io.restassured.parsing.Parser
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProxyControllerTests {

    @LocalServerPort
    private val port: Int = 0

    private var jwtToken: String? = null

    private val PROXY_ENDPOINT = "/proxy/"

    @BeforeAll
    fun testUp() {
        RestAssured.defaultParser = Parser.JSON
        jwtToken = IntegrationTestsUtils.authenticateAdmin(port).accessToken
    }

    @Test
    fun testGetUrl() {
        given()
            .port(port)
            .param("url", "http://thelastpictureshow.over-blog.com/rss")
            .header(Header("Authorization", "Bearer $jwtToken"))
            .`when`()
            .get(PROXY_ENDPOINT)
            .then().log().all()
            .statusCode(200)
            .log().all()
            .body("$", notNullValue())
    }

    @Test
    fun testGetUrlError() {
        given().port(port)
            .param("url", "http://testwrongurl.com")
            .header(Header("Authorization", "Bearer $jwtToken"))
            .`when`()
            .get(PROXY_ENDPOINT)
            .then().log().all()
            .statusCode(500)
            .log().all()
    }

    @Test
    fun testEndpointNotAuthenticated() {
        given().port(port)
            .param("url", "http://testwrongurl.com")
            .`when`()
            .get(PROXY_ENDPOINT)
            .then().log().all()
            .statusCode(401)
            .log().all().body("message", equalTo(""))
    }
}
