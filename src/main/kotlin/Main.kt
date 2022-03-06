import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import java.util.EmptyStackException
import java.util.Stack

const val DATA_SIZE = 30000
val data = Array(DATA_SIZE) { 0 }
var dataPtr = 0

fun buildBracketMap(program: List<Char>) = buildMap {
    val stack = Stack<Int>()
    val unbalancedBracketException by lazy { Exception("Unbalanced brackets") }
    try {
        program.forEachIndexed { index, char ->
            if (char == '[') {
                stack.push(index)
            } else if (char == ']') {
                val first = stack.pop()
                put(first, index)
                put(index, first)
            }
        }
    } catch (e: EmptyStackException) {
        throw unbalancedBracketException
    }
    if (stack.isNotEmpty()) throw unbalancedBracketException
}

fun Reader.readProgram() = useLines { sequence ->
    sequence.map { string ->
        string.filter { char ->
            "><+-.,[]".contains(char)
        }.toList()
    }.toList().flatten()
}

fun main(args: Array<String>) {
    val fileStream = if (args.size == 1) {
        File(args[0]).inputStream()
    } else return

    val reader = InputStreamReader(fileStream)
    val sysReader = InputStreamReader(System.`in`)

    // read program into memory
    val program = reader.readProgram()

    val bracketMap = buildBracketMap(program)

    var programPtr = 0
    while (programPtr < program.size) {
        when (program[programPtr]) {
            '>' -> dataPtr++
            '<' -> dataPtr--
            '+' -> {
                data[dataPtr]++
                if (dataPtr > 255) data[dataPtr] = 0
            }
            '-' -> {
                data[dataPtr]--
                if (dataPtr < 0) data[dataPtr] = 255
            }
            '.' -> print(data[dataPtr].toChar())
            ',' -> data[dataPtr] = sysReader.read()
            '[' -> if (data[dataPtr] == 0) bracketMap[programPtr]?.let { programPtr = it }
            ']' -> if (data[dataPtr] != 0) bracketMap[programPtr]?.let { programPtr = it }
        }
        programPtr++
    }
}

