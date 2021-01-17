package com.example.minesweeper

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity(){

    private var easy: Boolean = false
    private var medium : Boolean = false
    private var hard : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startGame : Button = findViewById(R.id.startGame);
        val customBoard : Button = findViewById(R.id.customBoard);

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.clearCheck()

        val bestTime = findViewById<TextView>(R.id.textView8)
        val lastTime = findViewById<TextView>(R.id.textView6)

        loadPreferences(bestTime, lastTime)

        startGame.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val button = findViewById<RadioButton>(selectedId)

            val intent = Intent(this, Minesweeper::class.java).apply {
                putExtra("Button",button.text.toString())
            }
            startActivity(intent);
        }
        customBoard.setOnClickListener {
            createCustomBoard();
        }
    }

    private fun loadPreferences(bestTime: TextView, lastGame: TextView){
        val sharedPreferences = getSharedPreferences("SAVED", Context.MODE_PRIVATE)
        val bestGameTime = sharedPreferences.getString("BestGameTime",null)
        val lastGameTime = sharedPreferences.getString("LastGameTime",null)

        if(bestGameTime != null) bestTime.text = "$bestGameTime"
        if(lastGameTime != null) lastGame.text = "$lastGameTime"

    }
    fun onRadioButtonClicked(view :View?){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.easy ->
                    if (checked) {
                        easy = true
                    }
                R.id.medium ->
                    if (checked) {
                        medium = true
                    }
                R.id.hard ->
                    if(checked){
                        hard = true
                    }
            }
        }
    }

    private fun createCustomBoard(){

        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_board,null)
        val rows = dialogView.findViewById<EditText>(R.id.rows)
        val cols = dialogView.findViewById<EditText>(R.id.cols)
        val mines = dialogView.findViewById<EditText>(R.id.mines)

        with(builder){
            setView(dialogView)
            setTitle("Custom Board")
            setPositiveButton("Create"){
                _, _ ->
                val noRows = rows.text.toString()
                val noCols = cols.text.toString()
                val noMines = mines.text.toString()
                if(noRows.isNotBlank() && noCols.isNotBlank() && noMines.isNotBlank()) {
                    val intent = Intent(this@MainActivity, Minesweeper::class.java).apply {
                        putExtra("Custom Board",true)
                        putExtra("Rows", noRows.toInt())
                        putExtra("Cols", noCols.toInt())
                        putExtra("Mines",noMines.toInt())
                    }
                    startActivity(intent);
                }else{
                    Toast.makeText(this@MainActivity,"Fields cannot be left empty!",Toast.LENGTH_LONG).show()
                }
            }
            setNegativeButton("Cancel"){
                _,_ ->
            }
        }
        val alertDialog = builder.create();
        alertDialog.show();
    }
}
