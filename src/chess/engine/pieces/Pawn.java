package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static chess.engine.move.Move.NULL_MOVE;

public class Pawn extends Piece {
    private int[] moveDirections = {7, 8, 9, 16};

    public Pawn(int position, Alliance alliance) {
        super(PieceType.PAWN, position, alliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset : moveDirections){
            if(isFirstColumnExclusion(this.getPiecePosition(), currentCandidateOffset) ||
                    isEightColumnExclusion(this.getPiecePosition(), currentCandidateOffset)) {
                continue;
            }
            final int candidateDestinationCoordinate = this.getPiecePosition() + (this.getAlliance().getDirection() * currentCandidateOffset);
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
            if(currentCandidateOffset == 8) {
                if(!candidateDestinationTile.isTileOccupied()) {
                    if((BoardUtils.EIGHTH_ROW[candidateDestinationCoordinate] && this.getAlliance() == Alliance.BLACK) ||
                            (BoardUtils.FIRST_ROW[candidateDestinationCoordinate] && this.getAlliance() == Alliance.WHITE)) {
                        legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.KNIGHT));
                        legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.QUEEN));
                        legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.BISHOP));
                        legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.ROOK));
                    } else {
                        Move move = new PawnMove(board, this, candidateDestinationCoordinate);
                        boolean hasMoved = this.getHasMoved();
                        Board transitionBoard = move.execute();
                        if (!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                            legalMoves.add(move);
                        }
                        this.setHasMoved(hasMoved);
                    }
                }
                continue;
            }
            if(currentCandidateOffset == 16) {
                if(this.getHasMoved()) {
                    continue;
                }
                if (!((BoardUtils.SECOND_ROW[this.getPiecePosition()] && this.getAlliance().isBlack()) ||
                        (BoardUtils.SEVENTH_ROW[this.getPiecePosition()] && this.getAlliance().isWhite()))) {
                    continue;
                }
                int behindCandidateDestinationCoordinate = this.getPiecePosition() + (this.getAlliance().getDirection() * 8);
                Tile behindCandidateDestinationTile = board.getTile(behindCandidateDestinationCoordinate);
                if (behindCandidateDestinationTile.isTileOccupied()) {
                    continue;
                }
                if (!candidateDestinationTile.isTileOccupied()) {
                    Move move = new PawnJump(board, this, candidateDestinationCoordinate);
                    boolean hasMoved = this.getHasMoved();
                    Board transitionBoard = move.execute();
                    if(!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                        legalMoves.add(move);
                    }
                    this.setHasMoved(hasMoved);
                }
                continue;
            }
            if(currentCandidateOffset == 7) {
                if(candidateDestinationTile.isTileOccupied()) {
                    Collection<Move> attackPromotions = addAttackPromotions(board, candidateDestinationCoordinate);
                    if(attackPromotions.isEmpty()) {
                        Move move = calculatePawnAttackMoves(board, candidateDestinationCoordinate);
                        if(move != NULL_MOVE) {
                            legalMoves.add(move);
                        }
                        continue;
                    } else {
                        legalMoves.addAll(attackPromotions);
                        continue;
                    }
                }
                if(!candidateDestinationTile.isTileOccupied()){
                    if(this.getAlliance() == Alliance.BLACK) {
                        Move move = calculateBlackEnPassantMoves(board, candidateDestinationCoordinate, -1);
                        if(move != NULL_MOVE) {
                            legalMoves.add(move);
                        }
                        continue;
                    }
                    if(this.getAlliance() == Alliance.WHITE){
                        Move move = calculateWhiteEnPassantMoves(board, candidateDestinationCoordinate, 1);
                        if(move != NULL_MOVE) {
                            legalMoves.add(move);
                        }
                        continue;
                    }
                }
            }
            if(currentCandidateOffset == 9) {
                if(candidateDestinationTile.isTileOccupied()) {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    if (this.getAlliance() != pieceAtDestination.getAlliance()) {
                        Collection<Move> attackPromotions = addAttackPromotions(board, candidateDestinationCoordinate);
                        if(attackPromotions.isEmpty()) {
                            Move move = calculatePawnAttackMoves(board, candidateDestinationCoordinate);
                            if(move != NULL_MOVE) {
                                legalMoves.add(move);
                            }
                            continue;
                        } else {
                            legalMoves.addAll(attackPromotions);
                            continue;
                        }
                    }
                }
                if(!candidateDestinationTile.isTileOccupied()){
                    if(this.getAlliance() == Alliance.BLACK) {
                        Move move = calculateBlackEnPassantMoves(board, candidateDestinationCoordinate, 1);
                        if(move != NULL_MOVE) {
                            legalMoves.add(move);
                        }
                        continue;
                    }
                    if(this.getAlliance() == Alliance.WHITE) {
                        Move move = calculateWhiteEnPassantMoves(board, candidateDestinationCoordinate, -1);
                        if(move != NULL_MOVE) {
                            legalMoves.add(move);
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    public Collection<Move> addAttackPromotions(Board board, int candidateDestinationCoordinate) {
        List<Move> legalMoves = new ArrayList<>();
        if((BoardUtils.EIGHTH_ROW[candidateDestinationCoordinate] && this.getAlliance() == Alliance.BLACK) ||
                (BoardUtils.FIRST_ROW[candidateDestinationCoordinate] && this.getAlliance() == Alliance.WHITE)) {
            Move move = calculatePawnAttackMoves(board, candidateDestinationCoordinate);
            if(move != NULL_MOVE) {
                legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.KNIGHT));
                legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.QUEEN));
                legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.BISHOP));
                legalMoves.add(new PawnMovePromotion(board, this, candidateDestinationCoordinate, PieceType.ROOK));
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    public Move calculatePawnAttackMoves(Board board, int destination) {
        Tile candidateDestinationTile = board.getTile(destination);
        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
        if (this.getAlliance() != pieceAtDestination.getAlliance()) {
            Move move = new PawnAttackMove(board, this, destination, pieceAtDestination);
            boolean hasMoved = this.getHasMoved();
            Board transitionBoard = move.execute();
            this.setHasMoved(hasMoved);
            if (!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                return new PawnAttackMove(board, this, destination, pieceAtDestination);
            }
        }
        return NULL_MOVE;
    }

    public Move calculateBlackEnPassantMoves(Board board, int destination, int direction) {
        if(!BoardUtils.FIFTH_ROW[this.getPiecePosition()]) {
            return NULL_MOVE;
        }
        if (!board.getTile(this.getPiecePosition() + direction).isTileOccupied()) {
            return NULL_MOVE;
        }
        if(board.getTile(this.getPiecePosition() + direction).getPiece().getPieceType() != PieceType.PAWN) {
            return NULL_MOVE;
        }
        if(board.getTile(this.getPiecePosition() + direction).getPiece().getAlliance() != Alliance.WHITE) {
            return NULL_MOVE;
        }
        if(board.getTile(this.getPiecePosition() + direction).getPiece().equals(board.getBuilder().getEnPassantPawn())) {
            Move move = new PawnEnPassantMove(board, this, destination, board.getTile(this.getPiecePosition() + direction).getPiece());
            boolean hasMoved = this.getHasMoved();
            Board transitionBoard = move.execute();
            this.setHasMoved(hasMoved);
            if (!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                return new PawnEnPassantMove(board, this, destination, board.getTile(this.getPiecePosition() + direction).getPiece());
            }
        }
        return NULL_MOVE;
    }

    public Move calculateWhiteEnPassantMoves(Board board, int destination, int direction) {
        if(!BoardUtils.FOURTH_ROW[this.getPiecePosition()]) {
            return NULL_MOVE;
        }
        if (!board.getTile(this.getPiecePosition() + direction).isTileOccupied()) {
            return NULL_MOVE;
        }
        if(board.getTile(this.getPiecePosition() + direction).getPiece().getPieceType() != PieceType.PAWN) {
            return NULL_MOVE;
        }
        if(board.getTile(this.getPiecePosition() + direction).getPiece().getAlliance() != Alliance.BLACK) {
            return NULL_MOVE;
        }
        if(board.getTile(this.getPiecePosition() + direction).getPiece().equals(board.getBuilder().getEnPassantPawn())) {
            Move move = new PawnEnPassantMove(board, this, destination, board.getTile(this.getPiecePosition() + direction).getPiece());
            boolean hasMoved = this.getHasMoved();
            Board transitionBoard = move.execute();
            this.setHasMoved(hasMoved);
            if (!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                return new PawnEnPassantMove(board, this, destination, board.getTile(this.getPiecePosition() + direction).getPiece());
            }
        }
        return NULL_MOVE;
    }

    @Override
    public Piece movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }

    public Piece promotePiece(Move move, PieceType chosenType) {
        Piece piece = null;
        if(chosenType == PieceType.ROOK)
            piece = new Rook(move.getDestinationCoordinate(), this.getAlliance());
        if(chosenType == PieceType.QUEEN)
            piece = new Queen(move.getDestinationCoordinate(), this.getAlliance());
        if(chosenType == PieceType.BISHOP)
            piece = new Bishop(move.getDestinationCoordinate(), this.getAlliance());
        if(chosenType == PieceType.KNIGHT)
            piece = new Knight(move.getDestinationCoordinate(), this.getAlliance());
        return piece;
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }
}