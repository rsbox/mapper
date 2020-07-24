package io.rsbox.mapper.gui

import io.rsbox.mapper.mapper.asm.Matchable

data class NodeMatch(val node: Matchable<*>, val score: Double = 0.0, val name: String) {
}