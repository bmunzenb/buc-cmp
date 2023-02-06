package com.munzenberger.buc

data class Record(
    val lastName: String,
    val firstName: String,
    val address: String,
    val cityAndState: String,
    val zip: String,
    val phoneNumbers: String,
    val emails: String,
    val memberStatus: String
)

fun List<CsvRecord>.toBucRecords(): Set<Record> {

    val records = mutableSetOf<Record>()

    forEach { csvRecord ->

        val lastName = csvRecord["Last_Name"]!!.trim()
        val firstName = csvRecord["First_Name"]!!.trim()
        val address = join(csvRecord["Address"], csvRecord["Address2"])
        val cityAndState = join(csvRecord["City"], csvRecord["State"])
        val zip = csvRecord["ZIP"]!!.trim()
        val phoneNumbers = csvRecord["Phone_Numbers"]!!.trim()
        val emails = csvRecord["Emails"]!!.trim()

        // split apart records that contains more than one member
        val fnIndex = firstName.indexOf(" and ")
        if (fnIndex > 0) {
            val firstName1 = firstName.substring(0, fnIndex).trim()
            val firstName2 = firstName.substring(fnIndex + " and ".length).trim()

            // check if each member has a different last name
            var lastName1 = lastName
            var lastName2 = lastName
            val lnIndex = lastName1.indexOf(",")

            if (lnIndex > 0) {
                lastName1 = lastName.substring(0, lnIndex).trim()
                lastName2 = lastName.substring(lnIndex + 1).trim()
            }

            val record1 = Record(
                lastName = lastName1,
                firstName = firstName1,
                address = address,
                cityAndState = cityAndState,
                zip = zip,
                phoneNumbers = phoneNumbers,
                emails = emails,
                memberStatus = "")

            // only add if there isn't already a record with this name
            val match1 = records.firstOrNull { it.fullName == record1.fullName }
            if (match1 == null) {
                records.add(record1)
            }

            val record2 = Record(
                lastName = lastName2,
                firstName = firstName2,
                address = address,
                cityAndState = cityAndState,
                zip = zip,
                phoneNumbers = phoneNumbers,
                emails = emails,
                memberStatus = "")

            val match2 = records.firstOrNull { it.fullName == record2.fullName }
            if (match2 == null) {
                records.add(record2)
            }
        }
        else {
            val record = Record(
                lastName = lastName,
                firstName = firstName,
                address = address,
                cityAndState = cityAndState,
                zip = zip,
                phoneNumbers = phoneNumbers,
                emails = emails,
                memberStatus = "")

            records.add(record)
        }
    }

    return records
}

fun List<CsvRecord>.toIndRecords(): Set<Record> {

    val records = mutableSetOf<Record>()

    forEach { csvRecord ->

        val lastName = csvRecord["Last Name"]!!.trim()
        val firstName = csvRecord["First Name"]!!.trim()
        val address = csvRecord["Address"]!!.trim()
        val cityAndState = csvRecord["City"]!!.trim()
        val zip = csvRecord["Zip Code"]!!.trim()
        val phoneNumbers = join(csvRecord["Home Phone"], csvRecord["Cell Phone"])
        val emails = csvRecord["E-Mail"]!!.trim()
        val memberStatus = csvRecord["Member Status"]!!.trim()

        val record = Record(
            lastName = lastName,
            firstName = firstName,
            address = address,
            cityAndState = cityAndState,
            zip = zip,
            phoneNumbers = phoneNumbers,
            emails = emails,
            memberStatus = memberStatus)

        records.add(record)
    }

    return records
}

/*
val String?.splitCityAndState : Pair<String, String>
    get() = when (this.isNullOrEmpty()) {
        true -> "" to ""
        else -> {
            val i = lastIndexOf(" ")
            if (i > 1)
                substring(0, i) to substring(i+1)
            else
                this to ""
        }
    }
*/

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
