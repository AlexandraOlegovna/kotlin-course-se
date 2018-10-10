package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun simpleTest() {
        val test = document {
            content {}
        }.toString().trimIndent()
        val result = """
            \begin{document}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun enumerateTest() {
        val test = document {
            content {
                enumerate {
                    item { +"test0" }
                    item { +"test1" }
                }
            }
        }.toString().trimIndent()
        val result = """
            \begin{document}
            \begin{enumerate}
            \item test0
            \item test1
            \end{enumerate}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun itemizeTest() {
        val test = document {
            content {
                itemize {
                    item { math("1+2") }
                    item { +"test1" }
                }
            }
        }.toString().trimIndent()
        val result = """
            \begin{document}
            \begin{itemize}
            \item $$1+2$$
            \item test1
            \end{itemize}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun usepackageTest() {
        val test = document {
            usepackage("test0")
            usepackage("test0", "test1")
            usepackage("test0", "test1", "test2", "test3")
            usepackage("test0", "test1" to "test2", "test3" to "test4")
            }.toString().trimIndent()
        val result = """
            \usepackage{test0}
            \usepackage[test1]{test0}
            \usepackage[test1,test2,test3]{test0}
            \usepackage[test1=test2,test3=test4]{test0}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun frameTest() {
        val test = document {
            content {
                frame("title1"){
                    +"test0"
                    frame("title2", "arg1" to "arg2"){}
                }
            }
        }.toString().trimIndent()
        val result = """
            \begin{document}
            \begin{frame}
            \frametitle{title1}
            test0
            \begin{frame}[arg1=arg2]
            \frametitle{title2}
            \end{frame}
            \end{frame}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun customTagTest() {
        val test = document {
            content {
                customTag("title1", "arg1" to "arg2"){
                    +"test0"
                    customTag("title2"){}
                }
            }
        }.toString().trimIndent()
        val result = """
            \begin{document}
            \begin{title1}[arg1=arg2]
            test0
            \begin{title2}
            \end{title2}
            \end{title1}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun mathTest() {
        val test = document {
            content {
                math("1+2")
                itemize {
                    item { math("3+4") }
                }
            }
        }.toString().trimIndent()
        val result = """
            \begin{document}
            $$1+2$$
            \begin{itemize}
            \item $$3+4$$
            \end{itemize}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun alignTest() {
        val test = document {
            content {
                align(Align.Center) {
                    +"test0"
                }
                align(Align.Left) {
                    +"test1"
                }
                align(Align.Right) {
                    +"test2"
                }
            }
        }.toString().trimIndent()
        val result = """
            \begin{document}
            \begin{center}
            test0
            \end{center}
            \begin{flushleft}
            test1
            \end{flushleft}
            \begin{flushright}
            test2
            \end{flushright}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }

    @Test
    fun complexTest() {
        val test =
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
                }.toString().trimIndent()
        val result = """
            \documentclass{beamer}
            \usepackage[russian]{babel}
            \begin{document}
            \begin{frame}[arg1=arg2]
            \frametitle{frametitle}
            \begin{itemize}
            \item a text
            \item b text
            \item c text
            \end{itemize}
            \end{frame}
            \begin{pyglist}[language=kotlin]
            val a = 1

            \end{pyglist}
            \end{document}
        """.trimIndent()
        assertEquals(result, test)
    }
}