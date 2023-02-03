package com.munzenberger.buc

import java.io.File
import java.lang.StringBuilder

typealias Record = Map<String, String?>
typealias Transformer = Pair<(Record) -> String?, String>

const val LAST_NAME = "Last_Name"
const val FIRST_NAME = "First_Name"
const val ADDRESS = "Address"
const val CITY = "City"
const val STATE = "State"
const val ZIP = "Zip"
const val PHONE_NUMBER = "Phone_Number"
const val EMAIL = "Email"
const val SK_STATUS = "SK_STATUS"

fun main(args: Array<String>) {

    val bucExport = parse(File("/Users/bmunzenb/Desktop/BUC/BUC Directory export 02-03-23.csv"))
    val indiv01aExport = parse(File("/Users/bmunzenb/Desktop/BUC/20220202_01a_INDIV Active Members.csv"))
    val indiv01bExport = parse(File("/Users/bmunzenb/Desktop/BUC/20220202_01b_INDIV Active NON Members.csv"))
    val family02bExport = parse(File("/Users/bmunzenb/Desktop/BUC/20220202_02b_FAMILY_Other Status_Contrib since 20220101.csv"))
    val family03Export = parse(File("/Users/bmunzenb/Desktop/BUC/20230202_03_FAMILY_Stewardship Active Mbr-Non-Pot-Reg.csv"))

    //family02bExport.first().keys.forEach { println(it) }

    val bucKeys = listOf(
        { r : Record -> r["Last_Name"] } to LAST_NAME,
        { r : Record -> r["First_Name"] } to FIRST_NAME,
        { r : Record -> join(r["Address"], r["Address2"]).cleanAddress } to ADDRESS,
        { r : Record -> r["City"] } to CITY,
        { r : Record -> r["State"] } to STATE,
        { r : Record -> r["ZIP"].zip5 } to ZIP,
        { r : Record -> r["Phone_Numbers"] } to PHONE_NUMBER,
        { r : Record -> r["Emails"] } to EMAIL,
        { r : Record -> null } to SK_STATUS
    )

    val bucNormalized = normalize(bucExport, bucKeys)
    //bucNormalized.forEach { r -> println("${r[LAST_NAME]}, ${r[FIRST_NAME]}") }

    val skKeys = listOf(
        { r : Record -> r["Last Name"] } to LAST_NAME,
        { r : Record -> r["First Name"] } to FIRST_NAME,
        { r : Record -> r["Address"].cleanAddress } to ADDRESS,
        { r : Record -> r["City"].splitCityAndState.first } to CITY,
        { r : Record -> r["City"].splitCityAndState.second } to STATE,
        { r : Record -> r["Zip Code"].zip5 } to ZIP,
        { r : Record -> join(r["Home Phone"], r["Cell Phone"]) } to PHONE_NUMBER,
        { r : Record -> r["E-Mail"] } to EMAIL,
        { r : Record -> r["Member Status"] } to SK_STATUS
    )

    val indiv01aNormalized = normalize(indiv01aExport, skKeys)
    //indiv01aNormalized.forEach { r -> println("${r[LAST_NAME]}, ${r[FIRST_NAME]}") }

    val indiv01bNormalized = normalize(indiv01bExport, skKeys)

    println("BUC vs SK Individual Records")
    println("----------------------------")
    compare(bucNormalized, indiv01aNormalized + indiv01bNormalized)

    println()
    println()

    /*
    println("BUC vs SK Family Records")
    println("------------------------")
    val family02bNormalized = normalize(family02bExport, skKeys)
    val family03Normalized = normalize(family03Export, skKeys)

    compare(bucNormalized, family02bNormalized + family03Normalized)

     */
}

val String?.splitCityAndState : Pair<String?, String?>
    get() = when (this.isNullOrEmpty()) {
        true -> null to null
        else -> {
            val i = lastIndexOf(" ")
            if (i > 1)
                substring(0, i) to substring(i+1)
            else
                this to null
        }
    }

fun join(value1: String?, value2: String?, separator: String = ", ") : String {

    if (value1.isNullOrBlank() && value2.isNullOrBlank()) {
        return ""
    }

    if (value1.isNullOrBlank()) {
        return value2 ?: ""
    }

    if (value2.isNullOrBlank()) {
        return value1
    }

    return listOf(value1, value2).joinToString(separator)
}

val String?.zip5 : String?
    get() = when (this) {
        null -> null
        else -> if (length > 5) substring(0, 5) else this
    }

val String?.cleanAddress : String?
    get() = when (this) {
        null -> null
        else -> {
            val sb = StringBuilder()
            forEach { if (it != '.') sb.append(it) }
            sb.toString()
        }
    }

val Record.uniqueId : String
    get() = this[LAST_NAME] + ", " + this[FIRST_NAME]
