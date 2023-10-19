package model;

/**
 * Pas grand chose à détailler, ça définit simplement les 4 fantômes.
 * CEPENDANT, il faut implémenter un comportement différent pour chaque fantôme.
 * Chaque fantome devrait avoir sa propre classe ou méthode qui implémente sa logique de déplacement.
 * une classe BlinkyAI, InkyAI, PinkyAI, ClydeAI qui implémentent une interface GhostAI ???
 * Utiliser les données du jeu pour déterminer la direction du fantôme. (la position de PacMan par exemple)
 *
 */

import geometry.RealCoordinates;

public enum Ghost implements Critter {

    // TODO: implement a different AI for each ghost, according to the description in Wikipedia's page
    BLINKY, INKY, PINKY, CLYDE;

    private RealCoordinates pos;
    private Direction direction = Direction.NONE;
    private boolean alive = true;
    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public void setPos(RealCoordinates newPos) {
        pos = newPos;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public double getSpeed() {
        return 0;
    }
    public boolean goToHome(PacMan p){
        RealCoordinates ghost = this.getPos();
        if (ghost.x() >= p.getPos().x()-0.25 && ghost.x() <= p.getPos().x()+0.25 && ghost.y() >=p.getPos().y()-0.25 && ghost.y() <=p.getPos().y()+0.25 && p.isEnergized()){
            this.alive = false;
            return true;
        }
        return false;
    }

}

