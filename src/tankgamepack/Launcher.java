package tankgamepack;

import tankgamepack.Resources.ResourceManager;
import tankgamepack.game.GameWorld;
import tankgamepack.menus.EndGamePanel;
import tankgamepack.menus.StartMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class Launcher {
    private JPanel mainPanel;
    private GameWorld gamePanel;
    private final JFrame jf;
    private CardLayout cl;
    private Thread gameThread;
    private boolean isFullScreen = false;
    private final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

    public Launcher() {
        this.jf = new JFrame();
        this.jf.setTitle("Tank Wars Game");
        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initUIComponents() {
        this.mainPanel = new JPanel();
        JPanel startPanel = new StartMenuPanel(this);
        this.gamePanel = new GameWorld(this);
        this.gamePanel.InitializeGame();
        JPanel endPanel = new EndGamePanel(this);
        cl = new CardLayout();
        this.mainPanel.setLayout(cl);
        this.mainPanel.add(startPanel, "start");
        this.mainPanel.add(gamePanel, "game");
        this.mainPanel.add(endPanel, "end");
        this.jf.add(mainPanel);
        this.jf.setResizable(true);
        this.setFrame("start");

        addFullScreenToggle();
    }

    private void addFullScreenToggle() {
        Action toggleFullScreenAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        };
        KeyStroke f11KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
        jf.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f11KeyStroke, "TOGGLE_FULL_SCREEN");
        jf.getRootPane().getActionMap().put("TOGGLE_FULL_SCREEN", toggleFullScreenAction);
    }

    public void setFrame(String type) {
        this.jf.setVisible(false);
        switch (type) {
            case "start" -> this.jf.setSize(GameConstants.START_MENU_SCREEN_WIDTH, GameConstants.START_MENU_SCREEN_HEIGHT);
            case "game" -> {
                this.jf.setSize(GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
                gameThread = new Thread(this.gamePanel);
                gameThread.start();
            }
            case "end" -> this.jf.setSize(GameConstants.END_MENU_SCREEN_WIDTH, GameConstants.END_MENU_SCREEN_HEIGHT);
        }
        this.cl.show(mainPanel, type);
        this.jf.setVisible(true);
    }

    private void toggleFullScreen() {
        if (isFullScreen) {
            this.jf.dispose();
            this.jf.setUndecorated(false);
            device.setFullScreenWindow(null);
            this.jf.setVisible(true);
            this.isFullScreen = false;
        } else {
            this.jf.dispose();
            this.jf.setUndecorated(true);
            device.setFullScreenWindow(this.jf);
            this.jf.setVisible(true);
            this.isFullScreen = true;
        }
    }

    public JFrame getJf() {
        return jf;
    }

    public void closeGame() {
        this.jf.dispatchEvent(new WindowEvent(this.jf, WindowEvent.WINDOW_CLOSING));
    }

    public void killGame() {
        gameThread.interrupt();
    }

    public void setWinner(int winnerId) {
        String winner = winnerId == 1 ? "Red Tank" : "Blue Tank";
        EndGamePanel.setWinner(winner);
        setFrame("end");
    }

    public void restartGame() {
        gamePanel.requestReset();
        setFrame("game");
    }

    public static void main(String[] args) {
        ResourceManager.loadResources();
        (new Launcher()).initUIComponents();
    }
}
