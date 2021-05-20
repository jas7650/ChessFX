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

public class WhitePlayer extends Player{


    public WhitePlayer(final Board board, final Collection<Move> playerLegalMoves, final Collection<Move> opponentLegalMoves) {
        super(board, playerLegalMoves, opponentLegalMoves);
    }

    @Override
    public Collection<Move> calculateCastleMoves(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {
        final Collection<Move> kingCastles = new ArrayList<>();
        if(!this.getPlayerKing().getHasMoved() && !this.isInCheck()) {
            //white king side castle
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(63);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingSideCastleMove(this.board, this.getPlayerKing(), 62, (Rook) rookTile.getPiece(), rookTile.getLocation(), 61));
                    }
                }
            }
            //white queen side castle
            if(!this.board.getTile(59).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(57).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(56);
                if(rookTile.isTileOccupied() && !rookTile.getPiece().getHasMoved()) {
                    if(Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(57, opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenSideCastleMove(this.board, this.getPlayerKing(), 58, (Rook) rookTile.getPiece(), rookTile.getLocation(), 59));
                    }
                }
            }
        }
        return Collections.unmodifiableCollection(kingCastles);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }
}
