package pl.wsei.pam.lab02

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity
import android.content.Intent


class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab02)

        // Pobranie przycisków i ustawienie wspólnego listenera
        val buttons = listOf(
            findViewById<Button>(R.id.btn_6x6),
            findViewById<Button>(R.id.btn_4x4),
            findViewById<Button>(R.id.btn_4x3),
            findViewById<Button>(R.id.btn_3x2)
        )

        buttons.forEach { button ->
            button.setOnClickListener { view ->
                handleButtonClick(view)
            }
        }
    }

    private fun handleButtonClick(view: View) {
        val tag: String? = view.tag as String?
        val tokens = tag?.split(" ")

        if (tokens != null && tokens.size == 2) {
            val rows = tokens[0].toInt()
            val columns = tokens[1].toInt()

            val intent = Intent(this, Lab03Activity::class.java)
            intent.putExtra("size", intArrayOf(rows, columns)) // Przekazujemy tablicę z rozmiarem
            startActivity(intent)
        } else {
            Toast.makeText(this, "Błąd pobierania rozmiaru planszy", Toast.LENGTH_SHORT).show()
        }
    }

}
