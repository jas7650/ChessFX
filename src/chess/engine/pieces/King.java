package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.CaptureMove;
import chess.engine.move.Move.StandardMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class King extends Piece {
    private int[] moveDirections = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int position, Alliance alliance) {
        super(PieceType.KING, position, alliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for(int moveDirection : moveDirections) {
            if (isFirstColumnExclusion(this.getPiecePosition(), moveDirection) ||
                    isEightColumnExclusion(this.getPiecePosition(), moveDirection)) {
                continue;
            }
            int potentialLocation = this.getPiecePosition() + moveDirection;
            if (BoardUtils.isValidTileCoordinate(potentialLocation)) {
                if (BoardUtils.isValidTileCoordinate(potentialLocation)) {
                    Tile destinationTile = board.getTile(potentialLocation);
                    if (!destinationTile.isTileOccupied()) {
                        legalMoves.add(new StandardMove(board, this, potentialLocation));
                    } else {
                        Piece pieceAtLocation = destinationTile.getPiece();
                        Alliance alliance = pieceAtLocation.getAlliance();
                        if (alliance != this.getAlliance()) {
                            legalMoves.add(new CaptureMove(board, this, potentialLocation, pieceAtLocation));
                        }
                    }
                }
            }
        }
        this.legalMoves = legalMoves;
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }

    @Override
    public King unMovePiece(Move move) {
        return new King(move.getCurrentCoordinate(), move.getMovedPiece().getAlliance());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7
        || candidateOffset == -1);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9
        || candidateOffset == 1);
    }
}