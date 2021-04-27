package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.move.Move;
import chess.engine.move.MoveStatus;
import chess.engine.move.MoveTransition;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Piece.PieceType;

import java.util.*;

import static chess.engine.move.Move.NULL_MOVE;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;

    public Player(final Board board) {
        this.board = board;
        this.playerKing = establishKing();
    }

    public static boolean isTileAttacked(int position, Board board, Alliance alliance) {
        boolean pawns, knights, diagonals, straights;
        pawns = checkPawns(position, board, alliance);
        knights = checkKnights(position, board, alliance);
        diagonals = checkDiagonals(position, board, alliance);
        straights = checkStraights(position, board, alliance);
        return  pawns || knights || diagonals || straights;
    }

    public static boolean doesMoveExposeKing(Move move, Board board) {
        if(!isAPieceBetweenPositionAndKing(move.getCurrentCoordinate(), board)) {
            int testDirection = getDirectionToKing(move.getCurrentCoordinate(), board) * -1;
            if(isTileAttackedFromDirection(move.getCurrentCoordinate(), testDirection, board)) {
                int moveDirection = move.getMoveDirection();
                if(testDirection != Math.abs(moveDirection)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean isTileAttackedFromDirection(int position, int direction, Board board) {
        int checkSpot = position + direction;
        Alliance alliance = board.getTile(position).getPiece().getAlliance();
        while(BoardUtils.isValidTileCoordinate(checkSpot)) {
            if(board.getTile(checkSpot).isTileOccupied()) {
                if(board.getTile(checkSpot).getPiece().getAlliance() != alliance) {
                    if(direction == -7 || direction == 7 || direction == -9 || direction == 9) {
                        if(board.getTile(checkSpot).getPiece().getPieceType() == PieceType.QUEEN ||
                                board.getTile(checkSpot).getPiece().getPieceType() == PieceType.BISHOP) {
                            return true;
                        } else {
                            break;
                        }
                    } else {
                        if(board.getTile(checkSpot).getPiece().getPieceType() == PieceType.QUEEN ||
                                board.getTile(checkSpot).getPiece().getPieceType() == PieceType.ROOK) {
                            return true;
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            checkSpot += direction;
        }
        return false;
    }

    public static int getDirectionToKing(int position, Board board) {
        int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};
        Alliance alliance = board.getTile(position).getPiece().getAlliance();
        int checkSpot;
        int kingDirection = 0;
        for(int direction : directions) {
            checkSpot = position + direction;
            while(BoardUtils.isValidTileCoordinate(checkSpot)) {
                if(board.getTile(checkSpot).isTileOccupied()) {
                    if(board.getTile(checkSpot).getPiece().getPieceType() == PieceType.KING) {
                        if(board.getTile(checkSpot).getPiece().getAlliance() == alliance) {
                            kingDirection = direction;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                checkSpot += direction;
            }
        }
        return kingDirection;
    }

    public static boolean isAPieceBetweenPositionAndKing(int position, Board board) {
        int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};
        Alliance alliance = board.getTile(position).getPiece().getAlliance();
        int checkSpot;
        for(int direction : directions) {
            checkSpot = position + direction;
            while(BoardUtils.isValidTileCoordinate(checkSpot)) {
                if(board.getTile(checkSpot).isTileOccupied()) {
                    if(board.getTile(checkSpot).getPiece().getPieceType() == PieceType.KING) {
                        if(board.getTile(checkSpot).getPiece().getAlliance() == alliance) {
                            return false;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                checkSpot += direction;
            }
        }
        return true;
    }

    private static boolean checkPawns(int position, Board board, Alliance alliance) {
        int[] attackDirections = {-9, -7, 7, 9};

        for(int direction : attackDirections) {
            int potentialThreatLocation = position + direction;
            if(BoardUtils.isValidTileCoordinate(potentialThreatLocation)) {
                if (board.getTile(potentialThreatLocation).isTileOccupied()) {
                    if (board.getTile(potentialThreatLocation).getPiece().getPieceType() == PieceType.PAWN &&
                            alliance != board.getTile(potentialThreatLocation).getPiece().getAlliance()) {
                        if (board.getTile(potentialThreatLocation).getPiece().getAlliance() == Alliance.BLACK) {
                            if (!((BoardUtils.FIRST_COLUMN[potentialThreatLocation] && direction == -9) ||
                                    (BoardUtils.EIGHTH_COLUMN[potentialThreatLocation] && direction == -7))) {
                                if (direction == -7 || direction == -9) {
                                    return true;
                                }
                            }
                        } else {
                            if (!((BoardUtils.FIRST_COLUMN[potentialThreatLocation] && direction == 9) ||
                                    (BoardUtils.EIGHTH_COLUMN[potentialThreatLocation] && direction == 7))) {
                                if (direction == 7 || direction == 9) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkKnights(int position, Board board, Alliance alliance) {
        int[] attackDirections = {-17, -15, -10, -6, 6, 10, 15, 17};
        for(int direction : attackDirections) {
            int potentialThreatLocation = position + direction;
            if(BoardUtils.isValidTileCoordinate(potentialThreatLocation)) {
                if(!((BoardUtils.FIRST_COLUMN[potentialThreatLocation] && (direction == 17 || direction == 10 || direction == -6 || direction == -15))||
                        (BoardUtils.SECOND_COLUMN[potentialThreatLocation] && (direction == 10 || direction == -6)) ||
                        (BoardUtils.SEVENTH_COLUMN[potentialThreatLocation] && (direction == 6 || direction == -10)) ||
                        (BoardUtils.EIGHTH_COLUMN[potentialThreatLocation] && (direction == 15 || direction == 6 || direction == -10 || direction == -17)))) {
                    if (board.getTile(potentialThreatLocation).isTileOccupied()) {
                        if (board.getTile(potentialThreatLocation).getPiece().getPieceType() == PieceType.KNIGHT &&
                                alliance != board.getTile(potentialThreatLocation).getPiece().getAlliance()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkStraights(int position, Board board, Alliance alliance) {
        int[] attackDirections = {-8, -1, 1, 8};
        for(int direction : attackDirections) {
            if(BoardUtils.isValidTileCoordinate(position)) {
                int potentialThreatLocation = position + direction;
                while (BoardUtils.isValidTileCoordinate(potentialThreatLocation)) {
                    if ((BoardUtils.FIRST_COLUMN[potentialThreatLocation] && direction == 1) ||
                        (BoardUtils.EIGHTH_COLUMN[potentialThreatLocation] && direction == -1)) {
                        break;
                    }
                    if (board.getTile(potentialThreatLocation).isTileOccupied()) {
                        if (alliance != board.getTile(potentialThreatLocation).getPiece().getAlliance()) {
                            if(board.getTile(potentialThreatLocation).getPiece().getPieceType() == PieceType.QUEEN ||
                            board.getTile(potentialThreatLocation).getPiece().getPieceType() == PieceType.ROOK) {
                                return true;
                            }
                        }
                        break;
                    }
                    potentialThreatLocation += direction;
                }
            }
        }
        return false;
    }

    private static boolean checkDiagonals(int position, Board board, Alliance alliance) {
        int[] attackDirections = {-9, -7, 7, 9};

        for(int direction : attackDirections) {
            if(BoardUtils.isValidTileCoordinate(position)) {
                int potentialThreatLocation = position + direction;
                while (BoardUtils.isValidTileCoordinate(potentialThreatLocation)) {
                    if ((BoardUtils.FIRST_COLUMN[potentialThreatLocation] && (direction == 9 || direction == -7)) ||
                            (BoardUtils.EIGHTH_COLUMN[potentialThreatLocation] && (direction == 7) || direction == -9)) {
                        break;
                    }
                    if (board.getTile(potentialThreatLocation).isTileOccupied()) {
                        if (alliance != board.getTile(potentialThreatLocation).getPiece().getAlliance()) {
                            if(board.getTile(potentialThreatLocation).getPiece().getPieceType() == PieceType.QUEEN ||
                                    board.getTile(potentialThreatLocation).getPiece().getPieceType() == PieceType.BISHOP) {
                                return true;
                            }
                        }
                        break;
                    }
                    potentialThreatLocation += direction;
                }
            }
        }
        return false;
    }

    public static boolean leavesKingInCheck(Move move, Board board) {
        if(board.getCurrentPlayer().isInCheck()) {

        }
        return false;
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
        return isTileAttacked(this.playerKing.getPiecePosition(), board, this.getAlliance());
    }

    //TODO implement methods below!!!
    public boolean isInCheckMate() {
        return isTileAttacked(this.playerKing.getPiecePosition(), board, this.getAlliance()) && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !isTileAttacked(this.playerKing.getPiecePosition(), board, this.getAlliance()) && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        int[] moveDirections = {-9, -8, -7, -1, 1, 7, 8, 9};
        for(int direction : moveDirections) {
            int destination = this.playerKing.getPiecePosition() + direction;
            if(BoardUtils.isValidTileCoordinate(destination)) {
                if(!board.getTile(destination).isTileOccupied()) {
                    Move move = Move.MoveFactory.createMove(board, this.playerKing.getPiecePosition(), destination);
                    MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                    if(moveTransition.getMoveStatus().isDone()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCastled() {
        return false;
    }

    public MoveTransition makeMove(final Move move) {
        if(move == NULL_MOVE) {
            return new MoveTransition(this.board, MoveStatus.ILLEGAL_MOVE);
        } else {
            if (!(move.getMovedPiece().calculateLegalMoves(this.board).contains(move)) ||
                    move.getMovedPiece().getAlliance() != board.getCurrentPlayer().getAlliance()) {
                System.out.println("Move is illegal");
                return new MoveTransition(this.board, MoveStatus.ILLEGAL_MOVE);
            }
            final Board transitionBoard = move.execute();
            if (Player.isTileAttacked(transitionBoard.getCurrentPlayer().getOpponent().playerKing.getPiecePosition(), transitionBoard, transitionBoard.getCurrentPlayer().getOpponent().playerKing.getAlliance())) {
                System.out.println("Leaves player in check");
                return new MoveTransition(this.board, MoveStatus.LEAVES_PLAYER_IN_CHECK);
            }
            return new MoveTransition(transitionBoard, MoveStatus.DONE);
        }
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
}
