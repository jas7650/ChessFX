package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.QueenSideCastleMove;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board, final Collection<Move> playerLegalMoves, final Collection<Move> opponentLegalMoves) {
        super(board, playerLegalMoves, opponentLegalMoves);
    }

    @Override
    public Collection<Move> calculateCastleMoves(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {
        final List<Move> kingCastles = new ArrayList<>();
        if(!this.getPlayerKing().getHasMoved() && !this.isInCheck()) {
            //black king side castle
            if(!board.getTile(5).isTileOccupied() && !board.getTile(6).isTileOccupied()) {
                final Tile rookTile = board.getTile(7);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingSideCastleMove(board, this.getPlayerKing(), 6, (Rook) rookTile.getPiece(), rookTile.getLocation(), 5));
                    }
                }
            }
            //black queen side castle
            if(!board.getTile(1).isTileOccupied() &&
                    !board.getTile(2).isTileOccupied() &&
                    !board.getTile(3).isTileOccupied()) {
                final Tile rookTile = board.getTile(0);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(Player.calculateAttacksOnTile(1, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(board, this.getPlayerKing(), 2, (Rook) rookTile.getPiece(), rookTile.getLocation(), 3));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }
}