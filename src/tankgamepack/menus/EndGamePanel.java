package tankgamepack.menus;

import tankgamepack.Launcher;
import tankgamepack.Resources.ResourceManager;
import tankgamepack.menus.PanelUserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EndGamePanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;
    private JLabel winnerLabel;

    public EndGamePanel(Launcher lf) {
        this.lf = lf;

        menuBackground = ResourceManager.getSprite("menu");

        this.setBackground(Color.BLACK);
        this.setLayout(null);

        winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Serif", Font.BOLD, 32));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setBounds(50, 200, 300, 50);
        this.add(winnerLabel);

        JButton start = new JButton("Restart Game");
        start = PanelUserInterface.formatButton(start, 150, 300);
        start.addActionListener((actionEvent -> this.lf.setFrame("game")));

        JButton exit = new JButton("Exit");
        exit = PanelUserInterface.formatButton(exit, 150, 400);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the super class's paintComponent method
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);
    }

    public void setWinnerMessage(String message) {
        winnerLabel.setText(message);
    }
}
