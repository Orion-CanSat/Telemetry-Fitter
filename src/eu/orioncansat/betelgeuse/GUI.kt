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
    private var _minimumValue : Double = 0.0
    private var _maximumValue : Double = 0.0
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

        for (i in 0 until data.GetSize() step data.GetSize() / 100)
        {
            val inData = data.GetData(i)
            if (inData.second < _minimumValue)
                _minimumValue = inData.second
            if (inData.second > _maximumValue)
                _maximumValue = inData.second
            dataset.addValue(inData.first, "Input RSSI", inData.second.toInt())
        }

        return dataset
    }

    fun UpdateNewChart(b: Double, n: Double, TxPower: Double)
    {
        try {
            this._pointDataset?.removeRow("Best R^2")
        }
        catch (e : Exception) { }
        for (i in this._maximumValue.toInt() downTo this._minimumValue.toInt())
            this._pointDataset?.addValue(10.0.pow((TxPower - i - b)/(10 * n)), "Best R^2", i)
    }
}