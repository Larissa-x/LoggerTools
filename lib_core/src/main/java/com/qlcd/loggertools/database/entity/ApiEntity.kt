package com.qlcd.loggertools.database.entity

import com.blankj.utilcode.util.GsonUtils

/**
 * Created by GaoLuHan on 2022/6/15
 * Describe:
 */
data class ApiEntity(
    val request: RequestEntity = RequestEntity(),
    val response: ResponseEntity = ResponseEntity()
) {
    data class RequestEntity(
        val method: String = "",
        val path: String = "",
    )

    data class ResponseEntity(
        val code: String = ""
    )

    fun parseJson(json: String): ApiEntity {
        return GsonUtils.fromJson(json, ApiEntity::class.java)
    }
}