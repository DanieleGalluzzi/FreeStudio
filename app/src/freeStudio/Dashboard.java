package freeStudio;

import java.sql.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class Dashboard extends JPanel {

    private final Color COLORE_SIDEBAR = new Color(0, 91, 150);

    private Fatture pannelloFatture;
    private Progetti pannelloProgetti;

    private Interfaccia interfaccia;

    // =====================================================
    // COSTRUTTORE
    // =====================================================
    public Dashboard(Interfaccia interfaccia, Fatture fatture, Progetti progetti) {
        this.interfaccia = interfaccia;
        this.pannelloFatture = fatture;
        this.pannelloProgetti = progetti;

        setLayout(new BorderLayout());
        inizializzaComponenti();
    }

    // =====================================================
    // COSTRUZIONE GRAFICA
    // =====================================================
    private void inizializzaComponenti() {

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titolo = new JLabel("FreeStudio - Dashboard");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel pnlTitolo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitolo.add(titolo);
        pnlHeader.add(pnlTitolo, BorderLayout.NORTH);

        int clientiAttivi = getNumeroClientiAttivi();
        int progettiInCorso = getProgettiInCorso();
        int progettiPrioritaAlta = getProgettiPrioritaAlta();
        int fattureEmesse = getFattureEmesse();
        double totaleIncassato = getTotaleIncassato();
        double totaleNonPagato = getTotaleNonPagato();

        int fattureInScadenza = (pannelloFatture != null)
                ? pannelloFatture.countFattureInScadenza()
                : 0;
        
        int fattureScadute = (pannelloFatture != null)
                ? pannelloFatture.countFattureScadute()
                : 0;


        int progettiInScadenza = (pannelloProgetti != null)
                ? pannelloProgetti.countProgettiInScadenza()
                : 0;

        JPanel pnlResoconto = new JPanel(new GridLayout(3, 3, 20, 20));
        pnlResoconto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlResoconto.add(creaCard("Clienti Attivi", clientiAttivi));
        pnlResoconto.add(creaCard("Progetti in Corso", progettiInCorso));
        pnlResoconto.add(creaCard("Fatture Emesse", fattureEmesse));

        pnlResoconto.add(creaCard("Totale Incassato", totaleIncassato));
        pnlResoconto.add(creaCard("Progetti Priorità Alta", progettiPrioritaAlta));
        pnlResoconto.add(creaCard("Fatture in Scadenza", fattureInScadenza));

        pnlResoconto.add(creaCard("Totale Non Pagato", totaleNonPagato));
        pnlResoconto.add(creaCard("Fatture Scadute", fattureScadute));
        pnlResoconto.add(creaCard("Progetti in Scadenza", progettiInScadenza));



        add(pnlHeader, BorderLayout.NORTH);
        add(pnlResoconto, BorderLayout.CENTER);
    }

    // =====================================================
    // CARD
    // =====================================================
    private JPanel creaCard(String titolo, double valore) {

        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        JLabel lblTitolo = new JLabel(titolo, SwingConstants.CENTER);
        lblTitolo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String valoreFormattato = 
                (valore == (long) valore) ? String.format("%d", (long) valore)
                                          : String.format("%.2f", valore);

        JLabel lblValore = new JLabel(valoreFormattato, SwingConstants.CENTER);
        lblValore.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValore.setForeground(Color.DARK_GRAY);

        card.add(lblTitolo);
        card.add(lblValore);

        return card;
    }

    // =====================================================
    // FUNZIONI DATABASE
    // =====================================================
    private int getNumeroClientiAttivi() {
        return getCount(Query.countClientiAttivi());
    }

    private int getProgettiInCorso() {
        return getCount(Query.countProgettiInCorso());
    }

    private int getProgettiPrioritaAlta() {
        return getCount("SELECT COUNT(*) FROM progetto WHERE priorita >= 4");
    }

    private int getFattureEmesse() {
        return getCount(Query.countFattureAttive());
    }

    private int getFattureScadute() {
        return getCount(Query.countFattureScadute());
    }

    
    private double getTotaleIncassato() {
        return getDouble(Query.sumTotaleIncassato());
    }

    private double getTotaleNonPagato() {
        return getDouble(Query.sumTotaleNonPagato());
    }

    private double getDouble(String query) {
        Connection conn = interfaccia.getDbManager().getConnessione(); // NON nel try
        if (conn == null) return 0;

        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int getCount(String query) {
        Connection conn = interfaccia.getDbManager().getConnessione(); // NON nel try
        if (conn == null) return 0;

        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // =====================================================
    // AGGIORNA
    // =====================================================
    public void aggiornaDashboard() {
        removeAll();
        inizializzaComponenti();
        revalidate();
        repaint();
    }
}
