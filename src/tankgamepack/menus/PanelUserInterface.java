package tankgamepack.menus;

import javax.swing.*;
import java.awt.*;

public class PanelUserInterface {

    public static JButton formatButton (JButton button, int positionX, int positionY) {
        button.setFont(new Font("Courier New", Font.BOLD, 24));
        button.setBounds(positionX, positionY, 150, 50);

        return button;
    }


}
