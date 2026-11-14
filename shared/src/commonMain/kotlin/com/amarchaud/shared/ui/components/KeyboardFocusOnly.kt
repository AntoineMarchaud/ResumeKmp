package com.amarchaud.shared.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusProperties
import androidx.compose.ui.focus.FocusPropertiesModifierNode
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalInputModeManager

/**
 * Lets the next clickable take the focus from the keyboard only, as on Android.
 *
 * On the web, clickables take the focus on press, even with a finger. Inside a section list, the focused
 * element is then brought into view, and that scroll collapses the hero header: it jumps when a drag starts
 * on a card, and collapses again by itself right after expanding.
 */
fun Modifier.keyboardFocusOnly(): Modifier = this then KeyboardFocusOnlyElement

private data object KeyboardFocusOnlyElement : ModifierNodeElement<KeyboardFocusOnlyNode>() {
    override fun create() = KeyboardFocusOnlyNode()
    override fun update(node: KeyboardFocusOnlyNode) = Unit
}

private class KeyboardFocusOnlyNode :
    Modifier.Node(),
    FocusPropertiesModifierNode,
    CompositionLocalConsumerModifierNode {

    override fun applyFocusProperties(focusProperties: FocusProperties) {
        focusProperties.canFocus = currentValueOf(LocalInputModeManager).inputMode == InputMode.Keyboard
    }
}
