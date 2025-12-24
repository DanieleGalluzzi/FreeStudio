package freeStudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.prefs.Preferences;


public class Login extends JPanel {

    private JLabel lblBenvenuto;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JButton btnAccedi;
    private JCheckBox checkMostraPassword;
    private JCheckBox checkRicordami;

    private Interfaccia interfaccia;

    public Login(Interfaccia interfaccia) {
        this.interfaccia = interfaccia;
        inizializzaComponenti();
        caricaCredenzialiSalvate();

    }

    private void inizializzaComponenti() {

        setLayout(new GridBagLayout()); // CENTRA TUTTO
        GridBagConstraints gbc = new GridBagConstraints();

        // ============================
        // TITOLO
        // ============================
        lblBenvenuto = new JLabel("Benvenuto in FreeStudio");
        lblBenvenuto.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBenvenuto.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(lblBenvenuto, gbc);

        // ============================
        // ROUNDED PANEL (CORRETTO)
        // ============================
        RoundedPanel pnlLogin = new RoundedPanel(20);
        pnlLogin.setBackground(new Color(245, 245, 245));

        // ⭐ DIMENSIONE NECESSARIA PER RENDERLO VISIBILE
        pnlLogin.setPreferredSize(new Dimension(380, 240));

        pnlLogin.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        // USERNAME
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(15);

        g.gridx = 0; g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        pnlLogin.add(lblUsername, g);

        g.gridx = 1;
        pnlLogin.add(txtUsername, g);

        // PASSWORD
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(15);

        g.gridx = 0; g.gridy = 1;
        pnlLogin.add(lblPassword, g);

        g.gridx = 1;
        pnlLogin.add(txtPassword, g);

        // MOSTRA PASSWORD
        checkMostraPassword = new JCheckBox("Mostra Password");
        checkMostraPassword.addActionListener(e ->
                txtPassword.setEchoChar(checkMostraPassword.isSelected() ? (char) 0 : '•')
        );

        g.gridx = 1; g.gridy = 2;
        pnlLogin.add(checkMostraPassword, g);

        // RICORDAMI
        checkRicordami = new JCheckBox("Ricordami");

        g.gridy = 3;
        pnlLogin.add(checkRicordami, g);

        // BOTTONE ACCEDI
        btnAccedi = new JButton("Accedi");
        btnAccedi.setBackground(new Color(0, 91, 150));
        btnAccedi.setForeground(Color.WHITE);
        btnAccedi.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnAccedi.addActionListener(e -> effettuaLogin());

        g.gridy = 4;
        g.anchor = GridBagConstraints.CENTER;
        g.fill = GridBagConstraints.NONE;
        pnlLogin.add(btnAccedi, g);

        // AGGIUNTA AL PANNELLO PRINCIPALE (CENTRATO)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnlLogin, gbc);
    }

    // ===========================================================
    // LOGICA LOGIN
    // ===========================================================
    private void effettuaLogin() {

        String user = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (user.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Inserisci username e password.",
                    "Campi obbligatori",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            // 1️⃣ Connessione SOLO al server MySQL
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/?serverTimezone=Europe/Rome",
                    user,
                    password
            );
            conn.close();

            // 2️⃣ Passo le credenziali all’interfaccia
            interfaccia.creaConnessione(user, password);
            salvaCredenziali(user);

            JOptionPane.showMessageDialog(this, "Accesso riuscito!");
            
            interfaccia.loginEffettuato();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Credenziali errate o impossibile connettersi al server MySQL.",
                    "Errore di accesso",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    private void caricaCredenzialiSalvate() {

        Preferences prefs = Preferences.userNodeForPackage(Login.class);

        String ultimoUser = prefs.get("lastUser", "");

        if (!ultimoUser.isEmpty()) {
            txtUsername.setText(ultimoUser);
            checkRicordami.setSelected(true);
        }
    }

    private void salvaCredenziali(String user) {

        Preferences prefs = Preferences.userNodeForPackage(Login.class);

        if (checkRicordami.isSelected()) {
            prefs.put("lastUser", user);
        } else {
            prefs.remove("lastUser");
        }
    }

    
    // ===========================================================
    // ROUNDED PANEL
    // ===========================================================
    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}
