package tankgamepack.menus;

import tankgamepack.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndScreen extends JPanel {
    private static int winner;
    private final Launcher lf;

    public EndScreen(Launcher lf) {
        this.lf = lf;
        this.setLayout(new BorderLayout());
        JLabel winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Serif", Font.BOLD, 32));
        this.add(winnerLabel, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lf.setFrame("game");
            }
        });
        this.add(restartButton, BorderLayout.SOUTH);
    }

    public static void setWinner(int winnerId) {
        winner = winnerId;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String winnerText = winner == 1 ? "Red Tank Wins!" : "Blue Tank Wins!";
        JLabel winnerLabel = new JLabel(winnerText, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Serif", Font.BOLD, 32));
        this.add(winnerLabel, BorderLayout.CENTER);
    }
}
