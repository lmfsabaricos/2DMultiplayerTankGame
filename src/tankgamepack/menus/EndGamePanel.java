package tankgamepack.menus;

import tankgamepack.Launcher;
import tankgamepack.Resources.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EndGamePanel extends JPanel {

    private BufferedImage menuBackground;
    private static String winnerText = "";
    private final Launcher lf;

    public EndGamePanel(Launcher lf) {
        this.lf = lf;
        this.menuBackground = ResourceManager.getSprite("menu");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JLabel winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Serif", Font.BOLD, 32));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setBounds(100, 200, 600, 50);
        this.add(winnerLabel);

        JButton restartButton = new JButton("Restart Game");
        restartButton.setBounds(100, 300, 200, 50);
        restartButton.addActionListener(e -> lf.restartGame());
        this.add(restartButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(100, 400, 200, 50);
        exitButton.addActionListener(e -> lf.closeGame());
        this.add(exitButton);
    }

    public static void setWinner(String winner) {
        winnerText = winner + " Wins!";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(menuBackground, 0, 0, this.getWidth(), this.getHeight(), null);
        g.setFont(new Font("Serif", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        g.drawString("Game Over", 100, 250); // Adjusted position
        g.drawString(winnerText, 100, 300); // Adjusted position
    }
}
