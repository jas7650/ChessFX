package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.move.Move;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

public abstract class Piece {
    protected int position;
    protected PieceType pieceType;
    protected Alliance alliance;
    protected boolean hasMoved;

    public Piece(PieceType pieceType, int position, Alliance alliance) {
        this.pieceType = pieceType;
        this.position = position;
        this.alliance = alliance;
        this.hasMoved = false;
    }

    public int getPiecePosition() {
        return this.position;
    }

    public Alliance getAlliance() {
        return this.alliance;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType {
        PAWN("P") {
            @Override
            public Image getImage(Alliance alliance) throws FileNotFoundException {
                if(alliance == Alliance.WHITE) {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\White_Pawn.png"));
                }
                else {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\Black_Pawn.png"));
                }
            }

            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public Image getImage(Alliance alliance) throws FileNotFoundException {
                if(alliance == Alliance.WHITE) {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\White_Knight.png"));
                }
                else {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\Black_Knight.png"));
                }
            }

            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public Image getImage(Alliance alliance) throws FileNotFoundException {
                if(alliance == Alliance.WHITE) {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\White_Bishop.png"));
                }
                else {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\Black_Bishop.png"));
                }
            }
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public Image getImage(Alliance alliance) throws FileNotFoundException {
                if(alliance == Alliance.WHITE) {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\White_Rook.png"));
                }
                else {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\Black_Rook.png"));
                }
            }

            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q") {
            @Override
            public Image getImage(Alliance alliance) throws FileNotFoundException {
                if(alliance == Alliance.WHITE) {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\White_Queen.png"));
                }
                else {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\Black_Queen.png"));
                }
            }

            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K") {
            @Override
            public Image getImage(Alliance alliance) throws FileNotFoundException {
                if(alliance == Alliance.WHITE) {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\White_King.png"));
                }
                else {
                    return new Image(new FileInputStream("C:\\Users\\justi\\OneDrive\\Documents\\Projects\\ChessGameFX\\src\\PieceImages\\Black_King.png"));
                }
            }

            @Override
            public boolean isKing() {
                return true;
            }
            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;

        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract Image getImage(Alliance alliance) throws FileNotFoundException;


        public abstract boolean isKing();

        public abstract boolean isRook();
    }
}
