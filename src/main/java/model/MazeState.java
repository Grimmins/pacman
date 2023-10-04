package model;

import config.Cell;

/**
 * Cette classe représente l'état du labyrinthe.
 * Elle contient les informations suivantes :
 * - la configuration du labyrinthe (cf. {@link config.MazeConfig})
 * - la gestion des collisions entre les différents éléments du labyrinthe
 * - le score du joueur
 * - le nombre de vies restantes
 * - la position initiale de chaque élément du labyrinthe
 */

import config.MazeConfig;
import config.Cell.Content;
import geometry.IntCoordinates;
import geometry.RealCoordinates;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Map;

import static model.Ghost.*;

public final class MazeState {
    private final MazeConfig config;
    private final int height;
    private final int width;

    private final boolean[][] gridState;

    private final List<Critter> critters;
    private int score;


    private final Map<Critter, RealCoordinates> initialPos;
    private int lives = 3;

    public MazeState(MazeConfig config) {
        this.config = config;
        height = config.getHeight();
        width = config.getWidth();
        critters = List.of(PacMan.INSTANCE, Ghost.CLYDE, BLINKY, INKY, PINKY);
        gridState = new boolean[height][width];
        initialPos = Map.of(
                PacMan.INSTANCE, config.getPacManPos().toRealCoordinates(1.0),
                BLINKY, config.getBlinkyPos().toRealCoordinates(1.0),
                INKY, config.getInkyPos().toRealCoordinates(1.0),
                CLYDE, config.getClydePos().toRealCoordinates(1.0),
                PINKY, config.getPinkyPos().toRealCoordinates(1.0)
        );
        resetCritters();
    }

    public List<Critter> getCritters() {
        return critters;
    }

    public double getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public boolean wall(Direction direction, config.Cell cellule){
        switch (direction){
            case NORTH  : return cellule.northWall();
            case SOUTH  : return cellule.southWall();
            case EAST   : return cellule.eastWall();
            case WEST   : return cellule.westWall();
            default : return false;
        }
    }

    public void update(long deltaTns) {

        /**
         * Reponsable de mettre à jour l'état du jeu.
         * Cette méthode est appelée à chaque frame.
         * Elle gère les collisions entre les différents éléments du labyrinthe.
         * Elle met à jour la position de chaque élément du labyrinthe.
         * Elle met à jour le score du joueur.
         * Elle met à jour le nombre de vies restantes.
         *
         *
         * BEAUCOUP DE CHOSES À FAIRE :
         * 1. Intégrer JavaFX pour afficher le score, le nombre de vies restantes, etc.
         * 2. Afficher un écran de fin de jeu, plutôt que d'appeler un vilain System.exit(0)
         *    message de fin de jeu + permettre au joueur de recommencer ou de quitter le jeu.
         *    (cf. https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Alert.html)
         *    (cf. https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html)
         * 3. déléguer certaines repsonsabilités à d'autres méthodes ?
         */

        for  (var critter: critters) {
            var curPos = critter.getPos();
            var nextPos = critter.nextPos(deltaTns);
            var curNeighbours = curPos.intNeighbours();
            var nextNeighbours = nextPos.intNeighbours();
            if (!curNeighbours.containsAll(nextNeighbours)) { // the critter would overlap new cells. Do we allow it?
                switch (critter.getDirection()) {
                    case NORTH -> {
                        for (var n: curNeighbours) if (config.getCell(n).northWall()) {
                            if (critter.imPacman()){
                                if(critter.getNextDirection() == Direction.NONE){
                                    critter.setNextDirection(Direction.NORTH);
                                }else{
                                    if (!wall(critter.getNextDirection(),config.getCell(n))){
                                        critter.setDirection(critter.getNextDirection());
                                        critter.setNextDirection(Direction.NONE);
                                    }
                                }
                            }else {
                                nextPos = curPos.floorY();
                                critter.setDirection(Direction.NONE);
                            }
                            break;
                        }
                    }
                    case EAST -> {
                        for (var n: curNeighbours) if (config.getCell(n).eastWall()) {
                            if (critter.imPacman()){
                                if(critter.getNextDirection() == Direction.NONE){
                                    critter.setNextDirection(Direction.EAST);
                                }else{
                                    if (!wall(critter.getNextDirection(),config.getCell(n))){
                                        critter.setDirection(critter.getNextDirection());
                                        critter.setNextDirection(Direction.NONE);
                                    }
                                }
                            }else {
                                nextPos = curPos.ceilX();
                                critter.setDirection(Direction.NONE);
                            }
                            break;
                        }
                    }
                    case SOUTH -> {
                        for (var n: curNeighbours) if (config.getCell(n).southWall()) {
                            if (critter.imPacman()){
                                if(critter.getNextDirection() == Direction.NONE){
                                    critter.setNextDirection(Direction.SOUTH);
                                }else{
                                    if (!wall(critter.getNextDirection(),config.getCell(n))){
                                        critter.setDirection(critter.getNextDirection());
                                        critter.setNextDirection(Direction.NONE);
                                    }
                                }
                            }else {
                                nextPos = curPos.ceilY();
                                critter.setDirection(Direction.NONE);
                            }
                            break;
                        }
                    }
                    case WEST -> {
                        for (var n: curNeighbours) if (config.getCell(n).westWall()) {
                            if (critter.imPacman()){
                                if(critter.getNextDirection() == Direction.NONE){
                                    critter.setNextDirection(Direction.WEST);
                                }else{
                                    if (!wall(critter.getNextDirection(),config.getCell(n))){
                                        critter.setDirection(critter.getNextDirection());
                                        critter.setNextDirection(Direction.NONE);
                                    }
                                }
                            }else {
                                nextPos = curPos.floorX();
                                critter.setDirection(Direction.NONE);
                            }
                            break;
                        }
                    }
                }

            }

            critter.setPos(nextPos.warp(width, height));
        }
        // FIXME Pac-Man rules should somehow be in Pacman class
        var pacPos = PacMan.INSTANCE.getPos().round();
        if (!gridState[pacPos.y()][pacPos.x()]) {
            if(config.getCell(pacPos).initialContent()==Content.ENERGIZER){ /* score energizer */
                addScore(5); 
                PacMan.INSTANCE.setEnergized(true);
            }
            else {
                addScore(1);
            }
            gridState[pacPos.y()][pacPos.x()] = true;
        }
        for (var critter : critters) {
            if (critter instanceof Ghost && critter.getPos().round().equals(pacPos)) {
                if (PacMan.INSTANCE.isEnergized()) {
                    addScore(10);
                    resetCritter(critter);
                } else {
                    playerLost();
                    return;
                }
            }
        }
    }

    private void addScore(int increment) {
        score += increment;
        displayScore();
    }

    private void displayScore() {
        // FIXME: this should be displayed in the JavaFX view, not in the console
        System.out.println("Score: " + score);
        System.out.println(PacMan.INSTANCE.isEnergized());
    }

    private void playerLost() {
        // FIXME: this should be displayed in the JavaFX view, not in the console. A game over screen would be nice too.
        lives--;
        if (lives == 0) {
            System.out.println("Game over!");
            System.exit(0);
        }
        System.out.println("Lives: " + lives);
        resetCritters();
    }

    private void resetCritter(Critter critter) {
        critter.setDirection(Direction.NONE);
        critter.setPos(initialPos.get(critter));
    }

    private void resetCritters() {
        for (var critter: critters) resetCritter(critter);
    }

    public MazeConfig getConfig() {
        return config;
    }

    public boolean getGridState(IntCoordinates pos) {
        return gridState[pos.y()][pos.x()];
    }
}
