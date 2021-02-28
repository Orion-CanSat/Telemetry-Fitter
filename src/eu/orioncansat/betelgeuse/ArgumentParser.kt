package eu.orioncansat.betelgeuse

import eu.orioncansat.betelgeuse.utilities.Quintuple

fun printHelp() {
    println("""Welcome to the Orion Telemetry Fitter

+-----------+------------+-----------------------+--------------------------------------------+
| Long Form | Short Form |       Has Value       |                Description                 |
+-----------+------------+-----------------------+--------------------------------------------+
|   help    |    ?, h    |         false         | Prints Help message                        |
+-----------+------------+-----------------------+--------------------------------------------+
|   input   |      i     |          true         | Sets input file                            |
+-----------+------------+-----------------------+--------------------------------------------+
|   force   |      f     | can have (true/false) | If an output file already exists then it   |
|           |            |                       | will re-write it                           |
+-----------+------------+-----------------------+--------------------------------------------+
|    log    |      l     |          true         | Sets log output file                       |
+-----------+------------+-----------------------+--------------------------------------------+
|           |            |                       | If set to true, then a gui will appear with|
|    gui    |      g     | can have (true/false) | a graph containing the best graph with the |
|           |            |                       | best fitted constants                      |
+----=------+------------+-----------------------+--------------------------------------------+
""")
}

fun parseArguments(args: Array<String>) : Quintuple<Boolean, Boolean, String?, String?, Boolean> {
    val argsLen = args.size
    var skipNext = false
    var inputFile : String? = null
    var logFile: String? = null
    var force = false
    var gui = false

    if (argsLen == 0)
    {
        println("Telemetry Fitter requires some arguments. To see available arguments pass /?, /help, -h or --help")
        return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
    }

    for (i in 0..(argsLen - 1)) {
        if (skipNext)
        {
            skipNext = false
            continue
        }

        when (args[i]) {
            "/?", "/help", "-h", "--help" -> {
                if (argsLen != 1)
                {
                    println("\"${args[i]}\" needs to be the only argument")
                    return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
                }
                printHelp()
                return Quintuple(first = true, second = true, third = null, fourth = null, fifth = false)
            }
            "/i", "/input", "-i", "--input" -> {
                if (argsLen < i + 1)
                {
                    println("\"${args[i]}\" needs an extra argument for the value")
                    return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
                }
                else if (inputFile != null)
                {
                    println("\"${args[i]}\" has already been assigned a value")
                    return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
                }
                else if (!java.io.File(args[i + 1]).exists())
                {
                    println("The file \"${args[i + 1]}\" does not exist")
                    return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
                }
                inputFile = args[i + 1]
                skipNext = true
            }
            "/l", "/log", "-l", "--log" -> {
                if (argsLen < i + 1)
                {
                    println("\"${args[i]}\" needs an extra argument for the value")
                    return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
                }
                else if (logFile != null)
                {
                    println("\"${args[i]}\" has already been assigned a value")
                    return Quintuple(false, second = false, third = null, fourth = null, fifth = false)
                }
                logFile = args[i + 1]
                skipNext = true
            }
            "/f", "/force", "-f", "--force" -> {
                if ((i + 1 < argsLen) || (listOf("true", "false").contains(args[i + 1].toLowerCase())))
                {
                    force = args[i + 1].toLowerCase() == "true"
                    skipNext = true
                }
                else
                    force = true
            }
            "/g", "/gui", "-g", "--gui" -> {
                if ((i + 1 < argsLen) || (listOf("true", "false").contains(args[i + 1].toLowerCase())))
                {
                    gui = args[i + 1].toLowerCase() == "true"
                    skipNext = true
                }
                else
                    gui = true
            }
            else -> {
                println("\"${args[i]}\" is not recognized as a valid argument")
                return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
            }
        }
    }

    if (logFile != null && java.io.File(logFile).exists() && !force)
    {
        println("Can not use the \"${logFile}\" as a log file, sine a file with that name already exists")
        return Quintuple(first = false, second = false, third = null, fourth = null, fifth = false)
    }

    return Quintuple(first = true, second = false, third = inputFile, fourth = logFile, fifth = gui)
}