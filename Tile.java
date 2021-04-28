package chess.engine.board;

import chess.engine.pieces.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    private int location;
    protected Piece piece;

    public Tile(int location) {
        this.location = location;
        this.piece = null;
    }
    public Tile(int location, Piece piece) {
        this.location = location;
        this.piece = piece;
    }

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for(int i = 0; i < 64; i++){
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return Collections.unmodifiableMap(emptyTileMap);
    }

    public static Tile createTile(int location, Piece piece) {
        return piece != null ? new OccupiedTile(location, piece) : EMPTY_TILES_CACHE.get(location);
    }

    public abstract Piece getPiece();

    public abstract boolean isTileOccupied();

    public int getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Location: " + getLocation();
    }

    public static class OccupiedTile extends Tile {

        public OccupiedTile(int location, Piece piece) {
            super(location, piece);
        }

        @Override
        public Piece getPiece() {
            return this.piece;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }
    }

    public static class EmptyTile extends Tile {

        public EmptyTile(int location) {
            super(location);
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }
    }
}