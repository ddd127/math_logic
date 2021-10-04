package task.b.solve

import commons.io.IO

fun main(args: Array<String>) {
    if (args.size == 2) {
        IO(args[0], args[1])
    } else {
        IO(System.`in`, System.out)
    }.use { io ->
        Solve(io).solve()
    }
}
