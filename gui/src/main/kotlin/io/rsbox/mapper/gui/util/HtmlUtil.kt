package io.rsbox.mapper.gui.util

import io.rsbox.mapper.mapper.asm.Field
import io.rsbox.mapper.mapper.asm.Method

object HtmlUtil {
    fun getId(method: Method): String {
        return "method-" + escapeId(method.name + method.desc)
    }

    fun getId(field: Field): String {
        return "field-" + escapeId(field.name + field.desc)
    }

    private fun escapeId(str: String): String {
        var ret: StringBuilder? = null
        var retEnd = 0
        var i = 0
        val max = str.length
        while (i < max) {
            val c = str[i]
            if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '-' && c != '_' && c != '.') { // use : as an escape identifier
                if (ret == null) ret = StringBuilder(max * 2)
                ret.append(str, retEnd, i)
                ret.append(':')
                for (j in 0..3) {
                    val v = c.toInt() ushr (3 - j) * 4 and 0xf
                    ret.append("0123456789abcdef"[v])
                }
                retEnd = i + 1
            }
            i++
        }
        return if (ret == null) {
            str
        } else {
            ret.append(str, retEnd, str.length)
            ret.toString()
        }
    }

    fun escape(str: String): String {
        var ret: StringBuilder? = null
        var retEnd = 0
        var i = 0
        val max = str.length
        while (i < max) {
            val c = str[i]
            if (c == '<' || c == '>' || c == '&') {
                if (ret == null) ret = StringBuilder(max * 2)
                ret.append(str, retEnd, i)
                if (c == '<') {
                    ret.append("&lt;")
                } else if (c == '>') {
                    ret.append("&gt;")
                } else {
                    ret.append("&amp;")
                }
                retEnd = i + 1
            }
            i++
        }
        return if (ret == null) {
            str
        } else {
            ret.append(str, retEnd, str.length)
            ret.toString()
        }
    }
}
