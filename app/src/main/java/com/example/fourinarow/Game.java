package com.example.fourinarow;

import java.util.ArrayList;
import java.util.Random;

public class Game {


    public static final int EMPTY = 0;
    public static final int PLAYER_RED = 1;
    public static final int PLAYER_YELLOW = -1;

    public static final int COLS = 7;
    public static final int ROWS = 6;

    private int[][] board;
    private int currentPlayer;

    public Game() {
        board = new int[ROWS][COLS];
        currentPlayer = PLAYER_RED;
    }

    public int getCurrentPlayer() {

        return currentPlayer;
    }

    public boolean isLegal(int col) {
        return col >= 0 && col < COLS && board[0][col] == EMPTY;
    }

    public int getEmptyRow (int col) {
        int emptyRow = 0;
        while (emptyRow+1 < ROWS && board[emptyRow+1][col] == EMPTY) {
            emptyRow += 1;
        }
        return emptyRow;
    }
    public int makeMove(int col) {
        if (!isLegal(col)) return -1;
        int row = getEmptyRow(col);

        board[row][col] = currentPlayer;
        return row;
    }


    public void changePlayer() {
        if(currentPlayer == PLAYER_RED)
            currentPlayer = PLAYER_YELLOW;
        else
            currentPlayer = PLAYER_RED;
    }

    public int checkWin() {
        // Returns PLAYER_RED, PLAYER_YELLOW, or EMPTY (no winner)
        int seq;
        int prev;
        for (int col = 0; col<COLS; col++) {
            seq=0;
            prev=EMPTY;
            for (int row = 0; row<ROWS; row++) {
                if(board[row][col]==EMPTY)
                    seq = 0;
                else {
                    if(board[row][col]==prev)
                        seq+=1;
                    else
                        seq=1;
                    prev=board[row][col];
                }
                if (seq==4)
                    return prev;
            }
        }

        for (int row = 0; row<ROWS; row++) {
            seq=0;
            prev=EMPTY;
            for (int col = 0; col<COLS; col++) {
                if(board[row][col]==EMPTY)
                    seq = 0;
                else {
                    if(board[row][col]==prev)
                        seq+=1;
                    else
                        seq=1;
                    prev=board[row][col];
                }
                if (seq==4)
                    return prev;
            }
        }

        for (int row = 0; row < ROWS - 3; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                int current = board[row][col];
                if (current != EMPTY &&
                        current == board[row + 1][col + 1] &&
                        current == board[row + 2][col + 2] &&
                        current == board[row + 3][col + 3]) {
                    return current;
                }
            }
        }

        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col < COLS - 3; col++) {
                int current = board[row][col];
                if (current != EMPTY &&
                        current == board[row - 1][col + 1] &&
                        current == board[row - 2][col + 2] &&
                        current == board[row - 3][col + 3]) {
                    return current;
                }
            }
        }

        return EMPTY;
    }

    public boolean isTie() {
        if (checkWin() != EMPTY) {
            return false;
        }

        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<Integer> getPossibleMoves() {
        ArrayList<Integer> moves = new ArrayList<>();

        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == EMPTY) {
                moves.add(col);
            }
        }

        return moves;
    }
    public int getRandomMove() {
        ArrayList<Integer> moves = getPossibleMoves();
        if (moves.isEmpty()) return -1; // No moves left
        Random rand = new Random();
        return moves.get(rand.nextInt(moves.size()));
    }
    public int getHeuristicMove(int aiPlayer, int humanPlayer) {
        //  Win if possible
        for (int move : getPossibleMoves()) {
            int row = getEmptyRow(move);
            board[row][move] = aiPlayer;
            if (checkWin() == aiPlayer) {
                board[row][move] = EMPTY;
                return move;
            }
            board[row][move] = EMPTY;
        }
        //  Block if needed
        for (int move : getPossibleMoves()) {
            int row = getEmptyRow(move);
            board[row][move] = humanPlayer;
            if (checkWin() == humanPlayer) {
                board[row][move] = EMPTY;
                return move;
            }
            board[row][move] = EMPTY;
        }
        return getRandomMove();
        //strategy
    }

    public void resetGame() {
        for (int col = 0; col<COLS; col++) 
            for (int row = 0; row<ROWS; row++) 
                board[row][col] = EMPTY;
        currentPlayer = PLAYER_RED;
    }
}









