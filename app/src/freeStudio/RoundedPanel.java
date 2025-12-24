package freeStudio;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    private int raggio;

    public RoundedPanel(int raggio) {
        this.raggio = raggio;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // sfondo
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), raggio, raggio);

        // ⭐ BORDO NERO PIENO, SPESSO 2PX
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, raggio, raggio);

        g2.dispose();
    }
}
