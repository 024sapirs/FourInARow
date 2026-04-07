package com.example.fourinarow;

import java.util.ArrayList;
import java.util.Random;

public class Game {


    public static final int EMPTY = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = -1;

    private int[][] board;
    private int currentPlayer;

    public Game() {
        board = new int[3][3];
        currentPlayer = PLAYER_X;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isLegal(int row, int col) {
        return board[row][col] == EMPTY;
    }

    public boolean makeMove(int row, int col) {
        if (!isLegal(row, col)) return false;
        board[row][col] = currentPlayer;
        return true;
    }

    public void changePlayer() {
        currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    public int checkWin() {
        // Returns PLAYER_X, PLAYER_O, or EMPTY (no winner)
        // Implement row, col, diagonal checks
        // (see below for example)
        for(int i=0; i<3; i++) {
            if (board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] != EMPTY) {
                return board[i][0];
            }
            if (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] != EMPTY) {
                return board[0][i];
            }
        }
        if(board[0][0]==board[1][1] && board[0][0]==board[2][2]&&board[0][0]!=EMPTY){
            return board[0][0];
        }
        if(board[0][2]==board[1][1] && board[0][2]==board[2][0]&&board[0][2]!=EMPTY){
            return board[0][2];
        }
        return EMPTY;
    }

    public boolean isTie() {
        // If all cells filled and no winner
        for (int i=0; i<3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j]==EMPTY) {
                    return false;
                }
            }
        }

        return checkWin()==EMPTY;
    }
    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    }
    public Move getRandomMove() {
        ArrayList<Move> moves = getPossibleMoves();
        if (moves.isEmpty()) return null; // No moves left
        Random rand = new Random();
        return moves.get(rand.nextInt(moves.size()));
    }
    public Move getHeuristicMove(int aiPlayer, int humanPlayer) {
        // 1. Win if possible
        for (Move move : getPossibleMoves()) {
            board[move.row][move.col] = aiPlayer;
            if (checkWin() == aiPlayer) {
                board[move.row][move.col] = EMPTY;
                return move;
            }
            board[move.row][move.col] = EMPTY;
        }
        // 2. Block if needed
        for (Move move : getPossibleMoves()) {
            board[move.row][move.col] = humanPlayer;
            if (checkWin() == humanPlayer) {
                board[move.row][move.col] = EMPTY;
                return move;
            }
            board[move.row][move.col] = EMPTY;
        }
        // 3. Take center
        if (board[1][1] == EMPTY)
            return new Move(1, 1);

        // 4. Take a corner
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] c : corners)
            if (board[c[0]][c[1]] == EMPTY)
                return new Move(c[0], c[1]);

        // 5. Otherwise, pick random
        return getRandomMove();
    }

    public void resetGame() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = EMPTY;
        currentPlayer = PLAYER_X;
    }
}








}
