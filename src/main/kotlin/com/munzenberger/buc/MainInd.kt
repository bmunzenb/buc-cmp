package com.munzenberger.buc

import java.io.File

fun main(args: Array<String>) {

    val bucExport = parse(File("/Users/bmunzenb/Desktop/BUC/BUC Directory export 02-03-23.csv"))
    val indiv01aExport = parse(File("/Users/bmunzenb/Desktop/BUC/20220202_01a_INDIV Active Members.csv"))
    val indiv01bExport = parse(File("/Users/bmunzenb/Desktop/BUC/20220202_01b_INDIV Active NON Members.csv"))
    //val family02bExport = parse(File("/Users/bmunzenb/Desktop/BUC/20220202_02b_FAMILY_Other Status_Contrib since 20220101.csv"))
    //val family03Export = parse(File("/Users/bmunzenb/Desktop/BUC/20230202_03_FAMILY_Stewardship Active Mbr-Non-Pot-Reg.csv"))

    val bucRecords = bucExport.toBucRecords()
    println("Found ${bucRecords.size} unique records in BUC directory (after splitting combined records).")

    val indRecords = indiv01aExport.toIndRecords() + indiv01bExport.toIndRecords()
    println("Found ${indRecords.size} individual member and non-member records in ServantKeeper.")

    val output = File("/Users/bmunzenb/Desktop/ind_output.csv")
    compareRecords(bucRecords, indRecords, output)
}
