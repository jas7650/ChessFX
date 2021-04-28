package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.PawnAttackMove;
import chess.engine.move.Move.PawnJump;
import chess.engine.move.Move.PawnMove;
import chess.engine.move.MoveTransition;
import chess.engine.player.Player;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {
    private int[] moveDirections = {7, 8, 9, 16};

    public Pawn(int position, Alliance alliance) {
        super(PieceType.PAWN, position, alliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset : moveDirections){
            final int candidateDestinationCoordinate = this.getPiecePosition() + (this.getAlliance().getDirection() * currentCandidateOffset);
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
            if(currentCandidateOffset == 8 && !candidateDestinationTile.isTileOccupied()) {
                //TODO more work to do here (deal with promotions)
                Move move = new PawnMove(board, this, candidateDestinationCoordinate);
                boolean hasMoved = this.getHasMoved();
                Board transitionBoard = move.execute();
                if(!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                    legalMoves.add(move);
                }
                this.setHasMoved(hasMoved);
            } else if(currentCandidateOffset == 16) {
                if(!this.getHasMoved()) {
                    if ((BoardUtils.SECOND_ROW[this.getPiecePosition()] && this.getAlliance().isBlack()) ||
                            (BoardUtils.SEVENTH_ROW[this.getPiecePosition()] && this.getAlliance().isWhite())) {
                        int behindCandidateDestinationCoordinate = this.getPiecePosition() + (this.getAlliance().getDirection() * 8);
                        Tile behindCandidateDestinationTile = board.getTile(behindCandidateDestinationCoordinate);
                        if (!behindCandidateDestinationTile.isTileOccupied()) {
                            if (!candidateDestinationTile.isTileOccupied()) {
                                Move move = new PawnJump(board, this, candidateDestinationCoordinate);
                                boolean hasMoved = this.getHasMoved();
                                Board transitionBoard = move.execute();
                                if(!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                                    legalMoves.add(move);
                                }
                                this.setHasMoved(hasMoved);
                            }
                        }
                    }
                }
                System.out.println("Piece has moved");
            } else if(currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.getPiecePosition()] && this.getAlliance().isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.getPiecePosition()] && this.getAlliance().isBlack()))){
                if(candidateDestinationTile.isTileOccupied()) {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    if(this.getAlliance() != pieceAtDestination.getAlliance()) {
                        //TODO more to do here (attacking into promotion)
                        Move move = new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination);
                        boolean hasMoved = this.getHasMoved();
                        Board transitionBoard = move.execute();
                        if(!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        this.setHasMoved(hasMoved);
                    }
                }
            } else if(currentCandidateOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.getPiecePosition()] && this.getAlliance().isWhite()) ||
                            (BoardUtils.EIGHTH_COLUMN[this.getPiecePosition()] && this.getAlliance().isBlack()))){
                if(candidateDestinationTile.isTileOccupied()) {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    if(this.getAlliance() != pieceAtDestination.getAlliance()) {
                        //TODO more to do here (attacking into promotion)
                        Move move = new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination);
                        boolean hasMoved = this.getHasMoved();
                        Board transitionBoard = move.execute();
                        if(!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                            legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        this.setHasMoved(hasMoved);
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }
}