package eu.orioncansat.betelgeuse

import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate", "RedundantExplicitType", "RemoveExplicitTypeArguments", "FunctionName")
class Train(data: DataReader, onPercentageRun: Int, functionToRun: (Int) -> Unit) {
    private val _bMIN : Double = -100.0
    private val _bMAX : Double = 100.0
    private val _bSTEP : Double = 0.01

    private val _nMIN : Double = 2.0
    private val _nMAX : Double = 2.5
    private val _nSTEP : Double = 0.001

    private var _rowsNumber : Int = 0
    private var _txPower : Double = 0.0
    private var _dataReader: DataReader? = null
    private var _meanObservedValue : Double = 0.0

    private var _onPercentageToRun : Int = 0
    private var _functionToRun : ((Int) -> Unit)? = null

    internal val bNListRSquared : ArrayList<Triple<Double, Double, Double>> = ArrayList()
    private var _bestValue : Triple<Double, Double, Double>? = null

    private fun insertValues(B: Double, N: Double, RSquared: Double) {
        if (this._bestValue == null || this._bestValue!!.third < RSquared)
            this._bestValue = Triple<Double, Double, Double>(B, N, RSquared)
        this.bNListRSquared.add(Triple<Double, Double, Double>(B, N, RSquared))
    }

    fun GetBestValues() : Triple<Double, Double, Double> {
        return this._bestValue!!
    }

    init {
        this._rowsNumber = data.GetSize()
        this._txPower = data.GetTxPower()
        this._dataReader = data

        this._onPercentageToRun = onPercentageRun
        this._functionToRun = functionToRun

        var sum : Double = 0.0
        for (i in 0 until this._rowsNumber)
            sum += data.GetData(i).second

        this._meanObservedValue = sum / this._rowsNumber
    }

    private fun getDistance(B: Double, N: Double, RSSI: Double) : Double {
        return 10.0.pow((this._txPower - RSSI - B)/(10 * N))
    }

    private fun getRSquared(B: Double, N: Double) : Double {
        var totalSumOfSquares : Double = 0.0
        var residualSumOfSquares : Double = 0.0

        for (i in 0 until this._rowsNumber)
        {
            val data = this._dataReader!!.GetData(i)
            totalSumOfSquares += (data.first - this._meanObservedValue).pow(2.0)
            residualSumOfSquares += (data.first - getDistance(B, N, data.second)).pow(2.0)
        }

        return 1.0 - residualSumOfSquares / totalSumOfSquares
    }

    fun Run() {
        var b: Double = this._bMIN

        var percentage = 0
        var iterationsFromLastUpdate = 0
        val maxIterations = (this._bMAX - this._bMIN) * (this._nMAX - this._nMIN) * (0.01 / (this._bSTEP * this._nSTEP))

        while (b <= this._bMAX) {
            var n: Double = this._nMIN

            while (n <= this._nMAX) {
                this.insertValues(b, n, getRSquared(b, n))
                n += this._nSTEP
                if (iterationsFromLastUpdate++ / maxIterations >= this._onPercentageToRun)
                {
                    iterationsFromLastUpdate = 0
                    if (this._functionToRun != null)
                        this._functionToRun?.let { it(percentage) }
                    percentage += this._onPercentageToRun
                }
            }
            b += this._bSTEP
        }
    }
}