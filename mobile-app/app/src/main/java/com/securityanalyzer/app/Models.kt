package com.securityanalyzer.app

enum class Severity(val penalty: Int) { INFO(0), LOW(5), MEDIUM(15), HIGH(25) }

data class Finding(
    val severity: Severity,
    val title: String,
    val detail: String,
    val packageName: String? = null,
)

data class ScanResult(val findings: List<Finding>, val score: Int)

object Scoring {
    fun score(findings: List<Finding>): Int =
        (100 - findings.sumOf { it.severity.penalty }).coerceIn(0, 100)
}
