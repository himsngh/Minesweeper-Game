package com.example.minesweeper


import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import java.util.concurrent.TimeUnit
import kotlin.random.Random

enum class STATUS{
    COVERED,
    UNCOVERED,
    FLAGGED,
    MINE
}

data class Cell(var value:Int = 0, var status: STATUS = STATUS.COVERED)

class Board(private val row:Int, private val col:Int, private val mines: Int){

    val boardGame:Array<Array<Cell>> = Array(row){
       Array<Cell>(col){
           Cell(0,STATUS.COVERED)
       }
    }

    init {
        var count = 0
        val minesCount = mines

        while(count < minesCount){
            val randomRow = Random.nextInt(0,row)
            val randomCol = Random.nextInt(0,col)
            boardGame[randomRow][randomCol].status = STATUS.MINE
            boardGame[randomRow][randomCol].value = -1
            count++
        }

        fillCellValue()
    }

    private fun fillCellValue(){

        for(i in boardGame.indices){
            for(j in boardGame[0].indices){
                if(boardGame[i][j].value == -1){
                    continue
                }
                boardGame[i][j].value = fill(i,j)
            }
        }
    }
    private fun fill(row: Int, col: Int): Int{
        var mines = 0
        if(row-1 >=0 && col - 1 >= 0){
           if(boardGame[row-1][col-1].value == -1) mines++
        }
        if(row-1 >=0 && col >= 0){
            if(boardGame[row-1][col].value == -1) mines++
        }
        if(row-1 >=0 && col+1 < this.col){
            if(boardGame[row-1][col+1].value == -1) mines++
        }
        if(row >=0 && col - 1 >= 0){
            if(boardGame[row][col-1].value == -1) mines++
        }
        if(row >=0 && col + 1 < this.col){
            if(boardGame[row][col+1].value == -1) mines++
        }
        if(row+1 <this.row  && col - 1 >= 0){
            if(boardGame[row+1][col-1].value == -1) mines++
        }
        if(row+1 < this.row && col >= 0){
            if(boardGame[row+1][col].value == -1) mines++
        }
        if(row+1 < this.row && col + 1 < this.col){
            if(boardGame[row + 1][col + 1].value == -1) mines++
        }
        return mines
    }

}

class MinesweeperGame(private val context:Context, private val intent: Intent,private val timer: CountDownTimer,
                      private val list:MutableList<Button>, private val board:Board){

    fun start(){
        for(button in list){

            button.setOnClickListener {
                val id = button.id

                val row = id / board.boardGame.size
                val col = id % board.boardGame.size

                val cell = board.boardGame[row][col]

                var win:Boolean = false

                if(cell.status == STATUS.MINE){
                    showAllMines()
                    gameOver(win)
                }
                updateSurroundingCell(row,col)

                if(checkWin()){
                    win = true
                    gameOver(win)
                }
            }

            // For flagging a cell
            button.setOnLongClickListener {
                val id = button.id
                val row = id / board.boardGame.size
                val col = id % board.boardGame.size
                var cell = board.boardGame[row][col]
                cell.status = STATUS.FLAGGED
                button.background = ContextCompat.getDrawable(context,R.mipmap.flags_foreground)

                true
            }
        }
    }

    private fun showAllMines(){

        for(i in board.boardGame.indices){
            for(j in board.boardGame[0].indices){
                if(board.boardGame[i][j].value == -1){
                    val listRow = i * board.boardGame.size +  j
                    list[listRow].background = ContextCompat.getDrawable(context,R.mipmap.mine_foreground)
                }
            }
        }
    }
    private fun updateSurroundingCell(row: Int, col: Int){
        if(row < 0 || row >= board.boardGame.size || col < 0 || col >= board.boardGame[0].size || board.boardGame[row][col].value == -1 ||
            board.boardGame[row][col].status == STATUS.UNCOVERED){
            return
        }
        val listRow = row * board.boardGame.size +  col
        list[listRow].setBackgroundColor(getColor(context,R.color.background))
        board.boardGame[row][col].status = STATUS.UNCOVERED
        list[listRow].text = board.boardGame[row][col].value.toString()
        list[listRow].setTextColor(getColor(context,R.color.colorPrimaryDark))
        list[listRow].isEnabled = false

        if(board.boardGame[row][col].value != 0){
            list[listRow].setTextColor(getColor(context,R.color.red))
            return
        }

        updateSurroundingCell(row+1,col+1)
        updateSurroundingCell(row+1,col)
        updateSurroundingCell(row+1,col-1)
        updateSurroundingCell(row,col+1)
        updateSurroundingCell(row,col-1)
        updateSurroundingCell(row-1,col-1)
        updateSurroundingCell(row-1,col)
        updateSurroundingCell(row-1,col+1)
    }

    private fun checkWin():Boolean{

        board.boardGame.forEach { arrayOfCells ->
            arrayOfCells.forEach {
               cell ->
                if(cell.status == STATUS.COVERED){
                    return false
                }
                if(cell.status == STATUS.FLAGGED && cell.value != -1){
                    return false
                }
            }
        }
        return true
    }

    private fun gameOver(win: Boolean){
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.game_over,null)
        if(!win){
            Toast.makeText(context,"GAME OVER",Toast.LENGTH_LONG).show()
            timer.cancel()
            with(builder) {
                setView(dialogView)
                setTitle("Game Over")
                setMessage("You Lost")
                setPositiveButton("Restart") {
                    _,_->
                        startActivity(context,intent,null)
                    }
                setNegativeButton("Main Menu"){
                    _,_ ->
                    val intent = Intent(context,MainActivity::class.java).apply{

                    }
                    startActivity(context,intent,null)
                }
            }
            val alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            Toast.makeText(context,"Congrats!!! You Won ",Toast.LENGTH_LONG).show()
            timer.onFinish()
            with(builder) {
                setView(dialogView)
                setTitle("Game Over")
                setMessage("Congrats \n YOU WON ")
                setPositiveButton("Restart") {
                        _,_->
                    startActivity(context,intent,null)
                }
                setNegativeButton("Main Menu"){
                        _,_ ->
                    val intent = Intent(context,MainActivity::class.java).apply{

                    }
                    startActivity(context,intent,null)
                }
            }
            val alertDialog = builder.create();
            alertDialog.show();
        }

    }
}