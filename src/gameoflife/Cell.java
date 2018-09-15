package gameoflife;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Samuel Bangslund
 *
 * The cell class. This class is meant to check and control each of the cells
 * individually.
 */
public class Cell {
    int xPos        = 0;    // The xPosition of the cell. (In px)
    int yPos        = 0;    // The yPosition of the cell. (In px)

    boolean markedFilled = false;   // Whether or not the cell is marked.
    boolean isFilled = false;       // Whether or not the cell is filled.

    Canvas canvas;  // Canvas for global access.

    /**
     * The cell object.  
     * @param width of the cell.
     * @param height of the cell.
     * @param x position of the cell. (In px)
     * @param y position of the cell. (In px)
     */
    public Cell(int width, int height, int x, int y) {
        setXPos(x);
        setYPos(y);

        canvas = GameOfLife.canvas; // Creates a reference to the main class.
    }

    /**
     * Apply all the rules for the cell. This is based on the
     * number of neighbours counted in the countNeighbours() method.
     * @param gc the GraphicsContext object.
     */
    public void checkCell(GraphicsContext gc) {
        int neighbours = countNeighbours(); // Gets the number of neighbours.

        if (neighbours < 2) {                           // rule 1
            markForEmpty();
        } else if (neighbours == 3) {                   // rule 2
            markForFill();
        } else if (neighbours == 2 || neighbours == 3) {// rule 3
            if (isFilled) {
                markForFill();
            } else {
                markForEmpty();
            }
        } else if (neighbours > 3) {                    // rule 4
            markForEmpty();
        }
    }

    /**
     * Fill the cell.
     * @param gc the GraphicContext object.
     */
    public void fillCell(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(getGlobalX(), getGlobalY(), getSize(), getSize());

        isFilled = true;
        gc.restore();
    }

    /**
     * Empties the cell.
     * @param gc the GraphicsContext object.
     */
    public void emptyCell(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(getGlobalX(), getGlobalY(), getSize(), getSize());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.1);
        gc.strokeRect(getGlobalX(), getGlobalY(), getSize(), getSize());

        isFilled = false;
        gc.restore();
    }
    
    /**
     * Converts the cell depending on if the cell is marked for fill or not.
     * @param gc the GraphicsContext object.
     */
    public void convertCell(GraphicsContext gc) {
        if (markedFilled) {
            fillCell(gc);
        } else {
            emptyCell(gc);
        }
    }

    /**
     * Counts the number of neighbours around the cell. (3x3)
     * @return number of neighbours.
     */
    public int countNeighbours() {
        int totalNeighbours = 0;

        // Iterates through the 3x3 area around the cell.
        for (int y = getLocalY() - 1; y <= getLocalY() + 1; y++) {
            for (int x = getLocalX() - 1; x <= getLocalX() + 1; x++) {
                // Ignore if the cell to check is itself.
                if (y == getLocalY() && x == getLocalX()) {
                
                // Ignore if the cell to check is out of limits.
                } else if (y < 0 || y > canvas.getHeight() / getSize() - 1 || x < 0 || x > canvas.getWidth() / getSize() - 1) {
                } else {
                    // Increments the number of neighbours if a live cell is
                    // found among the checked cells.
                    if (GameOfLife.cells[y][x].isFilled) {
                        totalNeighbours++;
                    }
                }
            }
        }
        return totalNeighbours;
    }

    /**
     * Mark the cell for fill.
     */
    public void markForFill() {
        markedFilled = true;
    }

    /**
     * Mark the cell for empty.
     */
    public void markForEmpty() {
        markedFilled = false;
    }

    // Setters
    private void setXPos(int x) {
        xPos = x;
    }
    private void setYPos(int y) {
        yPos = y;
    }

    // Getters
    int getGlobalX() {
        return xPos;
    }
    int getGlobalY() {
        return yPos;
    }
    int getLocalX() {
        return xPos / GameOfLife.CELL_SIZE;
    }
    int getLocalY() {
        return yPos / GameOfLife.CELL_SIZE;
    }
    int getSize() {
        return GameOfLife.CELL_SIZE;
    }
}
