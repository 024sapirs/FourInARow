package com.example.fourinarow;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.animation.AccelerateInterpolator;

public class MainActivity extends AppCompatActivity {
    private Game game;
    int aiPlayer = Game.PLAYER_YELLOW;
    int humanPlayer = Game.PLAYER_RED;
    private boolean animationInProgress = false;
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
    public void resetGame() {
        game.resetGame();
        GridLayout piecesGrid = findViewById(R.id.piecesGrid);
        int childCount = piecesGrid.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = piecesGrid.getChildAt(i);
            if (child instanceof ImageView) {
                ImageView piece = (ImageView) child;

                piece.setTranslationY(0f);
                piece.setImageDrawable(null);
            }
        }

        animationInProgress = false;
    }
    public boolean endOfTurn() {
        int winner = game.checkWin();

        if (winner != Game.EMPTY) {
            String msg;

            if (winner == Game.PLAYER_RED) {
                msg = "RED won!";
            } else {
                msg = "YELLOW won!";
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("game over")
                    .setMessage(msg)
                    .setPositiveButton("yay", (d, which) -> resetGame())
                    .create();

            dialog.show();

            Window window = dialog.getWindow();

            if (window != null) {
                window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                window.setDimAmount(0.3f);
            }

            return false;
        } else if (game.isTie()) {
            AlertDialog dialog2 = new AlertDialog.Builder(this)
                    .setTitle("game over")
                    .setMessage("TIE")
                    .setPositiveButton(":(", (dialog, which) -> resetGame())
                    .create();
            dialog2.show();

            Window window = dialog2.getWindow();

            if (window != null) {
                window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                window.setDimAmount(0.3f);
            }

            return false;
        } else {
            game.changePlayer();
            return true;
        }
    }
    private ImageView getPieceCell(int row, int col) {
        GridLayout piecesGrid = findViewById(R.id.piecesGrid);

        int index = row * Game.COLS + col;

        return (ImageView) piecesGrid.getChildAt(index);
    }

    private int getDrawableForPlayer(int player) {
        if (player == Game.PLAYER_RED) {
            return R.drawable.red_circle;
        } else {
            return R.drawable.yellow_circle;
        }
    }


    public void onCellClick(View view) {
        if (animationInProgress) {
            return;
        }

        String tag = view.getTag().toString();

        int col = Character.getNumericValue(tag.charAt(1));

        if (!game.isLegal(col)) {
            return;
        }

        animationInProgress = true;

        int player = game.getCurrentPlayer();
        int row = game.makeMove(col);

        animatePieceDrop(row, col, player, () -> {
            boolean gameContinues = endOfTurn();

            if (!gameContinues) {
                animationInProgress = false;
                return;
            }

            if (game.getCurrentPlayer() == aiPlayer) {
                playAiTurn();
            } else {
                animationInProgress = false;
            }
        });
    }
    private void playAiTurn() {
        int aiMove = game.getHeuristicMove(aiPlayer, humanPlayer);

        if (aiMove == -1) {
            animationInProgress = false;
            return;
        }

        int player = game.getCurrentPlayer();
        int row = game.makeMove(aiMove);

        animatePieceDrop(row, aiMove, player, () -> {
            endOfTurn();
            animationInProgress = false;
        });
    }

    private void animatePieceDrop(int row, int col, int player, Runnable afterAnimation) {
        ImageView piece = getPieceCell(row, col);

        piece.setImageResource(getDrawableForPlayer(player));

        int cellHeight = piece.getHeight();

        if (cellHeight == 0) {
            cellHeight = (int) (50 * getResources().getDisplayMetrics().density);
        }

        float startY = -cellHeight * (row + 1);

        piece.setTranslationY(startY);

        piece.animate()
                .translationY(0f)
                .setDuration(300 + row * 50L)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    piece.setTranslationY(0f);

                    if (afterAnimation != null) {
                        afterAnimation.run();
                    }
                })
                .start();
    }



}
