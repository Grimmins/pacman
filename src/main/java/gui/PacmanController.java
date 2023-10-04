package gui;
/**
 * Classe pour le controleur du Pacman
 * keyPressedHandler : récupère la touche appuyée et change la direction du Pacman
 * avec event.getCode() qui renvoie la touche appuyée
 * keyReleasedHandler : rien à faire ???
 */

import model.Direction;
import model.PacMan;
import model.MazeState;
import config.MazeConfig;


import javafx.scene.input.KeyEvent;

public class PacmanController {
    public void keyPressedHandler(KeyEvent event) {
        PacMan.INSTANCE.setDirection(
                switch (event.getCode()) {
                    case LEFT -> Direction.WEST;
                    case RIGHT -> Direction.EAST;
                    case UP -> Direction.NORTH;
                    case DOWN -> Direction.SOUTH;
                    default -> PacMan.INSTANCE.getDirection(); // do nothing
                }
        );
    }
    public void keyReleasedHandler(KeyEvent event) {
        // Nothing to do?
    }
}
