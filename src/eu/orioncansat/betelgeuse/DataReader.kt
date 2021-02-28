package eu.orioncansat.betelgeuse

import java.lang.NumberFormatException

/**
 * File format:
 * 1: Tx Power
 * 2: RSSI Strength, Distance
 * 3: RSSI Strength, Distance
 * 4: RSSI Strength, Distance
 * 5: RSSI Strength, Distance
 * 6: RSSI Strength, Distance
 * ..........................
 */
@Suppress("FunctionName")
class DataReader(fileName: String) {
    private var _txPower : Double = 0.0
    private val _allData = ArrayList<Pair<Double, Double>>()

    init {
        val lines = java.io.File(fileName).readLines(Charsets.UTF_8)
        try {
            this._txPower = lines[0].trim().toDouble()
        }
        catch (e: NumberFormatException) {
            throw Exception("File \"${fileName}\" is malformated")
        }

        for (i in 1..(lines.size - 1)) {
            val tokens = lines[i].split(',').toTypedArray()
            if (tokens.size != 2)
                throw Exception("File \"${fileName}\" is malformated")

            for (i in 0..1)
                tokens[i] = tokens[i].trim()

            try {
                this._allData.add(Pair<Double, Double>(tokens[0].toDouble(), tokens[1].toDouble()))
            }
            catch (e: NumberFormatException) {
                throw Exception("File \"${fileName}\" is malformated")
            }
        }
    }

    fun GetSize() : Int {
        return this._allData.size
    }

    fun GetData(index: Int) : Pair<Double, Double> {
        return this._allData[index]
    }

    fun GetTxPower() : Double {
        return this._txPower
    }
}