package eu.orioncansat.betelgeuse

import kotlin.system.exitProcess

val ShowUpdate = fun (percentage: Int) {
    println("[${percentage}%]: Done")
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

            val data = DataReader(fileName = argumentParseReturnValue.third)
            val train = Train(data, 1, ShowUpdate)
            train.Run()

            println("Best B:${train.GetBestValues().first} N:${train.GetBestValues().second} with R^2:${train.GetBestValues().third}")
        }
    }
}