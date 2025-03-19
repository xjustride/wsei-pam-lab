package pl.wsei.pam.lab03

import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private var rows = 4
    private var columns = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.mBoard)

        // Pobranie rozmiaru planszy z intencji
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(4, 4)
        rows = size[0]
        columns = size[1]

        // Ustawienie liczby kolumn i wierszy
        mBoard.columnCount = columns
        mBoard.rowCount = rows

        // Generowanie planszy
        generateBoard()
    }

    private fun generateBoard() {
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val btn = ImageButton(this).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(R.drawable.baseline_audiotrack_24) // Brakujący obraz
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    mBoard.addView(it)
                }
            }
        }
    }
}
