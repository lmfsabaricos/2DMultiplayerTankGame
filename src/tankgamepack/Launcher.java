package tankgamepack;

import tankgamepack.Resources.ResourceManager;
import tankgamepack.game.GameWorld;
import tankgamepack.menus.EndGamePanel;
import tankgamepack.menus.StartMenuPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
/* TODO:
    - add resource manager and animation manager
    - initialize game already loads resources in,
        no need to reload them back in if the game is being restarted, they are already loaded
    - always use the interface for the left hand side. ex: Map<> sprites = new HashMap<>();
    - sound class wraps a clip - clip is a sound, clip runs on its own thread whenever made
    - all static assets such be done by monday

*/
public class Launcher {

    /*
     * Main panel in JFrame, the layout of this panel
     * will be card layout, this will allow us to switch
     * to sub-panels depending on game state.
     */
    private JPanel mainPanel;
    /*
     * game panel is used to show our game to the screen. inside this panel
     * also contains the game loop. This is where out objects are updated and
     * redrawn. This panel will execute its game loop on a separate thread.
     * This is to ensure responsiveness of the GUI. It is also a bad practice to
     * run long-running loops(or tasks) on Java Swing's main thread. This thread is
     * called the event dispatch thread.
     */
    private GameWorld gamePanel;
    /*
     * JFrame used to store our main panel. We will also attach all event
     * listeners to this JFrame.
     */
    private final JFrame jf;
    /*
     * CardLayout is used to manage our sub-panels. This is a layout manager
     * used for our game. It will be attached to the main panel.
     */
    private CardLayout cl;
    private Thread gameThread;

    public Launcher(){
        this.jf = new JFrame();             // creating a new JFrame object
        this.jf.setTitle("Tank Wars Game"); // setting the title of the JFrame window.
        // when the GUI is closed, this will also shut down the VM
        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminates the JVM (game closes fully) after closing window
    }

    private void initUIComponents(){ // set up screen and give control to start screen
        this.mainPanel = new JPanel(); // create a new main panel
        /*
         * start panel will be used to view the start menu. It will contain
         * two buttons start and exit.
         */
        JPanel startPanel = new StartMenuPanel(this); // create a new start panel
        this.gamePanel = new GameWorld(this); // create a new game panel
        this.gamePanel.InitializeGame(); // initialize game, but DO NOT start game
        // suggestion: maybe don't load resources inside initializeGame()
        /*
         * end panel is used to show the end game panel.  it will contain
         * two buttons restart and exit.
         */
        JPanel endPanel = new EndGamePanel(this); // create a new end game pane;
        cl = new CardLayout(); // creating a new CardLayout Panel
        this.mainPanel.setLayout(cl); // set the layout of the main panel to our card layout
        this.mainPanel.add(startPanel, "start"); //add the start panel to the main panel
        this.mainPanel.add(gamePanel, "game");   //add the game panel to the main panel
        this.mainPanel.add(endPanel, "end");    // add the end game panel to the main panel
        this.jf.add(mainPanel); // add the main panel to the JFrame
        this.jf.setResizable(false); //make the JFrame not resizable
        this.setFrame("start"); // set the current panel to start panel
    }

    public void setFrame(String type){
        this.jf.setVisible(false); // hide the JFrame
        switch (type) {
            case "start" ->
                // set the size of the jFrame to the expected size for the start panel
                    this.jf.setSize(GameConstants.START_MENU_SCREEN_WIDTH, GameConstants.START_MENU_SCREEN_HEIGHT);
            case "game" -> {
                // set the size of the jFrame to the expected size for the game panel
                this.jf.setSize(GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
                //start a new thread for the game to run. This will ensure our JFrame is responsive and
                // not stuck executing the game loop.
                gameThread = new Thread(this.gamePanel);
                //(new Thread(this.gamePanel)).start(); // starts game on new thread, preventing it from running on UI thread
                gameThread.start();


            }
            case "end" ->
                // set the size of the jFrame to the expected size for the end panel
                    this.jf.setSize(GameConstants.END_MENU_SCREEN_WIDTH, GameConstants.END_MENU_SCREEN_HEIGHT);
        }
        this.cl.show(mainPanel, type); // change current panel shown on main panel to the panel denoted by type.
        this.jf.setVisible(true); // show the JFrame
    }

    public JFrame getJf() {
        return jf;
    }

    public void closeGame(){
        this.jf.dispatchEvent(new WindowEvent(this.jf, WindowEvent.WINDOW_CLOSING));
    }

    public void killGame () {
        gameThread.interrupt();
    }
    public static void main(String[] args) {
        ResourceManager.loadResources();
        (new Launcher()).initUIComponents();
    }
}