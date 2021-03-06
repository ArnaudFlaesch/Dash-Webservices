package com.dash.utils

import com.dash.security.payload.LoginRequest
import com.dash.security.response.JwtResponse
import io.restassured.RestAssured.given
import org.hamcrest.Matchers

class IntegrationTestsUtils private constructor() {

    companion object {
        @JvmStatic
        fun authenticateAdmin(port: Int): JwtResponse {
            return given()
                .port(port)
                .contentType("application/json")
                .`when`()
                .body(LoginRequest("admintest", "adminpassword"))
                .post("/auth/login/")
                .then().log().all()
                .statusCode(200)
                .log().all()
                .body("$", Matchers.notNullValue())
                .extract().`as`(JwtResponse::class.java)
        }
    }
}
