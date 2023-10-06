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
import GhostsAI.ClydeAI;
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
            if (critter == CLYDE) {
                Direction newDirClyde = Direction.SOUTH;
                CLYDE.setDirection(newDirClyde);
            }
            if (!curNeighbours.containsAll(nextNeighbours)) { // the critter would overlap new cells. Do we allow it?
                switch (critter.getDirection()) {
                    case NORTH -> {
                        for (var n: curNeighbours) if (config.getCell(n).northWall()) {
                            nextPos = curPos.floorY();
                            critter.setDirection(Direction.NONE);
                            break;
                        }
                    }
                    case EAST -> {
                        for (var n: curNeighbours) if (config.getCell(n).eastWall()) {
                            nextPos = curPos.ceilX();
                            critter.setDirection(Direction.NONE);
                            break;
                        }
                    }
                    case SOUTH -> {
                        for (var n: curNeighbours) if (config.getCell(n).southWall()) {
                            nextPos = curPos.ceilY();
                            critter.setDirection(Direction.NONE);
                            break;
                        }
                    }
                    case WEST -> {
                        for (var n: curNeighbours) if (config.getCell(n).westWall()) {
                            nextPos = curPos.floorX();
                            critter.setDirection(Direction.NONE);
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
                PacMan.INSTANCE.setEnergized();
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

//    public void move(Ghost ghost){
//        var PacmanDirection = PacMan.INSTANCE.getDirection();
//        switch (PacmanDirection){
//            case NONE -> ghost.setDirection(NONE);
//            case SOUTH -> ghost.setDirection(SOUTH);
//            case NORTH -> ghost.setDirection(NORTH);
//            case WEST -> ghost.setDirection(WEST);
//            case EAST -> ghost.setDirection(EAST);
//        }
//    }
    private boolean isWallAtPosition(IntCoordinates position, Direction direction) {
        Cell cell = config.getCell(position);
    //on Vérifie selon la cellule si il y a un mur dans une direction demandée.
        switch (direction) {
            case NORTH:
                return cell.northWall();
            case EAST:
                return cell.eastWall();
            case SOUTH:
                return cell.southWall();
            case WEST:
                return cell.westWall();
            default:
                return false; //pas de mur dans la direction demandée
        }
    }

    /*Cette fonction update est appelée dans MazeState.update() pour voir si la dernière direction demandée
      est possible à effectuer à chaque rafraichissement de la grille.
    */
//    public void update() {
//        //On vérifie si pacman peut se déplacer dans la direction demandée.
//        if (lastInputDirection!=null&&canMoveInDirection(lastInputDirection)) {
//            RealCoordinates currentPos = PacMan.INSTANCE.getPos();
//            RealCoordinates NewPos = new RealCoordinates(Math.round(currentPos.getX()), Math.round(currentPos.getY()));
//            /*
//            ici on a une mini zone de tolérance, mais en 4.9999 pacman ne peut pas descendre par exemple, il doit etre
//            exactement en 5.0 donc on "Téléporte" pacman à la position pour qu'il reste centré.
//            La zone de tolérance étant réellement petite, on ne voit pas la différence, le mouvement reste entièrement fluide
//            */
//            PacMan.INSTANCE.setPos(NewPos);
//            //un fois qu'il est centré sur la case on change sa direction et ça marche car il est pile en .0
//            PacMan.INSTANCE.setDirection(lastInputDirection);
//            lastInputDirection=null;
//        }
//    }

    private boolean canMoveInDirection(Direction direction, Critter critter) {
        //On récipère la position actuelle de pacman
//        RealCoordinates currentPos = PacMan.INSTANCE.getPos();
        RealCoordinates currentPos = critter.getPos();
        /*
        on définit l'écart de tolérance pour trouver quand pacman s'approche le plus possible du centre de la cellule
        en effet, en faisant les updates, pacman sera par exemple en 1,823 puis en 1,934040 puis en 2,003983 mais
        jamais pile en 2,0 donc on teste quand il entre dans un certain ecart.
        */
        double tolerance = 0.025;

        //ensuite on calcule au vu des ses deux coordonées s'il peut passer.
        boolean isCenteredX = Math.abs(currentPos.x() - Math.round(currentPos.x())) < tolerance;
        boolean isCenteredY = Math.abs(currentPos.y() - Math.round(currentPos.y())) < tolerance;

        if (isCenteredX && isCenteredY) {
            //Enfin, on vérifie selon la direction s'il y a un mur dans la cellule suivante.
            //En effet, même s'il est centré, la présence d'un mur empeche tout simplement pacman de se déplacer.
            switch (direction) {
                case NORTH:
                    return !isWallAtPosition(currentPos.round(), direction);
                case EAST:
                    return !isWallAtPosition(currentPos.round(), direction);
                case SOUTH:
                    return !isWallAtPosition(currentPos.round(), direction);
                case WEST:
                    return !isWallAtPosition(currentPos.round(), direction);
                case NONE:
                    return true;
            }
        }
        return false;
    }

//    private Direction[] hasChoices(Critter critter,)
}
