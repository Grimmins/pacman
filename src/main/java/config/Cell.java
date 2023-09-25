package config;

// La classe Cell est déclarée comme un enregistrement (record) qui est une nouveauté de Java 14.

// tutur : ici essentiellement on a la liste des cellules :  le nom donne la forme et le content donne ce qui se trouve dedans (soit un
// point, soit un energizer (le super boost), soit du vide)

public record Cell(boolean northWall, boolean eastWall, boolean southWall, boolean westWall, Cell.Content initialContent) {


    public enum Content { NOTHING, DOT, ENERGIZER }

    /** TO DO : Modification avec une suele méthode de création 'create" qui prend comme paramètres les valeurs des murs dans chaque direction/
     * ainsi que le contenu initial de la cellule. Les autres méthodes de création sont supprimées.
     *
     */

    // open cells
    public static Cell open(Content c) { return new Cell(false, false, false, false, c); }
    // --------- tutur : PROBLEME : la cellule closed devrait être true true true true : A CONFIRMER LES POTES ---------
    public static Cell closed(Content c) { return new Cell(true, true, true, true, c); }
    // straight pipes
    public static Cell hPipe(Content c) { return new Cell(true, false, true, false, c); }
    public static Cell vPipe(Content c) { return new Cell(false, true, false, true, c); }
    // corner cells
    public static Cell swVee(Content c) { return new Cell(true, true, false, false, c); }
    public static Cell nwVee(Content c) { return new Cell(false, true, true, false, c); }
    public static Cell neVee(Content c) { return new Cell(false, false, true, true, c); }
    public static Cell seVee(Content c) { return new Cell(true, false, false, true, c); }
    // T-shaped cells
    public static Cell nU(Content c) { return new Cell(false, true, true, true, c); }
    public static Cell eU(Content c) { return new Cell(true, false, true, true, c); }
    public static Cell sU(Content c) { return new Cell(true, true, false, true, c); }
    public static Cell wU(Content c) { return new Cell(true, true, true, false, c); }
    // U-shaped cells
    public static Cell nTee(Content c) { return new Cell(true, false, false, false, c); }
    public static Cell eTee(Content c) { return new Cell(false, true, false, false, c); }
    public static Cell sTee(Content c) { return new Cell(false, false, true, false, c); }
    public static Cell wTee(Content c) { return new Cell(false, false, false, true, c); }

}
