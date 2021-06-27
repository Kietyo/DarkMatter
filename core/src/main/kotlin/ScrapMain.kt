inline fun <reified T> membersOf() = T::class.members

data class Test(val data: Int) {

    companion object : Comparator<Test> {
        val bar = "test"
        override fun compare(o1: Test?, o2: Test?): Int {
            return o1!!.data - o2!!.data
        }
    }
}

fun main(s: Array<String>) {
    println(membersOf<StringBuilder>().joinToString("\n"))
    val test1 = Test(1)
    val test2 = Test(2)
    println(Test.compare(test1, test2))
}
