import org.junit.jupiter.api.Test;

import gui.CritterGraphicsFactory;
import model.Direction;
import model.Ghost;
import model.PacMan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class CritterGraphicsFactoryTest{
    
    @Test
    public void setimgPacmanTest(){
        for(double i = 50; i <300;i+=0.5){
            CritterGraphicsFactory facto = new CritterGraphicsFactory(i);
            PacMan pacman = new PacMan();

            String[] etat = {"ferme","ouvert"};
            for(String e : etat){
                facto.setEtatPacman(e);
                pacman.setDirection(Direction.NORTH);
                assertEquals(facto.setimgPacman(pacman),"pacman/pacman-haut-"+e+".png");
                pacman.setDirection(Direction.SOUTH);
                assertEquals(facto.setimgPacman(pacman),"pacman/pacman-bas-"+e+".png");
                pacman.setDirection(Direction.WEST);
                assertEquals(facto.setimgPacman(pacman),"pacman/pacman-gauche-"+e+".png");
                pacman.setDirection(Direction.EAST);
                assertEquals(facto.setimgPacman(pacman),"pacman/pacman-droite-"+e+".png");
                pacman.setDirection(Direction.NONE);
                assertEquals(facto.setimgPacman(pacman),"pacman/pacman-rond.png");
            }
        }    
    }

    @Test 
    public void setimgghostTest(){
        List<Ghost> critters = List.of(Ghost.CLYDE, Ghost.BLINKY, Ghost.INKY, Ghost.PINKY);
        for(double i = 50; i <300;i+=0.5){
            CritterGraphicsFactory facto = new CritterGraphicsFactory(i);
            
            for (Ghost critter: critters){
                critter.setDirection(Direction.NONE);
                int numGhost = facto.getnumghost(critter);
            
            for(int j =0; j<2; j++){
                facto.setEtatghost(j, numGhost);
                critter.setDirection(Direction.NORTH);
                assertEquals(facto.setimgghost(critter,numGhost,facto.setimgghostNE(critter)),facto.setimgghostNEb(critter)+"haut"+j+".png");
                critter.setDirection(Direction.SOUTH);
                assertEquals(facto.setimgghost(critter,numGhost,facto.setimgghostNE(critter)),facto.setimgghostNEb(critter)+"bas"+j+".png");
                critter.setDirection(Direction.WEST);
                assertEquals(facto.setimgghost(critter,numGhost,facto.setimgghostNE(critter)),facto.setimgghostNEb(critter)+"gauche"+j+".png");
                critter.setDirection(Direction.EAST);
                assertEquals(facto.setimgghost(critter,numGhost,facto.setimgghostNE(critter)),facto.setimgghostNEb(critter)+"droite"+j+".png");
                critter.setDirection(Direction.NONE);
                assertEquals(facto.setimgghost(critter,numGhost,facto.setimgghostNE(critter)),facto.setimgghostNEb(critter)+"droite"+j+".png");
            }
        }    
    }
    }
}
