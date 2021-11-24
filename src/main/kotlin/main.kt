import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import org.w3c.dom.Document
import java.io.File
import java.io.IOException
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


const val INPUT_FILENAME = "input.txt"
const val OUTPUT_FILENAME = "output.html"

const val backgroundStyle = "background-color: black;"
const val cellStyle = "width: 1px;height: 1px;border: 1px solid blue;"
const val tableStyle = "border-collapse: collapse;"

fun main(args: Array<String>) {
    val fileName = args.firstOrNull() ?: INPUT_FILENAME
    val input = readInput(fileName) ?: return

    val html = if (input.isNotEmpty()) createHTMLTable(input) else createEmptyHTMLDocument()

    writeToFile(html.toHTMLString() ?: "")
}

fun createHTMLTable(input: List<Pair<Int, Int>>): Document {
    val maxRows = input.last().second + 1
    val maxColumns = input.last().first + 1

    var i = 0

    return createHTMLDocument().html {
        body {
            table {
                style = tableStyle
                for (row in 0 until maxRows) {
                    tr {
                        for (column in 0 until maxColumns) {
                            th {
                                style = cellStyle
                                if (row == input[i].second && column == input[i].first) {
                                    style += backgroundStyle
                                    i++
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun writeToFile(content: String): Boolean {
    println("save result to $OUTPUT_FILENAME")
    return try {
        saveToFile(OUTPUT_FILENAME, content)
        true
    } catch (e: IOException) {
        println("can't write to the file")
        false
    }
}

fun readInput(fileName: String): List<Pair<Int, Int>>? {
    println("import data from $fileName")
    return try {
        readFile(fileName).inputMap().sortedWith(compareBy({ it.second }, { it.first }))
    } catch (e: IOException) {
        println("can't read from the file")
        null
    }
}

fun readFile(fileName: String): List<String> = File(fileName).bufferedReader().readLines()
fun saveToFile(fileName: String, context: String) = File(fileName).bufferedWriter().use { out -> out.write(context) }

fun createEmptyHTMLDocument() = createHTMLDocument().html {}
fun Document.toHTMLString(): String? {
    return try {
        val sw = StringWriter()
        val domSource = DOMSource(this)
        TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
            setOutputProperty(OutputKeys.METHOD, "xml")
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            transform(domSource, StreamResult(sw))
        }
        sw.toString()
    } catch (ex: TransformerException) {
        null
    }
}


fun List<String>.inputMap(): MutableList<Pair<Int, Int>> {
    val list: MutableList<Pair<Int, Int>> = mutableListOf()
    forEach {
        val splitStr = it.split(' ')
        if (splitStr.size == 4) try {
            val x1 = splitStr[0].toInt()
            val y1 = splitStr[1].toInt()
            val x2 = splitStr[2].toInt()
            val y2 = splitStr[3].toInt()
            for (x in x1 until x2)
                for (y in y1 until y2)
                    list.add(Pair(x, y))
        } catch (e: NumberFormatException) {
        }
    }
    return list
}