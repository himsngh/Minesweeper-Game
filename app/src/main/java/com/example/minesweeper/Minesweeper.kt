package com.example.minesweeper

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout;
import java.sql.Time
import java.time.Instant
import java.util.*
import kotlin.time.*

class Minesweeper : AppCompatActivity() {

    private val list = mutableListOf<Button>()
    private lateinit var board:Board
    private lateinit var timer : CountDownTimer



    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minesweeper)

        //custom board attributes
        val customBoard = intent.getBooleanExtra("Custom Board",false)
        val noRows = intent.getIntExtra("Rows",0)
        val noCols = intent.getIntExtra("Cols",0)
        val noMines = intent.getIntExtra("Mines",0)



        val level = intent.getStringExtra("Button")

        val noOfMines: EditText = findViewById<EditText>(R.id.editTextNumber)

        if(customBoard){
            setUpGameView(noRows,noCols,noMines)
            noOfMines.setText(noMines.toString())
        }else{
            when(level){
                "Easy" -> {
                    setUpGameView(8,8,10)
                    noOfMines.setText("10")
                }
                "Medium" -> {
                    setUpGameView(10,10,20)
                    noOfMines.setText("20")
                }
                "Hard" -> {
                    setUpGameView(15,15,50)
                    noOfMines.setText("50")
                }
            }
        }
        val intent = intent

        val minesweeperGame = MinesweeperGame(this,intent,timer,list,board)

        minesweeperGame.start()

        val restart = findViewById<Button>(R.id.restart)

        restart.setOnClickListener {
            startActivity(intent)
        }
    }

    private fun setUpGameView(rows: Int,cols: Int,mines: Int){
        board  = Board(rows,cols,mines)
        val gridLayout = findViewById<GridLayout>(R.id.board)
        gridLayout.rowCount = rows
        gridLayout.columnCount = cols

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.weight = 1f
        var count = 0
        for(i in 1..rows){
            for(j in 1..cols){
                val button:Button = Button(this)
                button.id = count++
                list.add(button)
                button.layoutParams = layoutParams
                val layoutParamsG: GridLayout.LayoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)).apply {
                    width = 0
                    height = 0
                }
                gridLayout.addView(button,layoutParamsG)
            }
        }
        startTimer()
    }

    private fun startTimer(){
        val timerText = findViewById<EditText>(R.id.editTextNumber2)

        var seconds: Long = 0

        timer = object : CountDownTimer(2000000000,1000){

            override fun onTick(millisUntilFinished: Long) {
                seconds = (2000000000 / 1000) - millisUntilFinished / 1000
                timerText.setText(seconds.toString())
            }
            override fun onFinish() {

                val sharedPreferences = getSharedPreferences("SAVED",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("LastGameTime",seconds.toString())


                val time = sharedPreferences.getString("BestGameTime",null)
                if(time != null && time.toLong() > seconds){
                    editor.putString("BestGameTime",seconds.toString())
                }
                else if(time == null){
                    editor.putString("BestGameTime",seconds.toString())
                }

                editor.apply()
                editor.commit()
                cancel()
            }
        }
        timer.start()
    }

}