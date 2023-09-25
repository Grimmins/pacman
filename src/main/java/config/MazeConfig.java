package config;

import geometry.IntCoordinates;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static config.Cell.*;
import static config.Cell.Content.*;


// tutur : la classe MazeConfig
public class MazeConfig {
    public MazeConfig(Cell[][] grid, IntCoordinates pacManPos, IntCoordinates blinkyPos, IntCoordinates pinkyPos,
                      IntCoordinates inkyPos, IntCoordinates clydePos) {
        this.grid = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < getHeight(); i++) {
            if (getWidth() >= 0) System.arraycopy(grid[i], 0, this.grid[i], 0, getHeight());
        }
        this.pacManPos = pacManPos;
        this.blinkyPos = blinkyPos;
        this.inkyPos = inkyPos;
        this.pinkyPos = pinkyPos;
        this.clydePos = clydePos;
    }

    private final Cell[][] grid;
    private final IntCoordinates pacManPos, blinkyPos, pinkyPos, inkyPos, clydePos;

    public IntCoordinates getPacManPos() {
        return pacManPos;
    }

    public IntCoordinates getBlinkyPos() {
        return blinkyPos;
    }

    public IntCoordinates getPinkyPos() {
        return pinkyPos;
    }

    public IntCoordinates getInkyPos() {
        return inkyPos;
    }

    public IntCoordinates getClydePos() {
        return clydePos;
    }

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight() {
        return grid.length;
    }

    public Cell getCell(IntCoordinates pos) {
        return grid[Math.floorMod(pos.y(), getHeight())][Math.floorMod(pos.x(), getWidth())];
    }

    /** *
     *
     * */
    public static boolean isLab(String[][] lab) { // prend labyrinthe sous format tableau de tableau de String et renvoie s'il est valide d'après le format indiqué en commentaire en dessous
        for(int i  = 0; i < lab.length ;i++) { // double parcours du tableau
            for(int j  = 0; j < lab[0].length ;j++) {
                if(i % 2 == 0) {
                    if(j % 2 == 0) {
                        if (!(lab[i][j].equals("+"))) { // dans les cases de coordonnées [paires]-[paires], on a forcément des coins de murs
                            return false;
                        }
                    } else {
                        if (!(lab[i][j].equals("|") || lab[i][j].equals(" "))) { // dans les cases [paires]-[impaires] on doit avoir des murs verticaux (|) ou du vide
                            return false;
                        }
                    }
                } else {
                    if(j % 2 == 0) { // dans les cases [impaires]-[paires] on doit avoir des murs horizontaux (---) ou du vide
                        if (!(lab[i][j].equals("---") || lab[i][j].equals("   "))) {
                            return false;
                        }
                    } else {
                        if (!(lab[i][j].equals(" E ") || lab[i][j].equals("   ") || lab[i][j].equals(" . "))) { // dans les cases de coordonnées [impaires]-[impaires] on a forcément les contenus des cellules :
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static MazeConfig txtToLab(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        int height = lines.size()-5;
        int width = 2 * (lines.get(0).length()/4) + 1;
        String[] linesArray = new String[lines.size()];
        lines.toArray(linesArray);
        String[][] lab = new String[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < height; j++) {
                if (j % 2 == 0) {
                    lab[i][j] = "" + linesArray[i].charAt(2*j);
                } else {
                    lab[i][j] = "" + linesArray[i].charAt(2*j-1) + linesArray[i].charAt(2*j) + linesArray[i].charAt(2*j+1);
                }
            }
        }
        String[][] pos = new String[5][];
        for(int i = height ; i < height + 5; i++){
            pos[i-height] = linesArray[i].split(",");
        }
        int[][] coord = new int[5][];
        for(int i = 0 ; i < 5; i++){
            coord[i][0] = Integer.valueOf(pos[i][0].substring(3));
            coord[i][1] = Integer.valueOf(pos[i][1]);
        }
        if (isLab(lab)) {
            return new MazeConfig(stringToCell(lab), new IntCoordinates(coord[0][0], coord[0][1]),
                    new IntCoordinates(coord[1][0], coord[1][1]), new IntCoordinates(coord[2][0], coord[2][1]),
                    new IntCoordinates(coord[3][0], coord[3][1]), new IntCoordinates(coord[4][0], coord[4][1]));
        }
        System.out.println("ERREUR DE FORMAT");
        return null;
    }


    public static Cell[][] stringToCell(String[][] lab){
        Cell[][] grid = new Cell[lab.length][lab[0].length];
        for(int i = 1; i < lab.length; i+=2) {
            for(int j = 1; j < lab[0].length; j+=2) {
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals(" ") &&
                    lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i/2][j/2] = open(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i/2][j/2] = open(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i/2][j/2] = open(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals("|") &&
                    lab[i][j - 1].equals("---") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i/2][j/2] = closed(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i/2][j/2] = closed(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i/2][j/2] = closed(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals(" ") &&
                    lab[i][j - 1].equals("---") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i/2][j/2] = hPipe(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i/2][j/2] = hPipe(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i/2][j/2] = hPipe(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals("|") &&
                    lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i/2][j/2] = vPipe(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i/2][j/2] = vPipe(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i/2][j/2] = vPipe(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals("|") &&
                    lab[i][j - 1].equals("---") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = swVee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = swVee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = swVee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals("|") &&
                    lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = nwVee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = nwVee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = nwVee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals(" ") &&
                        lab[i][j - 1].equals("---") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = seVee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = seVee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = seVee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals("|") &&
                        lab[i][j - 1].equals("---") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = swVee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = swVee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = swVee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals("|") &&
                        lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = nU(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = nU(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = nU(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals("|") &&
                        lab[i][j - 1].equals("---") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = sU(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = sU(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = sU(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals(" ") &&
                        lab[i][j - 1].equals("---") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = eU(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = eU(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = eU(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals("|") &&
                        lab[i][j - 1].equals("---") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = wU(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = wU(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = wU(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals(" ") &&
                        lab[i][j - 1].equals("---") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = nTee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = nTee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = nTee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals("|") &&
                        lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = eTee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = eTee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = eTee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals(" ") && lab[i + 1][j].equals(" ") &&
                        lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("---")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = sTee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = sTee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = sTee(ENERGIZER);
                    }
                }
                if (lab[i - 1][j].equals("|") && lab[i + 1][j].equals(" ") &&
                        lab[i][j - 1].equals("   ") && lab[i][j + 1].equals("   ")) {
                    if (lab[i][j].equals(" . ")) {
                        grid[i / 2][j / 2] = wTee(DOT);
                    }
                    if (lab[i][j].equals("   ")) {
                        grid[i / 2][j / 2] = wTee(NOTHING);
                    }
                    if (lab[i][j].equals(" E ")) {
                        grid[i / 2][j / 2] = wTee(ENERGIZER);
                    }
                }
            }
        }
        return grid;
    }

    /** TODO : labyrinthes pourraient être configurés à partir d'un fichier texte
     *  1. Définir une structure de fichier texte qui représente le labyrinthe, et des caractères pour représenter les
     *     différents types de cellules. Par exemple, le fichier suivant pourrait représenter un labyrinthe :
     *     <pre>
     *         +---+---+---+---+---+---+
     *         | .   .   . | .   .   . |
     *         +---+---+   +   +---+   +
     *         | .   . | .   . | . | . |
     *         +   +   +   +   +   +   + 
     *         | .   .   . | . | .   . |
     *         +   +   +   +   +   +   +
     *         | . | .   . | .   . | . |
     *         +---+---+---+---+---+---+
     *     </pre>
     *         Dans ce fichier, les caractères utilisés sont :
     *         <ul>
     *             <li> '+' pour les coins </li>
     *             <li> '-' pour les murs horizontaux </li>
     *             <li> '|' pour les murs verticaux </li>
     *             <li> '.' pour les points </li>
     *             <li> ' ' pour les cases vides </li>
     *             <li> 'E' le Xanax de Pacman (pour ceux qui ont la réf xd) </li>
     *             <li> 'PAC' pour Pacman </li>
     *             <li> 'BLK' pour Blinky </li>
     *             <li> 'PIK' pour Pinky </li>
     *             <li> 'INK' pour Inky </li>
     *             <li> 'CLY' pour Clyde </li>
     *          </ul>
     *  2. On pourrait alors utiliser la fonction suivante pour lire un labyrinthe à partir d'un fichier :
     *     <pre>
     *         public static MazeConfig readFromFile(String filename) {
     *         // TODO
     *         }
     *     </pre>
     *  3. Ajout d'une méthode de lecture de fichier dans la classe {@link MazeConfig}
     *
     * @return
     */

    public static MazeConfig makeExample1() {
        return new MazeConfig(new Cell[][]{
                {nTee(DOT),    hPipe(DOT),     hPipe(DOT),     hPipe(DOT),     hPipe(DOT),     nTee(DOT)},
                {vPipe(DOT),    seVee(NOTHING), nTee(NOTHING),  nTee(NOTHING),  swVee(NOTHING), vPipe(DOT)},
                {vPipe(DOT),     wTee(NOTHING),  open(NOTHING),  open(NOTHING),  eTee(NOTHING),  vPipe(DOT)},
                {vPipe(DOT),    wTee(NOTHING),  open(NOTHING),  open(NOTHING),  eTee(NOTHING),  vPipe(DOT)},
                {vPipe(DOT),    neVee(NOTHING), sTee(NOTHING),  sTee(NOTHING),   nwVee(NOTHING), vPipe(DOT)},
                {neVee(DOT),    hPipe(DOT),     hPipe(DOT),     hPipe(DOT),     hPipe(DOT),     nwVee(DOT)}
        },
                new IntCoordinates(3, 0),
                new IntCoordinates(0, 3),
                new IntCoordinates(3, 5),
                new IntCoordinates(5, 5),
                new IntCoordinates(5, 1)
        );
    }

    public static MazeConfig makeExampleTxt() throws IOException {
        return txtToLab("../resources/testMap.txt");
    }
}
