// File: app/src/main/java/pl/wsei/pam/lab03/MemoryBoardView.kt
package pl.wsei.pam.lab03

import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: MutableList<Int> = mutableListOf(
        R.drawable.baseline_rocket_24,
        R.drawable.icon2,    // add additional drawable identifiers as needed
        R.drawable.icon3,
        R.drawable.icon4,
        R.drawable.icon5,
        R.drawable.icon6,
        R.drawable.icon7,
        R.drawable.icon8
    )
    private val deckResource: Int = R.drawable.deck
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = {}
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic((cols * rows) / 2)
    private val state: IntArray = IntArray(cols * rows) { -1 }

    init {
        // prepare shuffled icons
        val neededIcons = (cols * rows) / 2
        val availableIcons = ArrayList(icons)
        availableIcons.shuffle()
        val selectedIcons = availableIcons.take(neededIcons).toMutableList()
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(selectedIcons)
            it.addAll(selectedIcons)
            it.shuffle()
        }

        // generate grid of ImageButtons and assign a Tile to each button
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(deckResource)
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    gridLayout.addView(it)
                }
                // assign a tile by using a resource from shuffledIcons
                val tileResource = shuffledIcons.removeAt(0)
                addTile(btn, tileResource)
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag.toString()] ?: return
        tile.revealed = true
        matchedPair.push(tile)
        val matchResult = logic.process { tile.tileResource }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        if (matchResult != GameStates.Matching) {
            if (matchResult == GameStates.NoMatch) {
                Handler(gridLayout.context.mainLooper).postDelayed({
                    for (t in matchedPair) {
                        t.revealed = false
                    }
                }, 2000)
            }
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }

    fun getState(): IntArray {
        // generate state array: if a tile is revealed, store its tileResource; otherwise, -1
        var index = 0
        for (key in tiles.keys) {
            val tile = tiles[key]
            state[index++] = if (tile?.revealed == true) tile.tileResource else -1
        }
        return state
    }

    fun setState(newState: IntArray) {
        // restore each tile state assuming iteration order is the same
        var index = 0
        for (key in tiles.keys) {
            val tile = tiles[key]
            if (tile != null) {
                tile.revealed = newState[index] != -1
            }
            index++
        }
    }
}