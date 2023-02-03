package com.munzenberger.buc

fun compare(setA: List<Record>, setB: List<Record>) {

    println("BUC Name,SK Member Status,Field,BUC Directory Value,Servant Keeper Value")

    var matched = 0

    var different = 0
    var same = 0

    val unmatchedRecords = mutableListOf<Record>()

    val matchedFamilyIds = mutableListOf<String>()

    setA.forEach { recordA ->

        // find the matching record in setB
        val recordB = setB.findMatching(recordA)

        when (recordB) {
            null -> {
                unmatchedRecords.add(recordA)
                //println("Could not find match for record: $recordA")
            }
            else -> {
                matched++
                val diff = compare(recordA, recordB)

                when (diff.isEmpty()) {
                    true -> {
                        same++
                    }
                    false -> {
                        different++
                        println("${recordA.fullName},${recordB[SK_STATUS]},,,")
                        diff.forEach { (key, value) ->
                            println(",,$key,\"${value.first}\",\"${value.second}\"")
                        }
                    }
                }
            }
        }
    }

    println()
    println("BUC directory records: ${setA.size}")
    println("Servant Keeper records: ${setB.size}")
    println("Matched $matched records, $different records are different, $same records are the same")

    println()
    println("Unmatched records from BUC directory (${unmatchedRecords.size}):")
    println()
    unmatchedRecords.forEachIndexed { _, value -> println("${value[FIRST_NAME]} ${value[LAST_NAME]}") }
}

fun compare(recordA: Record, recordB: Record) : Map<String, Pair<String?, String?>> {

    val map = mutableMapOf<String, Pair<String?, String?>>()

    recordA.forEach { (key, valueA) ->
        when (key) {
            SK_STATUS -> Unit // do not compare
            else -> {
                val valueB = recordB[key]
                if (valueA != valueB) {
                    map[key] = valueA to valueB
                }
            }
        }
    }

    return map
}

fun List<Record>.findMatching(record: Record) : Record? {
/*
    // find all matching the last name
    var match = filter { it[LAST_NAME]?.uppercase() == record[LAST_NAME]?.uppercase() }

    if (match.size > 1) {
        // try by email address
        when (val e = record[EMAIL]) {
            null -> Unit // do nothing
            else -> match = match.filter { it[EMAIL]?.contains(e) ?: false }
        }
    }

    if (match.size > 1) {
        // try by first name
        when (val n = record[FIRST_NAME]) {
            null -> Unit // do nothing
            else -> match = match.filter { it[FIRST_NAME]?.contains(n) ?: false }
        }
    }

    if (match.size > 1) {
        //println("Duplicate matches found: $match")
        match = emptyList()
    }

    return match.firstOrNull()

 */

    val match = filter { it.uniqueId == record.uniqueId }

    if (match.size > 1) {
        throw IllegalStateException("Duplicate records found")
    }

    return match.firstOrNull()
}

val Record.fullName : String
    get() = "${this[FIRST_NAME]} ${this[LAST_NAME]}"