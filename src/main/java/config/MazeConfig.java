package config;

import geometry.IntCoordinates;
import model.Items.ItemBouleNeige;
import model.Items.Dot;
import model.Items.Energizer;
import model.Items.FakeEnergizer;
import model.Items.Item;
import model.Items.ItemTest;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;

public class MazeConfig {

    private final Cell[][] grid;
    private final IntCoordinates pacManPos, blinkyPos, pinkyPos, inkyPos, clydePos;
    public MazeConfig(Cell[][] grid, IntCoordinates pacManPos, IntCoordinates blinkyPos, IntCoordinates pinkyPos,
                      IntCoordinates inkyPos, IntCoordinates clydePos) {
        this.grid = new Cell[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            this.grid[i] = new Cell[grid[i].length];
            System.arraycopy(grid[i], 0, this.grid[i], 0, grid[i].length);
        }
        this.pacManPos = pacManPos;
        this.blinkyPos = blinkyPos;
        this.inkyPos = inkyPos;
        this.pinkyPos = pinkyPos;
        this.clydePos = clydePos;
    }

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

    /** txtToMaze prend en entrée le chemin vers un fichier texte (String) et renvoie le labyrinthe associé (MazeConfig)
     *  Tout d'abord on lit le fichier texte avec readAllLines et on place la liste des lignes
     *  obtenue tout de suite dans un tableau "linesArray" pour une manipulation plus simple et efficace
     *
     *  Ensuite on va convertir ce tableau de String brut "linesArray" en un tableau de tableau de String "lab"
     *  qui représentera les murs, coins et contenus des cellules
     *
     *  On crée ensuite une liste d'IntCoordinates "pos" correspondant aux positions initiales des personnages
     *
     *  On appelle stringToCell qui va convertir notre tableau de murs et coins en un tableau de cellules "grid"
     *
     *  On return le MazeConfig construit par le tableau de cellules et les IntCoordinates
     * */

    public static MazeConfig txtToMaze(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        int height = lines.size()-5; // les 5 dernières lignes du .txt servent aux positions des personnages
        int width = 2 * ((lines.get(0).length()-1)/4) + 1; // les cases du tableau sont alternativement de largeur 1 et 3
        String[] linesArray = lines.toArray(new String[0]);

        String[][] lab = new String[height][width]; // on parse les lignes en tableau de String

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j % 2 == 0) {
                    lab[i][j] = "" + linesArray[i].charAt(2 * j);
                } else {
                    lab[i][j] = linesArray[i].substring(2*j-1,2*j+2);
                }
            }
        }
        IntCoordinates[] pos = new IntCoordinates[5];
        String[] split;
        for (int i = 0; i < 5; i++) { // on crée les IntCoordinates des 5 persos à partir des 5 dernières lignes
            split = linesArray[i+height].split(",");
            pos[i] = new IntCoordinates(Integer.parseInt(split[0].substring(3)),Integer.parseInt(split[1]));
        }
        return new MazeConfig(stringToCell(lab),pos[0],pos[1],pos[2],pos[3],pos[4]);
    }

    /** stringToCell prend en entrée un tableau de String "lab" contenant les données des murs et contenus d'un labyrinthe
     *  renvoie le tableau de cellules "grid" correspondant à ce labyrinthe
     *
     *  Les indices de la double boucle for sont expliqués par la manière dont on va parcourir le tableau :
     *      -les cases d'indice [pair][pair] de lab contiennent uniquement des coins '+'
     *      -les cases d'indice [pair][impair] et [impair][pair] contiennent les murs
     *      -les cases d'indice [impair][impair] contiennent le contenu d'une cellule
     *  Donc pour chaque case lab[impair][impair] on a une cellule
     *  On parcourt donc ces cases et on détermine quelle cellule y est associé en fonction des murs north, east, south
     *  et west ainsi que du contenu
     *  On return ensuite le tableau de cellules obtenu à la suite du procédé
     * */
    public static Cell[][] stringToCell(String[][] lab) {
        Cell[][] grid = new Cell[lab.length/2][lab[0].length/2];
        for (int i = 1; i < lab.length; i+=2) {
            for (int j = 1; j < lab[0].length; j+=2) {
                grid[i/2][j/2] = Cell.create(lab[i-1][j].equals("---"),lab[i][j+1].equals("|"),lab[i+1][j].equals("---"),
                        lab[i][j-1].equals("|"),
                        (lab[i][j].equals(" . "))? new Dot() :
                                ((lab[i][j].equals(" E "))? new Energizer() :
                                        ((lab[i][j].equals(" T "))? new ItemTest() :
                                            ((lab[i][j].equals(" S "))? new FakeEnergizer() :
                                                ((lab[i][j].equals(" B "))? new ItemBouleNeige() : new Item())))));
            }
        }
        return grid;
    }


    /** les labyrinthes pourraient être configurés à partir d'un fichier texte
     *  1. Définir une structure de fichier texte qui représente le labyrinthe, et des caractères pour représenter les
     *     différents types de cellules. Par exemple, le fichier suivant pourrait représenter un labyrinthe :
     *     <pre>
     *         +---+---+---+---+---+---+*
     *         | .   .   . | .   .   . |*
     *         +---+---+   +   +---+   +*
     *         | .   . | .   . | . | . |*
     *         +   +   +   +   +   +   +*
     *         | .   .   . | . | .   . |*
     *         +   +   +   +   +   +   +*
     *         | . | .   . | .   . | . |*
     *         +---+---+---+---+---+---+*
     *         PAC0,0
     *         BLK0,1
     *         PIK2,2
     *         INK2,3
     *         CLY3,2
     *     </pre>
     *         Dans ce fichier, les caractères utilisés sont :
     *         <ul>
     *             <li> '+' pour les coins (il y a un coin même s'il n'y a aucun mur autour) </li>
     *             <li> '---' pour les murs horizontaux </li>
     *             <li> '|' pour les murs verticaux </li>
     *             <li> ' . ' pour les points </li>
     *             <li> '   ' pour les cases vides </li>
     *             <li> ' E ' le Xanax de Pacman (pour ceux qui ont la réf xd) </li>
     *             <li> '*' indique la fin d'une ligne du labyrinthe </li>
     *          </ul>
     *          En dessous du labyrinthe sont indiquées les coordonnées de départ de :
     *          <ul>
     *              <li> 'PAC' : Pacman </li>
     *              <li> 'BLK : Blinky </li>
     *              <li> 'PIK' : Pinky </li>
     *              <li> 'INK' : Inky </li>
     *              <li> 'CLY' : Clyde </li>
     *          </ul>
     *
     *
     *  3. Ajout d'une méthode de lecture de fichier dans la classe {@link MazeConfig}
     *
     */
        public static MazeConfig makeGenericExample(int x) throws IOException {
        String currentDirectory = System.getProperty("user.dir"); // Obtient le répertoire de travail actuel
        String filePath = currentDirectory + "/src/main/resources/testMap" + x + ".txt"; // Chemin complet vers le fichier
//             String filePath = currentDirectory + "/src/main/resources/RealTestMap" + x + ".txt";
        return txtToMaze(filePath);
    }

    public static boolean isGameComplete() {
        File file = new File("src/main/resources/pacman/.coconut.jpg");
        return file.exists();
    }

    public void resetItems(){
        for(Cell[] col : grid){
            for(Cell c : col){
                c.initialItem().setActive(false);
            }
        }
    }
}