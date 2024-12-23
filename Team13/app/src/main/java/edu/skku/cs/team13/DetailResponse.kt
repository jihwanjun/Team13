package edu.skku.cs.team13

data class DetailResponse(
    val title: String?,
    val tables: List<Table>
)

data class Table(
    val title: String,
    val table: List<List<Row>>
)

data class Row(
    val title: String,
    val content: String
)
