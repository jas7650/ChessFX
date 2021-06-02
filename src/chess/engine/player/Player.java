package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.move.Move;
import chess.engine.move.MoveStatus;
import chess.engine.move.MoveTransition;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;

import java.util.*;

import static chess.engine.move.Move.NULL_MOVE;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final boolean isInCheck;
    protected Collection<Move> playerLegalMoves;
    protected Collection<Move> castleMoves;

    public Player(final Board board, final Collection<Move> playerLegalMoves, final Collection<Move> opponentLegalMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegalMoves).isEmpty();
        this.playerLegalMoves = playerLegalMoves;
        this.castleMoves = calculateCastleMoves(playerLegalMoves, opponentLegalMoves);
        if(!castleMoves.isEmpty()) {
            playerLegalMoves.addAll(castleMoves);
        }
    }

    private King establishKing() {
        for(final Piece piece : getActivePieces()) {
            if(piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board!!");
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return isInCheck && !hasLegalMove();
    }

    public boolean isInStaleMate() {
        return !isInCheck && !hasLegalMove();
    }

    protected boolean hasLegalMove() {
        for(Move move : this.getPlayerLegalMoves()) {
            MoveTransition transition = this.makeMove(move);
            if(transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public Collection<Move> getPlayerLegalMoves() {
        return this.playerLegalMoves;
    }

    public Collection<Move> getCastleMoves() {
        return this.castleMoves;
    }

    public static Collection<Move> calculateAttacksOnTile(int location, Collection<Move> opponentLegalMoves) {
        List<Move> attacksOnTile = new ArrayList<>();
        for(Move move : opponentLegalMoves) {
            if(move.getDestinationCoordinate() == location) {
                attacksOnTile.add(move);
            }
        }
        return Collections.unmodifiableList(attacksOnTile);
    }

    public MoveTransition makeMove(final Move move) {
        if(move == NULL_MOVE) {
            return new MoveTransition(this.board, MoveStatus.ILLEGAL_MOVE);
        } else {
            if (!this.playerLegalMoves.contains(move)) {
                return new MoveTransition(this.board, MoveStatus.ILLEGAL_MOVE);
            }
            final Board transitionBoard = move.execute();
            return transitionBoard.getCurrentPlayer().getOpponent().isInCheck() ?
                    new MoveTransition(this.board, MoveStatus.LEAVES_PLAYER_IN_CHECK) :
                    new MoveTransition(transitionBoard, MoveStatus.DONE);
        }
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public abstract Collection<Move> calculateCastleMoves(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves);
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
}