package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.CaptureMove;
import chess.engine.move.Move.StandardMove;
import chess.engine.move.MoveTransition;
import chess.engine.player.Player;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Bishop extends Piece {
    private int[] moveDirections = {-9, -7, 7, 9};

    public Bishop(int position, Alliance alliance) {
        super(PieceType.BISHOP, position, alliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for(int moveDirection : moveDirections) {
            int potentialLocation = this.getPiecePosition();
            while(BoardUtils.isValidTileCoordinate(potentialLocation)) {
                if(isFirstColumnExclusion(potentialLocation, moveDirection) ||
                isEightColumnExclusion(potentialLocation, moveDirection)) {
                    break;
                }
                potentialLocation += moveDirection;
                if(BoardUtils.isValidTileCoordinate(potentialLocation)) {
                    Tile destinationTile = board.getTile(potentialLocation);
                    if(!destinationTile.isTileOccupied()) {
                        Move move = new StandardMove(board, this, potentialLocation);
                        Board checkBoard = move.execute();
                        if(!checkBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                            legalMoves.add(move);
                        }
                    } else {
                        Piece pieceAtLocation = destinationTile.getPiece();
                        Alliance alliance = pieceAtLocation.getAlliance();
                        if(alliance != this.getAlliance()) {
                            Move move = new CaptureMove(board, this, potentialLocation, pieceAtLocation);
                            Board checkBoard = move.execute();
                            if(!checkBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                                legalMoves.add(move);
                            }
                        }
                        break;
                    }
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }


    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }
}
