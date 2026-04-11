package com.example.fourinarow;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Game game;
    int aiPlayer = game.PLAYER_YELLOW;
    int humanPlayer= game.PLAYER_RED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        game = new Game();
    }

    public void resetGame(){
        game.resetGame();
        GridLayout grid = findViewById(R.id.main);
        int childCount = grid.getChildCount();

        System.out.println(childCount);

        for (int i = 0; i < childCount; i++) {
            View child = grid.getChildAt(i);
            if (child instanceof ImageView) {
                ImageView cell = (ImageView) child;
                cell.setImageResource(R.drawable.hollow_square_blue);
            }
        }



    }
    public void endOfTurn(){
        int winner = game.checkWin();
        if (winner != game.EMPTY) {
            String msg;
            if (winner == game.PLAYER_RED) {
                msg = "X won!";
            } else {
                msg = "O won!";
            }

            new AlertDialog.Builder(this)
                    .setTitle("game over")
                    .setMessage(msg)
                    .setPositiveButton("yay", null)
                    .show();
            resetGame();
        } else if (game.isTie()) {
            new AlertDialog.Builder(this)
                    .setTitle("game over")
                    .setMessage("TIE")
                    .setPositiveButton(":(", null) //
                    .show();
            resetGame();
        } else {
            game.changePlayer();
        }
    }


    public void onCellClick(View view) {
        String tag = view.getTag().toString();

        int col = Character.getNumericValue(tag.charAt(1));

        // 1 isEmpty
        if(game.isLegal(col)){
            int row=game.makeMove(col);
            GridLayout grid = findViewById(R.id.main);
            int index = row * game.COLS + col;
            ImageView cell = (ImageView) grid.getChildAt(index);

            if (game.getCurrentPlayer() == game.PLAYER_RED) {
                cell.setImageResource(R.drawable.red_circle);
            } else {
                cell.setImageResource(R.drawable.yellow_circle);
            }
            endOfTurn();

            if (game.getCurrentPlayer() == aiPlayer) {
                int aiMove = game.getHeuristicMove(aiPlayer, humanPlayer);
                row = game.makeMove(aiMove);



                index = row * game.COLS + aiMove;
                ImageView aiCell = (ImageView) grid.getChildAt(index);

                if (aiPlayer == game.PLAYER_RED) {
                    aiCell.setImageResource(R.drawable.red_circle);
                } else {
                    aiCell.setImageResource(R.drawable.yellow_circle);
                }

                endOfTurn();
            }

        }



    }
}