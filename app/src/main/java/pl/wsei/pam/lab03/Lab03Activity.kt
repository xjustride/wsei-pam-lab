// File: app/src/main/java/pl/wsei/pam/lab03/Lab03Activity.kt
package pl.wsei.pam.lab03

import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {

    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView
    private var rows: Int = 4
    private var columns: Int = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.mBoard)
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(4, 4)
        rows = size[0]
        columns = size[1]

        mBoard.columnCount = columns
        mBoard.rowCount = rows

        mBoardModel = MemoryBoardView(mBoard, columns, rows)
        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("gameState")
            savedState?.let { mBoardModel.setState(it) }
        }

        mBoardModel.setOnGameChangeListener { event ->
            when (event.state) {
                GameStates.Matching -> {
                    event.tiles.forEach { it.revealed = true }
                }
                GameStates.Match -> {
                    event.tiles.forEach { it.revealed = true }
                }
                GameStates.NoMatch -> {
                    event.tiles.forEach { it.revealed = true }
                    Timer().schedule(2000) {
                        runOnUiThread {
                            event.tiles.forEach { it.revealed = false }
                        }
                    }
                }
                GameStates.Finished -> {
                    Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("gameState", mBoardModel.getState())
    }
}