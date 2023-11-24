package GhostsAI;

import geometry.AStar;
import geometry.IntCoordinates;
import model.Direction;
import model.Ghost;

import java.util.ArrayList;
import java.util.Random;

import config.MazeConfig;

public class ClydeAI{

    public static Direction getRandomDir(){ // Renvoie une direction au hasard.
        Random rd = new Random();
        int n = rd.nextInt(4);
        return switch (n) {
            case 0 -> Direction.NORTH;
            case 1 -> Direction.SOUTH;
            case 2 -> Direction.EAST;
            case 3 -> Direction.WEST;
            default -> Direction.NONE;
        };
    }

    public static boolean isInNode(MazeConfig config, IntCoordinates intC){ // Vérifie que la case courante est une intersection (càd pas une pipe)
        return !config.getCell(intC).isPipe();
    }

    public static Direction getDirection(MazeConfig config, IntCoordinates intC, Direction defaultDir,IntCoordinates ghostPos) {
        if (!Ghost.CLYDE.isAlive()) {
            if (intC.equals(config.getGhostHousePos())){
                return Direction.NONE;
            }else{
                ArrayList<IntCoordinates> path = AStar.shortestPath(ghostPos, config.getGhostHousePos(), config);
                int pathlen = path.size();
                IntCoordinates nextPos = path.get(pathlen-1);
                return BlinkyAI.whichDir(ghostPos
                        , nextPos);


            }
        } else {
            if (isInNode(config, intC)) {
                return getRandomDir();
            } else {
                return defaultDir;
            }
        }
    }
}
