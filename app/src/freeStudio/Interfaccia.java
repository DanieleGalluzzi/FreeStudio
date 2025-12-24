package freeStudio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.net.URL;
import javax.swing.ImageIcon;




public class Interfaccia extends JFrame {

    // ============================================================
    // ========================== CAMPI ============================
    // ============================================================

    private JPanel pnlSinistro;     // Colonna blu
    private JPanel pnlMenu;         // Bottoni sinistra
    private JPanel pnlDestro;       // Area centrale

    private JButton btnHome;
    private JButton btnClienti;
    private JButton btnProgetti;
    private JButton btnFatture;
    private JButton btnPagamenti;
    private JButton btnAttivita;
    private JButton btnDashboard;
    private JButton btnLogout;

    private CardLayout cardLayout;


    private DatabaseManager dbManager;

    // Pagine
    private Login loginPage;
    private Home homePage;
    private Clienti clientiPage;
    private Progetti progettiPage;
    private Fatture fatturePage;
    private Pagamenti pagamentiPage;
    private Attivita attivitaPage;
    private Dashboard dashboardPage;

    // ============================================================
    // ======================== COSTRUTTORE ========================
    // ============================================================

    public Interfaccia() {

        setTitle("FreeStudio - Servizi digitali e consulenziali");      
       
        URL iconUrl = ClassLoader.getSystemResource("icon.png");
        System.out.println("ICON URL = " + iconUrl);

        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }


        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (dbManager != null) dbManager.chiudiConnessione();
            }
        });

        
        inizializzaPannelloSinistro();
        inizializzaPannelloDestro();
        inizializzaListenerMenu();

        // Prima del login
        pnlMenu.setVisible(false);     // I BOTTONI NON SI VEDONO
        pnlSinistro.setVisible(true);  // La colonna blu SI VEDE ma è vuota

        cardLayout.show(pnlDestro, "login");
    }

    

    // ============================================================
    // ====================== PANNELLO SINISTRO ====================
    // ============================================================

    private void inizializzaPannelloSinistro() {

        pnlSinistro = new JPanel(new BorderLayout());
        pnlSinistro.setPreferredSize(new Dimension(180, 700));
        pnlSinistro.setBackground(new Color(30, 30, 60));
        add(pnlSinistro, BorderLayout.WEST);
        
     // Spacer superiore per allineamento con il frame
        JPanel pnlTopLeftSpacer = new JPanel();
        pnlTopLeftSpacer.setPreferredSize(new Dimension(180, 30)); // stessa altezza del top spacer
        pnlTopLeftSpacer.setBackground(new Color(30, 30, 60));     // stesso blu della sidebar

        pnlSinistro.add(pnlTopLeftSpacer, BorderLayout.NORTH);


        // Questo pannello conterrà i bottoni, ma inizialmente è nascosto
        pnlMenu = new JPanel(new GridLayout(12, 1, 0, 10));
        pnlMenu.setBackground(new Color(30, 30, 60));

     // Spacer sopra Home
        JPanel pnlMenuTopGap = new JPanel();
        pnlMenuTopGap.setPreferredSize(new Dimension(180, 10));
        pnlMenuTopGap.setBackground(new Color(30, 30, 60));

        pnlMenu.add(pnlMenuTopGap);
        
        btnHome = creaBottoneMenu("Home");
        btnClienti = creaBottoneMenu("Clienti");
        btnProgetti = creaBottoneMenu("Progetti");
        btnFatture = creaBottoneMenu("Fatture");
        btnPagamenti = creaBottoneMenu("Pagamenti");
        btnAttivita = creaBottoneMenu("Attività");
        btnDashboard = creaBottoneMenu("Dashboard");
        btnLogout = creaBottoneMenu("Logout");

        pnlMenu.add(btnHome);
        pnlMenu.add(btnClienti);
        pnlMenu.add(btnProgetti);
        pnlMenu.add(btnFatture);
        pnlMenu.add(btnPagamenti);
        pnlMenu.add(btnAttivita);
        pnlMenu.add(btnDashboard);
        pnlMenu.add(new JLabel(""));
        pnlMenu.add(btnLogout);

        pnlSinistro.add(pnlMenu, BorderLayout.NORTH);
    }

    // ============================================================
    // ====================== PANNELLO DESTRO ======================
    // ============================================================

    private void inizializzaPannelloDestro() {

        pnlDestro = new JPanel();
        cardLayout = new CardLayout();
        pnlDestro.setLayout(cardLayout);
        add(pnlDestro, BorderLayout.CENTER);

        loginPage = new Login(this);

        pnlDestro.add(loginPage, "login");
    }

    // ============================================================
    // ==================== LISTENER MENU ==========================
    // ============================================================

    private void inizializzaListenerMenu() {

    	btnHome.addActionListener(e -> {
    	    homePage = new Home(this); 
    	    pnlDestro.add(homePage, "home");
    	    cardLayout.show(pnlDestro, "home");
    	});
        btnClienti.addActionListener(e -> cardLayout.show(pnlDestro, "clienti"));
        btnProgetti.addActionListener(e -> cardLayout.show(pnlDestro, "progetti"));
        btnFatture.addActionListener(e -> cardLayout.show(pnlDestro, "fatture"));
        btnPagamenti.addActionListener(e -> cardLayout.show(pnlDestro, "pagamenti"));
        btnAttivita.addActionListener(e -> {
            cardLayout.show(pnlDestro, "attivita");
            attivitaPage.aggiornaAttivita();
        });

        btnDashboard.addActionListener(e -> {
            cardLayout.show(pnlDestro, "dashboard");
            dashboardPage.aggiornaDashboard();
        });


        btnLogout.addActionListener(e -> effettuaLogout());
    }

    // ============================================================
    // ==================== CREAZIONE BOTTONI ======================
    // ============================================================

    private JButton creaBottoneMenu(String testo) {

        JButton btn = new JButton(testo);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(40, 40, 80));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 120));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 40, 80));
            }
        });

        return btn;
    }

    // ============================================================
    // ========================== LOGIN ============================
    // ============================================================

    public void creaConnessione(String user, String password) {
        this.dbManager = new DatabaseManager(user, password);
        this.dbManager.connetti(user, password);
    }

    // Dopo login → carichiamo le pagine
    public void loginEffettuato() {

    	
        clientiPage = new Clienti(this);
        progettiPage = new Progetti(this);
        fatturePage = new Fatture(this);
        pagamentiPage = new Pagamenti(this);
        attivitaPage = new Attivita(this);
        dashboardPage = new Dashboard(this, fatturePage, progettiPage);
        
        homePage = new Home(this);

        pnlDestro.add(homePage, "home");
        pnlDestro.add(clientiPage, "clienti");
        pnlDestro.add(progettiPage, "progetti");
        pnlDestro.add(fatturePage, "fatture");
        pnlDestro.add(pagamentiPage, "pagamenti");
        pnlDestro.add(attivitaPage, "attivita");
        pnlDestro.add(dashboardPage, "dashboard");

        pnlMenu.setVisible(true);  // ORA I BOTTONI SI VEDONO

       

        cardLayout.show(pnlDestro, "home");
    }

    // ============================================================
    // ========================== LOGOUT ===========================
    // ============================================================

    private void effettuaLogout() {

        pnlMenu.setVisible(false);

        cardLayout.show(pnlDestro, "login");
    }

    // ============================================================
    // ========================= DB MANAGER ========================
    // ============================================================

    public DatabaseManager getDbManager() {
        return dbManager;
    }
    
    public void aggiornaFatture() {
        if (fatturePage != null) {
            fatturePage.aggiornaTabella();
        }
    }

    // ============================================================
    // ======================= AGGIORNAMENTI =======================
    // ============================================================

    public void mostra(String pagina) {
        cardLayout.show(pnlDestro, pagina);
    }

    
    public void aggiornaHome() {

        if (homePage != null) {
            homePage = new Home(this);
            pnlDestro.add(homePage, "home");
            // ❌ NON fare cardLayout.show(...)
        }

        if (dashboardPage != null) {
            dashboardPage.aggiornaDashboard();
        }
    }



    public Connection getConnessione() {

        if (dbManager != null) {
            return dbManager.getConnessione();
        }

        return null;
    }

    public void mostraFatture() {
        cardLayout.show(pnlDestro, "fatture");

        if (fatturePage != null) {
            fatturePage.aggiornaVistaCorrente();
        }
    }

    public void mostraProgetti() {
        cardLayout.show(pnlDestro, "progetti");

        if (progettiPage != null) {
            progettiPage.aggiornaVistaCorrente();
        }
    }

    
}
