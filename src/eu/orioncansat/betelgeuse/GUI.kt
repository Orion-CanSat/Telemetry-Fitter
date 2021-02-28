package eu.orioncansat.betelgeuse

import org.jfree.chart.*
import org.jfree.chart.plot.*
import org.jfree.chart.ui.ApplicationFrame
import org.jfree.data.category.*
import kotlin.math.pow

class GUI (data: DataReader) : ApplicationFrame("Telemetry Fitter") {
    private var _chart : JFreeChart? = null
    private var _chartPanel: ChartPanel? = null
    private var _pointDataset : DefaultCategoryDataset? = null
    private var _minimumValue : Double? = null
    private var _maximumValue : Double? = null
    private var _existingValues = HashMap<Int, Boolean>()

    init {
        this._pointDataset = this.createPointDataset(data)
        this._chart = ChartFactory.createLineChart("Telemetry Fitter", "RSSI", "Distance", this._pointDataset, PlotOrientation.VERTICAL, true, true, false)
        this._chartPanel = ChartPanel(this._chart)
        this._chartPanel!!.preferredSize = java.awt.Dimension(1080, 720)
        this.contentPane = this._chartPanel!!
        this.pack()
        this.isVisible = true
    }

    private fun createPointDataset(data: DataReader) : DefaultCategoryDataset {
        val dataset = DefaultCategoryDataset()

        for (i in 0 until data.GetSize()) {
            if (this._minimumValue == null || data.GetData(i).second < this._minimumValue!!)
                this._minimumValue = data.GetData(i).second
            if (this._maximumValue == null || data.GetData(i).second > this._maximumValue!!)
                this._maximumValue = data.GetData(i).second
        }

        for (i in this._minimumValue!!.toInt() until this._maximumValue!!.toInt()) {
            var sum: Double? = null
            var totalSum = 0
            for (j in 0 until data.GetSize()) {
                if (data.GetData(j).second.toInt() == i) {
                    if (sum == null)
                        sum = 0.0
                    sum += data.GetData(j).first
                    totalSum++
                }
            }
            if (sum != null) {
                dataset.addValue(sum/totalSum, "Data RSSI", i)
            }
            this._existingValues[i] = sum != null
        }

        return dataset
    }

    fun UpdateNewChart(b: Double, n: Double, TxPower: Double) {
        try {
            this._pointDataset?.removeRow("Best R^2")
        }
        catch (e : Exception) { }
        for (i in this._minimumValue!!.toInt() until this._maximumValue!!.toInt()) {
            if (this._existingValues.containsKey(i) && this._existingValues[i] == true)
                this._pointDataset?.addValue(10.0.pow((TxPower - i - b)/(10 * n)), "Best R^2", i)
        }
    }
}