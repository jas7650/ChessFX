package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.CaptureMove;
import chess.engine.move.Move.KingSideCastleMove;
import chess.engine.move.Move.QueenSideCastleMove;
import chess.engine.move.Move.StandardMove;
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

public class King extends Piece {
    private int[] moveDirections = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int position, Alliance alliance) {
        super(PieceType.KING, position, alliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for(int moveDirection : moveDirections) {
            int potentialLocation = this.getPiecePosition() + moveDirection;
            if (BoardUtils.isValidTileCoordinate(potentialLocation)) {
                if (isFirstColumnExclusion(potentialLocation, moveDirection) ||
                        isEightColumnExclusion(potentialLocation, moveDirection)) {
                    break;
                }
                if (BoardUtils.isValidTileCoordinate(potentialLocation)) {
                    Tile destinationTile = board.getTile(potentialLocation);
                    if (!destinationTile.isTileOccupied()) {
                        Move move = new StandardMove(board, this, potentialLocation);
                        boolean hasMoved = this.getHasMoved();
                        Board transitionBoard = move.execute();
                        if(!transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
                            legalMoves.add(move);
                        }
                        this.setHasMoved(hasMoved);
                    } else {
                        Piece pieceAtLocation = destinationTile.getPiece();
                        Alliance alliance = pieceAtLocation.getAlliance();
                        if (alliance != this.getAlliance()) {
                            Move move = new CaptureMove(board, this, potentialLocation, pieceAtLocation);
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
        }
        if(this.getAlliance() == Alliance.BLACK) {
            legalMoves.addAll(calculateBlackKingCastles(board));
        } else {
            legalMoves.addAll(calculateWhiteKingCastles(board));
        }
        return Collections.unmodifiableList(legalMoves);
    }

    protected Collection<Move> calculateWhiteKingCastles(Board board) {
        final Collection<Move> kingCastles = new ArrayList<>();
        if(!this.getHasMoved() && !board.getCurrentPlayer().isInCheck()) {
            //white king side castle
            if(!board.getTile(61).isTileOccupied() && !board.getTile(62).isTileOccupied()) {
                final Tile rookTile = board.getTile(63);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(!Player.isTileAttacked(61, board, Alliance.WHITE) &&
                            !Player.isTileAttacked(62, board, Alliance.WHITE) &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(board, this, 62, (Rook) rookTile.getPiece(), rookTile.getLocation(), 61));
                    }
                }
            }
            //white queen side castle
            if(!board.getTile(59).isTileOccupied() &&
                    !board.getTile(58).isTileOccupied() &&
                    !board.getTile(57).isTileOccupied()) {
                final Tile rookTile = board.getTile(56);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(!Player.isTileAttacked(59, board, Alliance.WHITE) &&
                            !Player.isTileAttacked(58, board, Alliance.WHITE) &&
                            !Player.isTileAttacked(57, board, Alliance.WHITE) &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(board, this, 58, (Rook) rookTile.getPiece(), rookTile.getLocation(), 59));
                    }
                }
            }
        }
        return Collections.unmodifiableCollection(kingCastles);
    }

    protected Collection<Move> calculateBlackKingCastles(Board board) {
        final List<Move> kingCastles = new ArrayList<>();
        if(!this.getHasMoved() && !board.getCurrentPlayer().isInCheck()) {
            //black king side castle
            if(!board.getTile(5).isTileOccupied() && !board.getTile(6).isTileOccupied()) {
                final Tile rookTile = board.getTile(7);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(!Player.isTileAttacked(5, board, Alliance.BLACK) &&
                            !Player.isTileAttacked(6, board, Alliance.BLACK) &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(board, this, 6, (Rook) rookTile.getPiece(), rookTile.getLocation(), 5));
                    }
                }
            }
            //black queen side castle
            if(!board.getTile(1).isTileOccupied() &&
                    !board.getTile(2).isTileOccupied() &&
                    !board.getTile(3).isTileOccupied()) {
                final Tile rookTile = board.getTile(0);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(!Player.isTileAttacked(1, board, Alliance.BLACK) &&
                            !Player.isTileAttacked(2, board, Alliance.BLACK) &&
                            !Player.isTileAttacked(3, board, Alliance.BLACK) &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(board, this, 2, (Rook) rookTile.getPiece(), rookTile.getLocation(), 3));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    @Override
    public Piece movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
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