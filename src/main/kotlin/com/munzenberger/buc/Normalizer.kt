package com.munzenberger.buc

fun normalize(records: List<Record>, keyMap: List<Transformer>): List<Record> {

    val results = mutableListOf<Record>()

    records.forEach { record ->

        val map = mutableMapOf<String, String>()
        keyMap.forEach { (transform, target) ->
            map[target] = transform(record)?.trim() ?: ""
        }

        val firstName = map[FIRST_NAME] ?: ""
        val fnIndex = firstName.indexOf(" and ")

        // split records with two first names
        if (fnIndex > 0) {
            val firstName1 = firstName.substring(0, fnIndex).trim()
            val firstName2 = firstName.substring(fnIndex + " and ".length).trim()

            val lastName = map[LAST_NAME] ?: ""
            var lastName1 = lastName
            var lastName2 = lastName
            val lnIndex = lastName1.indexOf(",")

            if (lnIndex > 0) {
                lastName1 = lastName.substring(0, lnIndex).trim()
                lastName2 = lastName.substring(lnIndex + 1).trim()
            }

            map[FIRST_NAME] = firstName1
            map[LAST_NAME] = lastName1

            // don't add if there is already a record with this name
            val duplicate1 = results.filter { it[FIRST_NAME] == firstName1 && it[LAST_NAME] == lastName1 }
            if (duplicate1.isEmpty()) {
                results.add(map)
            }

            val map2 = mutableMapOf<String, String>()
            map.forEach { (key, value) -> map2[key] = value }

            map2[FIRST_NAME] = firstName2
            map2[LAST_NAME] = lastName2

            // don't add if there is already a record with this name
            val duplicate2 = results.filter { it[FIRST_NAME] == firstName2 && it[LAST_NAME] == lastName2 }
            if (duplicate2.isEmpty()) {
                results.add(map2)
            }
        }
        else {
            results.add(map)
        }
    }

    results.sortBy { r -> r[LAST_NAME] + " " + r[FIRST_NAME] }

    return results
}
