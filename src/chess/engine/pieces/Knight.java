package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.CaptureMove;
import chess.engine.move.Move.StandardMove;
import chess.engine.player.Player;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Knight extends Piece {
    private int[] moveDirections = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(int position, Alliance alliance) {
        super(PieceType.KNIGHT, position, alliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : moveDirections) {
            final int candidateDestinationCoordinate = this.getPiecePosition() + currentCandidateOffset;
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if(isFirstColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                        isSecondColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                        isSeventhColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                        isEighthColumnExclusion(this.getPiecePosition(), currentCandidateOffset)) {
                    continue;
                }
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if(!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new StandardMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getAlliance();
                    if(this.getAlliance() != pieceAlliance) {
                        legalMoves.add(new CaptureMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        this.legalMoves = legalMoves;
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 ||
                candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 ||
                candidateOffset == 10 || candidateOffset == 17);
    }
}