package gui;

/**
 * Cette classe est le point d'entrée de l'application.
 * Elle crée une fenêtre avec un labyrinthe et un Pacman.
 * Elle crée aussi un listener d'événements clavier pour le Pacman.
 * Elle évite la méthode start() qui est appelée automatiquement par JavaFX.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import config.MazeConfig;
import javafx.stage.StageStyle;
import model.MazeState;

import java.io.IOException;

public class App extends Application {
    @Override
    /**
     * Crée une fenêtre avec un labyrinthe et un Pacman.
     * @param primaryStage la fenêtre principale
     *                     (voir https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html)
     *                     pour la documentation de la classe Stage)
     *                     (voir https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Scene.html)
     *                     pour la documentation de la classe Scene)
     *                     (voir https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/Pane.html)
     *                     pour la documentation de la classe Pane)
     */
    public void start(Stage primaryStage) throws IOException {
        // Pane est un conteneur qui peut contenir des éléments graphiques
        // root est le conteneur principal du jeu (il contient tous les autres conteneurs)
        var root = new BorderPane();
        // gamePane est le conteneur de l'écran de jeu
        var gamePane = new Pane();
        // Scene est un objet qui contient tous les éléments graphiques (ça correspond à la fenêtre qui sera affichée)
        Scene gameScene = new Scene(root);
        if (!MazeConfig.isGameComplete()) { TF2Complete(); }

        // PacmanController est un listener d'événements clavier (ça récupère les touches promptées par l'user)
        PacmanController pacmanController = new PacmanController();

        /** gameScene est un objet de type Scene
         *
         *  setOnKeyPressed() et setOnKeyReleased() sont des méthodes de Scene.
         *  Elles permettent de définir des actions à effectuer quand une touche est pressée ou relâchée.
         *
         *
         *  Elles prennent en paramètre un objet de type EventHandler<KeyEvent>.
         *  PacmanController implémente EventHandler<KeyEvent>, donc on peut lui passer en paramètre.
         *  C'est ce qu'on fait ici.
         *  C'est un peu comme si on faisait :
         *
         *  gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         *    @Override
         *    public void handle(KeyEvent event) {
         *    pacmanController.keyPressedHandler(event);
         *    }
         *    });
         *    gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
         *    @Override
         *    public void handle(KeyEvent event) {
         *    pacmanController.keyReleasedHandler(event);
         *    }
         *    });
         *    C'est juste plus court.
         *
         * */

        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        MazeState maze = new MazeState(MazeConfig.makeGenericExample(1));

        //Récupère la taille de l'écran
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        //Adapte la taille de la fenêtre de la partie (sans le hud) en fonction du nombre de lignes et de colonnes, ainsi que de la taille de l'écran
        double widthScale = Math.floor(screenBounds.getWidth() / maze.getWidth());///10.0;
        double heightScale = Math.floor(screenBounds.getHeight() / maze.getHeight());///10.0;

        double scale = Math.min(/*(int)*/widthScale, /*(int)*/heightScale);// * 10.0 - 5;

        //Conteneur de tout ce qui est la vue du jeu (jeu, menu de pause, etc)
        StackPane gameComponents = new StackPane();

        //Vue de la partie (sans le hud)
        var gameView = new GameView(maze, gamePane, 0.8*scale);

        gameComponents.getChildren().add(gamePane);
        StackPane.setAlignment(gamePane,Pos.CENTER);

        //--HUD--
        Pane hudPane = new Pane();
        HUDView hudView = new HUDView(maze, hudPane, maze.getWidth() * 0.8 * scale, maze.getHeight() * 0.8 * scale * 0.25, scale); //Dégueu, il faut rendre ça plus dynamique.
        gameView.getGraphicsUpdaters().add(hudView.getHudUpdater());

        root.setCenter(gameComponents);
        root.setBottom(hudPane);

        var animationController = new AnimationController(gameView.getGraphicsUpdaters(), gameView.getMaze(), primaryStage, gameView, gameComponents, 0.8*scale);
        animationController.mainTheme();
        pacmanController.setAnimationController(animationController);
        maze.setAnimationController(animationController);

        //Empeche de resize la fenetre
        primaryStage.setResizable(false);

        //Permet d'enlever la barre du haut
        primaryStage.initStyle(StageStyle.UNDECORATED);


        var mainMenu = new MainMenu();
        var optionsMenu = new OptionsMenu();
        primaryStage.setScene(mainMenu.startMenu(primaryStage, gameScene, animationController, optionsMenu));
        primaryStage.show();
        //primaryStage.setMaximized(true);
    }

    private void TF2Complete() {
        System.out.println("Erreur de compilation, fichiers manquants...");
        System.exit(42);
    }

    public static void restartApplication(Stage stage) {
        try {
            // Fermer le stage actuel
            stage.close();

            // Lancer une nouvelle instance de l'application
            Platform.runLater(() -> {
                try {
                    new App().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
