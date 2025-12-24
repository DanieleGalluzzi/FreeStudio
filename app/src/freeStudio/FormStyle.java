package freeStudio;

import javax.swing.*;
import java.awt.*;

public class FormStyle {

    public static final Dimension CAMPO_STANDARD = new Dimension(220, 28);
    public static final Dimension AREA_STANDARD  = new Dimension(220, 60);

    public static void textField(JTextField campo) {
        campo.setPreferredSize(CAMPO_STANDARD);
    }

    public static void comboBox(JComboBox<?> combo) {
        combo.setPreferredSize(CAMPO_STANDARD);
    }

    public static JScrollPane textArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(AREA_STANDARD);
        return scroll;
    }

    public static void labelValue(JLabel lbl) {
        lbl.setPreferredSize(CAMPO_STANDARD);
    }
}
