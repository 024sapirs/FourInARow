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

    public int getEmptyCell(int col) {
        // returns the lowest empty cell in the col
        int emptyRow = 0;
        while (emptyRow+1 < ROWS && board[emptyRow+1][col] == EMPTY) {
            emptyRow += 1;
        }
        return emptyRow;
    }
    public int makeMove(int col) {
        if (!isLegal(col))
            return -1;
        int row = getEmptyCell(col);
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
        // returns winner or Empty
        int seq;
        int prev;
        // checks for a vertical seq
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
        // checks for a horizontal seq
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
        // checks for a diagonal left+up --> right+down seq
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
        // checks for a diagonal right+up --> left+down seq
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
        // checks for empty cells
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
    public ArrayList<Integer> getSafeMoves(int aiPlayer, int humanPlayer) {
        ArrayList<Integer> safeMoves = new ArrayList<>();
        // makes new list of moves without the moves that allow the human to win
        for (int aiMove : getPossibleMoves()) {
            int aiRow = getEmptyCell(aiMove);
            // fake move
            board[aiRow][aiMove] = aiPlayer;
            boolean humanCanWinNextTurn = false;
            for (int humanMove : getPossibleMoves()) {
                if (isWinningMove(humanMove, humanPlayer)) {
                    humanCanWinNextTurn = true;
                }
            }

            // undo fake move
            board[aiRow][aiMove] = EMPTY;

            if (!humanCanWinNextTurn) {
                safeMoves.add(aiMove);
            }
        }

        return safeMoves;
    }
    public int getRandomMove(int aiPlayer, int humanPlayer) {
        ArrayList<Integer> moves = getSafeMoves(aiPlayer, humanPlayer);
        if (moves.isEmpty()) {
            moves = getPossibleMoves();
            if (moves.isEmpty())
                return -1; // No moves left
        }
        Random rand = new Random();
        return moves.get(rand.nextInt(moves.size()));
    }
    private boolean isWinningMove(int col, int player) {
        if (!isLegal(col)) {
            return false;
        }
        int row = getEmptyCell(col);
        // fake move
        board[row][col] = player;
        boolean isWin = checkWin() == player;
        // undo fake move
        board[row][col] = EMPTY;
        return isWin;
    }
    public int getHeuristicMove(int aiPlayer, int humanPlayer) {
        // try to win
        for (int move : getPossibleMoves()) {
            if (isWinningMove(move, aiPlayer)) {
                return move;
            }
        }
        // prevent human from winning
        for (int move : getPossibleMoves()) {
            if (isWinningMove(move, humanPlayer)) {
                return move;
            }
        }
        // choose random move
        return getRandomMove(aiPlayer, humanPlayer);
    }
    public void resetGame() {
        for (int col = 0; col<COLS; col++) 
            for (int row = 0; row<ROWS; row++) 
                board[row][col] = EMPTY;
        currentPlayer = PLAYER_RED;
    }
}