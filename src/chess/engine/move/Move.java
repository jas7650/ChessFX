package chess.engine.move;

import chess.engine.board.Board;
import chess.engine.board.Board.Builder;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Piece.PieceType;
import chess.engine.pieces.Rook;

import java.io.FileNotFoundException;

public abstract class Move {

    protected int endPosition;
    protected Piece piece;
    protected Board board;

    public static final Move NULL_MOVE = new NullMove();

    public Move(Board board, Piece piece, int endPosition) {
        this.board = board;
        this.endPosition = endPosition;
        this.piece = piece;
    }

    public int getCastleRookStart() {
        return -1;
    }

    public int getCastleRookDestination() {
        return -1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.endPosition;
        result = prime * result + this.piece.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece() == otherMove.getMovedPiece();
    }

    public int getDestinationCoordinate() {
        return this.endPosition;
    }

    public int getCurrentCoordinate() {
        return this.piece.getPiecePosition();
    }


    public Piece getMovedPiece() {
        return this.piece;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Rook getCastleRook() {
        return null;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public boolean isEnPassantMove() {
        return false;
    }

    public boolean isPromotionMove() {
        return false;
    }

    public PieceType getChosenType() {
        return null;
    }

    public Board execute() {
        final Builder builder = new Builder();
        for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
            if(!this.piece.equals(piece)) {
                builder.setPiece(piece);
            }
        }
        for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        builder.setPiece(this.piece.movePiece(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static class StandardMove extends Move {
        public StandardMove(Board board, Piece piece, int endPosition) {
            super(board, piece, endPosition);
        }
    }

    public static class CaptureMove extends Move {
        protected Piece pieceAtLocation;
        public CaptureMove(Board board, Piece piece, int endPosition, Piece pieceAtLocation) {
            super(board, piece, endPosition);
            this.pieceAtLocation = pieceAtLocation;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!this.piece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if(!this.pieceAtLocation.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.piece.movePiece(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.pieceAtLocation;
        }

        @Override
        public int hashCode() {
            return this.pieceAtLocation.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof CaptureMove)) {
                return false;
            }
            final CaptureMove otherAttackMove = (CaptureMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }
    }

    public static final class PawnMove extends Move {

        public PawnMove(Board board, Piece piece, int endPosition) {
            super(board, piece, endPosition);
        }
    }

    public static final class PawnMovePromotion extends Move {
        private PieceType chosenType;
        public PawnMovePromotion(Board board, Piece piece, int endPosition, PieceType chosenType) {
            super(board, piece, endPosition);
            this.chosenType = chosenType;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!this.piece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.piece.movePiece(this);
            final Piece newPiece = movedPawn.promotePiece(this, chosenType);
            builder.setPiece(newPiece);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isPromotionMove() {
            return true;
        }

        @Override
        public PieceType getChosenType() {
            return this.chosenType;
        }
    }

    public static class PawnAttackMove extends CaptureMove {

        public PawnAttackMove(Board board, Piece piece, int endPosition, Piece pieceAtLocation) {
            super(board, piece, endPosition, pieceAtLocation);
        }
    }

    public static final class PawnAttackMovePromotion extends PawnAttackMove {
        private PieceType chosenType;
        public PawnAttackMovePromotion(Board board, Piece piece, int endPosition, Piece pieceAtLocation, PieceType chosenType) {
            super(board, piece, endPosition, pieceAtLocation);
            this.chosenType = chosenType;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!this.piece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if(!this.pieceAtLocation.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            final Pawn movedPawn = (Pawn) this.piece.movePiece(this);
            final Piece newPiece = movedPawn.promotePiece(this, chosenType);
            builder.setPiece(newPiece);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isPromotionMove() {
            return true;
        }

        @Override
        public PieceType getChosenType() {
            return this.chosenType;
        }
    }

    public static class PawnEnPassantMove extends CaptureMove {
        public PawnEnPassantMove(Board board, Piece piece, int endPosition, Piece pieceTaken) {
            super(board, piece, endPosition, pieceTaken);
        }

        @Override
        public boolean isEnPassantMove() {
            return true;
        }
    }

    public static class PawnJump extends Move {

        public PawnJump(Board board, Piece piece, int endPosition) {
            super(board, piece, endPosition);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!this.piece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.piece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    static abstract class CastleMove extends Move {

        final Rook castleRook;
        final int castleRookStart;
        final int castleRookDestination;

        CastleMove(final Board board,
                   final Piece piece,
                   final int destinationCoordinate,
                   final Rook castleRook,
                   final int castleRookStart,
                   final int castleRookDestination) {
            super(board, piece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public int getCastleRookStart() {
            return this.castleRookStart;
        }

        @Override
        public int getCastleRookDestination() {
            return this.castleRookDestination;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if (!this.piece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.piece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getAlliance()));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move {

        public NullMove() {
            super(null, null,-1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("cannot execute a null move");
        }
    }

    public static class MoveFactory {

        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(Board board, int currentPosition, int destinationPosition) {
            if(board.getTile(currentPosition).getPiece().getPieceType() == PieceType.KING) {
                for(final Move move : board.getCurrentPlayer().getCastleMoves()) {
                    if(move.getCurrentCoordinate() == currentPosition &&
                            move.getDestinationCoordinate() == destinationPosition) {
                        return move;
                    }
                }
            }
            for(final Move move : board.getCurrentPlayer().getPlayerLegalMoves()) {
                if(move.getCurrentCoordinate() == currentPosition &&
                        move.getDestinationCoordinate() == destinationPosition) {
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move createPromotionMove(Board board, int currentPosition, int destinationPosition, PieceType pieceType) {
            for(final Move move : board.getCurrentPlayer().getPlayerLegalMoves()) {
                if(move.getCurrentCoordinate() == currentPosition &&
                        move.getDestinationCoordinate() == destinationPosition &&
                        move.getChosenType() == pieceType) {
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move getNullMove() {
            return NULL_MOVE;
        }
    }
}
