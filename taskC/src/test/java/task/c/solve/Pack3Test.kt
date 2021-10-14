package task.c.solve

import org.junit.Test

class Pack3Test {

    @Test
    fun test1() = Util.test(
        "pack3/test1_in.txt",
        "pack3/test1_out.txt",
    )

    @Test
    fun test2() = Util.test(
        "pack3/test2_in.txt",
        "pack3/test2_out.txt",
    )
}
