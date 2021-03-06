package com.raritasolutions.mymining.model.converter


/** Apache POI stores colors in a Byte type, which is understandable because color ranges from 0 to 255
 *  However, keeping the color values in the Byte type restricts the range to -127 to 127 due to sign bit
 *  That's why we cast incoming Bytes to Ints to avoid all of that bullshit.
 *  *******
 *  Also, Apache POI Word supplies only String of HEXed RGB color so we want to convert it to Ints too
 */
data class RGBColor constructor(val red: Int, val green: Int, val blue: Int) {
    // Constructor from ByteArray that XSSFColor provides
    constructor(array: ByteArray) : this(array[0].toInt() and 0xFF,
            array[1].toInt() and 0xFF,
            array[2].toInt() and 0xFF)

    constructor(hex: String) : this(Integer.parseInt(hex.substring(0, 2), 16),
            Integer.parseInt(hex.substring(2, 4), 16),
            Integer.parseInt(hex.substring(4, 6), 16))

}