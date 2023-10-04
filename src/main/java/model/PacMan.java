package model;

import geometry.RealCoordinates;

/**
 * Implémente PacMan comme un singleton.
 * TODO : ajouter les fonctionnalités suivantes :
 * 1. Gestion du temps d'énergie : un timer qui se décrémente à chaque tick
 *    et qui désactive l'état énergisé quand il atteint 0.
 *    (voir https://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java)
 *
 */
public final class PacMan implements Critter {
    private Direction direction = Direction.NONE;

    private Direction nextDirection = Direction.NONE;
    private RealCoordinates pos;
    private boolean energized;

    private PacMan() {
    }

    public static final PacMan INSTANCE = new PacMan();

    @Override
    public RealCoordinates getPos() {
        return pos;
    }

    @Override
    public double getSpeed() {
        return isEnergized() ? 6 : 4;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getNextDirection(){
        return this.nextDirection;
    }
    public void setNextDirection(Direction nextDirection){
        this.nextDirection = nextDirection;
    }
    public boolean imPacman(){
        return true;
    }


    @Override
    public void setPos(RealCoordinates pos) {
        this.pos = pos;
    }

    /**
     *
     * @return whether Pac-Man just ate an energizer
     */
    public boolean isEnergized() {
        // TODO handle timeout!
        return energized;
    }

    public void setEnergized(boolean energized) {
        this.energized = energized;
    }
}
