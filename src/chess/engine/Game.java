package chess.engine;

import chess.engine.move.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Game {
    private List<Move> movesMade;
    private int numMovesMade;

    public Game() {
        this.movesMade = new ArrayList<>();
        this.numMovesMade = 0;
    }

    public void addMove(Move move) {
        this.movesMade.add(move);
        this.numMovesMade++;
    }

    public int getNumMovesMade() {
        return this.numMovesMade;
    }

    public Move getMove(int index) {
        return this.movesMade.get(index);
    }
}
