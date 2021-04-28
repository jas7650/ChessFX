package chess.engine.board;

import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

public class BoardUtils {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] THIRD_ROW = initRow(16);
    public static final boolean[] FOURTH_ROW = initRow(24);
    public static final boolean[] FIFTH_ROW = initRow(32);
    public static final boolean[] SIXTH_ROW = initRow(40);
    public static final boolean[] SEVENTH_ROW = initRow(48);
    public static final boolean[] EIGHTH_ROW = initRow(56);

    public static final Color[] TILE_COLORS = initTileColors();

    private static Color[] initTileColors() {
        final Color[] colors = new Color[NUM_TILES];
        for(int location = 0; location < 64; location++) {
            int col = location % 8;
            int row = (location - col) / 8;
            Color color;
            if (row % 2 == 0) {
                if (col % 2 == 0) {
                    color = Color.BURLYWOOD;
                } else {
                    color = Color.SADDLEBROWN;
                }
            } else {
                if (col % 2 == 0) {
                    color = Color.SADDLEBROWN;
                } else {
                    color = Color.BURLYWOOD;
                }
            }
            colors[location] = color;
        }
        return colors;
    }

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me");
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_TILES];
        for(int i = 0; i < NUM_TILES; i++) {
            column[i] = false;
        }
        do {
            column[columnNumber] = true;
            columnNumber += 8;
        } while(columnNumber < 64);
        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        for(int i = 0; i < NUM_TILES; i++) {
            row[i] = false;
        }
        do{
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % BoardUtils.NUM_TILES_PER_ROW != 0);
        return row;
    }


    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }
}
