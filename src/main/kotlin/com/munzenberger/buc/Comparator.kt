package com.munzenberger.buc

import java.io.File

private data class Difference(
    val field: String,
    val valueA: String,
    val valueB: String
)

fun compareRecords(bucRecords: Set<Record>, skRecords: Set<Record>, out: File) {

    var matched = 0
    val unmatched = mutableListOf<Record>()
    var same = 0
    var different = 0

    val writer = out.printWriter()

    // CSV file headers
    writer.println("First and Last Name,SK Member Status,Field,BUC Directory Value,ServantKeeper Value")

    bucRecords.forEach { bucRecord ->

        val skRecord = skRecords.match(bucRecord)

        when (skRecord) {
            null -> {
                unmatched.add(bucRecord)
            }
            else -> {
                matched++
                val differences = compare(bucRecord, skRecord)
                if (differences.isNotEmpty()) {
                    different++
                    writer.println("\"${bucRecord.fullName}\",\"${skRecord.memberStatus}\",,,")
                    differences.forEach {
                        writer.println(",,\"${it.field}\",\"${it.valueA}\",\"${it.valueB}\"")
                    }
                }
                else {
                    same++
                }
            }
        }
    }

    writer.flush()
    writer.close()

    println("$matched records from BUC directory found in ServantKeeper (matched by first and last name), ${unmatched.size} records were not found (see list below).")
    println("Found $different records with differences between BUC directory and ServantKeeper (see output CSV), $same records are the same.")
    println()
    println("Records in BUC directory that were not found in ServantKeeper:")
    unmatched.sortedBy { "${it.lastName} ${it.firstName}" }.forEach { println(it.fullName) }
}

private fun Set<Record>.match(record: Record): Record? {

    // match by first and last name
    val matched = filter { it.firstName == record.firstName && it.lastName == record.lastName }

    if (matched.size > 1) {
        println("More than one match for record ${record.firstName} ${record.lastName}.")
        return null
    }

    if (matched.isEmpty()) {
        //println("No match for record ${record.firstName} ${record.lastName}")
        return null
    }

    return matched.first()
}

private fun compare(bucRecord: Record, skRecord: Record): List<Difference> {

    val diff = mutableListOf<Difference>()


    if (bucRecord.address.comparable() != skRecord.address.comparable()) {
        diff.add(Difference("Address", bucRecord.address, skRecord.address))
    }

    if (bucRecord.cityAndState.comparable() != skRecord.cityAndState.comparable()) {
        diff.add(Difference("City and State", bucRecord.cityAndState, skRecord.cityAndState))
    }

    if (bucRecord.zip.truncate(5) != skRecord.zip.truncate(5)) {
        diff.add(Difference("Zip", bucRecord.zip, skRecord.zip))
    }

    if (bucRecord.phoneNumbers != skRecord.phoneNumbers) {
        diff.add(Difference("Phone Numbers", bucRecord.phoneNumbers, skRecord.phoneNumbers))
    }

    if (bucRecord.emails != skRecord.emails) {
        diff.add(Difference("Emails", bucRecord.emails, skRecord.emails))
    }

    return diff
}

val Record.fullName: String
    get() = "$firstName $lastName"

fun String.comparable(): String {
    val sb = StringBuilder()

    var space = false

    this.forEach {
        when (it) {
            ',', '.' -> Unit // ignore
            ' ' -> {
                if (!space) {
                    sb.append(it)
                }
                space = true
            }
            else -> {
                sb.append(it)
                space = false
            }
        }
    }

    return sb.toString().trim()
}

fun String.truncate(length: Int): String {
    return when {
        this.length > length -> substring(0, length)
        else -> this
    }
}