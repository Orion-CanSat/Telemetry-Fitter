package eu.orioncansat.betelgeuse

import kotlin.system.exitProcess

var _DataReader : DataReader? = null
var _ShowGUI = false
var _Gui : GUI? = null
var _Train : Train? = null

val ShowUpdate = fun (percentage: Int) {
    println("[${percentage}%]: Done")
    if (_Gui != null)
    {
        _Gui!!.UpdateNewChart(_Train!!.GetBestValues().first, _Train!!.GetBestValues().second, _DataReader!!.GetTxPower())
    }
}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val argumentParseReturnValue = parseArguments(args)
            if (!argumentParseReturnValue.first)
                exitProcess(0)

            if (argumentParseReturnValue.second)
                exitProcess(0)
            if (argumentParseReturnValue.third == null)
            {
                println("An input file is required to continue")
                exitProcess(1)
            }

            _DataReader = DataReader(fileName = argumentParseReturnValue.third)

            _ShowGUI = argumentParseReturnValue.fifth
            if (_ShowGUI)
                _Gui = GUI(_DataReader!!);

            _Train = Train(_DataReader!!, 1, ShowUpdate)
            _Train!!.Run()

            println("Best B:${_Train!!.GetBestValues().first} N:${_Train!!.GetBestValues().second} with R^2:${_Train!!.GetBestValues().third}")
        }
    }
}