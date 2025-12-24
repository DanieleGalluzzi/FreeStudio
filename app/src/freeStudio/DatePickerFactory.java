package freeStudio;

import org.jdatepicker.impl.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DatePickerFactory {

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd-MM-yyyy");

    public static JDatePickerImpl creaDatePicker() {

        UtilDateModel model = new UtilDateModel();
        model.setSelected(false);

        Properties p = new Properties();
        p.put("text.today", "Oggi");
        p.put("text.month", "Mese");
        p.put("text.year", "Anno");

        JDatePanelImpl panel = new JDatePanelImpl(model, p);

        JDatePickerImpl picker =
                new JDatePickerImpl(panel, new DateLabelFormatter());

        picker.setPreferredSize(FormStyle.CAMPO_STANDARD);

        // ✅ FIX DEFINITIVO: ascolta il PANEL (non il model)
        panel.addActionListener(e -> {
            Date d = (Date) model.getValue();
            if (d != null) {
                picker.getJFormattedTextField().setText(
                        FORMAT.format(d)
                );
            }
        });

        return picker;
    }


    // =============================
    // FORMATTER
    // =============================
    private static class DateLabelFormatter
            extends JFormattedTextField.AbstractFormatter {

        @Override
        public Object stringToValue(String text) {
            try {
                if (text == null || text.trim().isEmpty()) return null;
                return FORMAT.parse(text);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String valueToString(Object value) {
            if (value instanceof Date) {
                return FORMAT.format((Date) value);
            }
            return "";
        }
    }
}
