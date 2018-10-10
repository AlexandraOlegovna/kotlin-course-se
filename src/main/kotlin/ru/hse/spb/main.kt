package ru.hse.spb

import java.io.OutputStream
import java.lang.StringBuilder


fun <A, B> Pair<A, B>.format(): String = "$first=$second"

fun printerAdditionalParams(params: Array<out Pair<String, String>>) : String {
    return if (params.isNotEmpty())
        "[${params.joinToString(",") { param -> param.format() }}]"
    else ""
}


enum class Align {
    Right, Left, Center
}


abstract class Element(protected var printer: (String) -> Unit)


fun document(init: DocumentPreamble.() -> Unit): Buffer {
    val sb = StringBuilder()
    DocumentPreamble { sb.append(this) }.init()
    return Buffer(sb)
}

class DocumentPreamble(printer: String.() -> Unit) : Document(printer) {
    fun documentClass(type: String) {
        printer("\\documentclass{$type}\n")
    }

    fun usepackage(packageName: String) {
        printer("\\usepackage{$packageName}\n")
    }

    fun usepackage(packageName: String, vararg packages: String) {
        printer("\\usepackage")
        val additionalPackage =
                if (packages.isNotEmpty())
                    "[${packages.joinToString(",")}]"
                else ""
        printer(additionalPackage)
        printer("{$packageName}\n")
    }

    fun usepackage(packageName: String, vararg params: Pair<String, String>) {
        printer("\\usepackage")
        val additionalPackage = printerAdditionalParams(params)
        printer(additionalPackage)
        printer("{$packageName}\n")
    }

    // new tag to create \begin{document} \end{document}
    fun content(init: Document.() -> Unit) {
        printer("\\begin{document}\n")
        init()
        printer("\\end{document}\n")
    }

}


open class Document(printer: String.() -> Unit) : Element(printer) {

    fun frame(frameTitle: String, vararg params: Pair<String, String>, init: Document.() -> Unit) {
        printer("\\begin{frame}")
        val additional = printerAdditionalParams(params)
        printer(additional + "\n")
        printer("\\frametitle{$frameTitle}\n")
        init()
        printer("\\end{frame}\n")
    }

    fun itemize(init: Itemize.() -> Unit) {
        printer("\\begin{itemize}\n")
        Itemize(printer).init()
        printer("\\end{itemize}\n")
    }

    fun enumerate(init: Itemize.() -> Unit) {
        printer("\\begin{enumerate}\n")
        Itemize(printer).init()
        printer("\\end{enumerate}\n")
    }

    fun align(type: Align, init: Document.() -> Unit) {
        val align = when (type) {
            Align.Left -> printer("flushleft")
            Align.Right -> printer("flushright")
            Align.Center -> printer("center")
        }
        printer("\\begin{$align}")
        init()
        printer("\\end{$align}")
    }

    fun customTag(name: String, vararg params: Pair<String, String>, init: Document.() -> Unit) {
        printer("\\begin{$name}")
        val additional = printerAdditionalParams(params)
        printer(additional + "\n")
        init()
        printer("\\end{$name}\n")
    }

    fun math(formula: String) {
        printer("$$")
        printer(formula)
        printer("$$\n")
    }

    operator fun String.unaryPlus() {
        printer(this.trimMargin() + "\n")
    }
}


@DslMarker
annotation class TexMarker

@TexMarker
class Itemize(printer: String.() -> Unit) : Element(printer) {
    fun item(init: Document.() -> Unit) {
        printer("\\item ")
        Document(printer).init()
    }
}



class Buffer(private val sb: StringBuilder) {
    fun toOutputStream(os: OutputStream) = os.write(toString().toByteArray())
    override fun toString() = sb.toString()
}


fun main(args: Array<String>) {
    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)
        content {
            frame("frametitle", "arg1" to "arg2") {
                itemize {
                    for (row in listOf("a", "b", "c")) {
                        item { +"$row text" }
                    }
                }
            }
            // begin{pyglist}[language=kotlin]...\end{pyglist}
            customTag("pyglist", "language" to "kotlin") {
                +"""
               |val a = 1
               |
            """
            }
        }
    }.toOutputStream(System.out)
}

