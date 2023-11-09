import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import model.*;
import java.util.List;

public class GhostTest {

    @Test
    public void isCenteredTest(){
        List<Ghost> critters = List.of(Ghost.CLYDE, Ghost.BLINKY, Ghost.INKY, Ghost.PINKY);
        for (Ghost critter: critters){
            if (Math.round(critter.getPos().x()) != critter.getPos().x() ||
                    Math.round(critter.getPos().y()) != critter.getPos().y()) {

            }
        }
    }
}
