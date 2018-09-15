package gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author Samuel Bangslund
 *
 * Game of life. The concept is further described on the Wikipage:
 * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
 *
 * This is the main class to run the game - which is run on the JavaFX
 * application.
 */
public class GameOfLife extends Application {

    public static final byte CELL_SIZE = 8;    // Size of the cells.
    final int GENERATION_SPEED = 5;    // The time between each generation. (Depends on the system speed)

    static Cell[][] cells;  // Where we will store all the cells as a grid.

    int count = 0;    // Used for the delay between each generation.

    static boolean running = false; // Whether or not the program should continue on to the next generation.

    static Group root;          // A JavaFX group component - used when drawing on the canvas.
    static Scene scene;         // A JavaFX scene component - what scene we are currently drawing on.
    static Canvas canvas;       // A JavaFX canvas component - where to draw.
    static GraphicsContext gc;  // A JavaFX class for drawing on the canvas.

    Button toggleButton;    // The toggle button. Toggle the simulation on and off.
    Button resetButton;     // The reset button. Resets the cells to empty.

    @Override
    /**
     * This is where the JavaFX application "starts". From here the program is
     * initialized and displayed.
     */
    public void start(Stage stage) {
        stage.setTitle("Game of life");     // Sets the title of the window.

        // Create buttons for controlling the simulation.
        toggleButton = new Button("Toggle");// Creates a button with the label: Toggle
        resetButton = new Button("Reset");  // Creates a button with the label: Reset
        resetButton.setTranslateX(55);      // The reset button should not be on top of the other button.

        // Create necessary JavaFX components for the simulation.
        root = new Group();              // The group of where we will store our components.
        canvas = new Canvas(600, 600); // The canvas to draw on. This will also act as the window.
        gc = canvas.getGraphicsContext2D(); // A class to draw shapes.

        // Create the cell array - with a length based on the
        // window resolution and the cell size (width and height)
        cells = new Cell[(int) (canvas.getHeight() / CELL_SIZE)][(int) (canvas.getWidth() / CELL_SIZE)];

        drawGrid();   // Draw the initial grid.
        setup();      // Setup eventhandlers.
        drawLoop();   // Run the main loop for the simulation.

        // Add all the components to the group.
        root.getChildren().add(canvas);
        root.getChildren().add(toggleButton);
        root.getChildren().add(resetButton);

        // Add the group to a scene and show that scene.
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Creates the necessary eventHandlers for the simulation to be controlled.
     * This include actionEvents for the buttons and mouseEvent for mouse input.
     */
    void setup() {
        // Creates the ActionEvent for the toggle button. 
        // The toggle button proceeds to toggle between running and
        // not running. Starting and stopping the simulation.
        toggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                running = !running;
            }
        });

        // Creates the ActionEvent for the reset button.
        // The reset button will reset all the cells to their empty state.
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                resetCells();
            }
        });

        // Creates the MouseEvent for the mouse. 
        // This awaits mouse input and upon clicking, will convert
        // the mouse coordinates to cell coordinates. The cell will then
        // be filled or emptied depending on what state it was at first.
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getClickCount() > 0) {
                    Cell cell = cells[(int) Math.floor(t.getY() / CELL_SIZE)][(int) Math.floor(t.getX() / CELL_SIZE)];

                    if (cell.isFilled) {
                        cell.emptyCell(gc);
                    } else {
                        cell.fillCell(gc);
                    }
                }
            }
        });
    }

    /**
     * Draws the initial grid for the cells. This also assigns all the cells to
     * the cells array - with the cell class properties.
     */
    void drawGrid() {
        gc.setStroke(Color.BLACK);  // Makes sure the borders of the cells are BLACK.
        gc.setLineWidth(0.1);       // Makes sure the width of the border is 0.1 wide.

        // Iterates through all the cell coordinates and then proceeds to draw
        // a box around the cell, effectively creating a grid. After each draw,
        // the cell is then added to the cells[][] array.
        for (int y = 0; y < canvas.getHeight() / CELL_SIZE; y++) {
            for (int x = 0; x < canvas.getWidth() / CELL_SIZE; x++) {
                gc.strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cells[y][x] = new Cell(CELL_SIZE, CELL_SIZE, x * CELL_SIZE, y * CELL_SIZE);
            }
        }
    }
    
    

    /**
     * The main loop of the program. From here all the cells are checked for
     * changes and then converted depending on their markings.
     */
    void drawLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    if (count % GENERATION_SPEED == 0) {
                        checkCells();
                        convertCells();

                        gc.restore();
                    }
                    count++;
                }
            }
        }.start();
    }

    /**
     * Converts the cells depending on how the check went. If the cell is marked
     * for filled, the cell will be filled. If the cell isn't marked for filled,
     * the cell will be emptied.
     */
    void convertCells() {
        for (int y = 0; y < canvas.getHeight() / CELL_SIZE; y++) {
            for (int x = 0; x < canvas.getWidth() / CELL_SIZE; x++) {
                Cell cell = cells[y][x];

                if (cell.markedFilled) {
                    cell.fillCell(gc);
                } else {
                    cell.emptyCell(gc);
                }
            }
        }
    }

    /**
     * Checks all the cells by applying the rules of the "Game of life". (The
     * cells have their own check)
     */
    void checkCells() {
        for (int y = 0; y < canvas.getHeight() / CELL_SIZE; y++) {
            for (int x = 0; x < canvas.getWidth() / CELL_SIZE; x++) {
                cells[y][x].checkCell(gc);
            }
        }
    }

    /**
     * Resets all the cells to the empty state. (Marks all the cells for empty
     * and then runs the convertCells() method)
     */
    void resetCells() {
        for (int y = 0; y < canvas.getHeight() / CELL_SIZE; y++) {
            for (int x = 0; x < canvas.getWidth() / CELL_SIZE; x++) {
                cells[y][x].markForEmpty();
            }
        }
        convertCells();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
