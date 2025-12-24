package freeStudio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class Home extends JPanel {

	private Interfaccia interfaccia;
	private Connection connessione;
	private JLabel lblClienti;
	private JLabel lblProgetti;
	private JLabel lblFatture;
	private JLabel lblScadenze;

	private static final int URGENZA_ENTRO_3GG = 2;
	private static final int URGENZA_ENTRO_7GG = 1;
	private static final int URGENZA_ENTRO_MESE = 0;
	private static final int URGENZA_SCADUTA = 3;

	// 🎨 Palette professionale
	private static final Color COL_SCADUTE    = new Color(0xD32F2F); // rosso forte
	private static final Color COL_3_GIORNI   = new Color(0xF57C00); // arancione
	private static final Color COL_7_GIORNI   = new Color(0xFBC02D); // giallo
	private static final Color COL_MESE       = new Color(0x388E3C); // verde


	
	private LocalDate meseVisualizzato;
	private JPanel pannelloCalendario;
	private JLabel lblTitoloMese;


    // ============================================================
    // COSTRUTTORE
    // ============================================================
	public Home(Interfaccia interfaccia) {
	    this.interfaccia = interfaccia;
	    this.connessione = interfaccia.getConnessione();
	    this.meseVisualizzato = LocalDate.now().withDayOfMonth(1);


	    setLayout(new BorderLayout());
	    setBackground(new Color(245, 245, 245));
	    setBorder(new EmptyBorder(20, 20, 20, 20));

	    add(creaHeader(), BorderLayout.NORTH);
	    add(creaContenuto(), BorderLayout.CENTER);
	    
	    caricaNumeroClienti();
	    caricaNumeroProgetti();
	    caricaNumeroFatture();
	    caricaNumeroScadenze();
	}


    // ============================================================
    // HEADER
    // ============================================================
    private JPanel creaHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titolo = new JLabel("FreeStudio - Home");
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 32));

        header.add(titolo, BorderLayout.WEST);

        return header;
    }

    // ============================================================
    // CONTENUTO PRINCIPALE
    // ============================================================
    private JPanel creaContenuto() {

        JPanel contenuto = new JPanel(new BorderLayout(0, 20));
        contenuto.setOpaque(false);

        contenuto.add(creaCards(), BorderLayout.NORTH);
        contenuto.add(creaSezioneCentrale(), BorderLayout.CENTER);

        return contenuto;
    }

    // ============================================================
    // CARDS RIEPILOGATIVE
    // ============================================================
    private JPanel creaCards() {

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
        cards.setOpaque(false);

        cards.add(creaCardClienti());
        cards.add(creaCardProgetti());
        cards.add(creaCardFatture());
        cards.add(creaCardScadenze());
        return cards;
    }

    private JPanel creaCard(String titolo, String valore) {

        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblValore = new JLabel(valore);
        lblValore.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitolo.setForeground(Color.GRAY);

        card.add(lblValore, BorderLayout.NORTH);
        card.add(lblTitolo, BorderLayout.SOUTH);

        return card;
    }

    // ============================================================
    // SEZIONE CENTRALE (CALENDARIO + AVVISI)
    // ============================================================
    private JPanel creaSezioneCentrale() {

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        centro.setOpaque(false);

        centro.add(creaCalendarioPlaceholder());
        centro.add(creaAvvisiPlaceholder());

        return centro;
    }

    // ============================================================
    // PLACEHOLDER CALENDARIO
    // ============================================================
    private JPanel creaCalendarioPlaceholder() {

        RoundedPanel calendario = new RoundedPanel(20);
        calendario.setBackground(Color.WHITE);
        calendario.setLayout(new BorderLayout(10, 10));
        calendario.setBorder(new EmptyBorder(15, 20, 15, 20));

        // ===============================
        // HEADER (mese + navigazione)
        // ===============================
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JButton btnPrev = new JButton("◀");
        JButton btnNext = new JButton("▶");
        JButton btnOggi = new JButton("Oggi");
        btnOggi.setFocusPainted(false);

        btnOggi.addActionListener(e -> {
            meseVisualizzato = LocalDate.now().withDayOfMonth(1);
            aggiornaCalendario();
        });


        btnPrev.setFocusPainted(false);
        btnNext.setFocusPainted(false);

        lblTitoloMese = new JLabel(getTitoloMese(), SwingConstants.CENTER);
        lblTitoloMese.setFont(new Font("Segoe UI", Font.BOLD, 18));

        btnPrev.addActionListener(e -> {
            meseVisualizzato = meseVisualizzato.minusMonths(1);
            aggiornaCalendario();
        });

        btnNext.addActionListener(e -> {
            meseVisualizzato = meseVisualizzato.plusMonths(1);
            aggiornaCalendario();
        });

        JPanel navigazione = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        navigazione.setOpaque(false);

        navigazione.add(btnOggi);
        navigazione.add(btnPrev);
        navigazione.add(btnNext);

        header.add(lblTitoloMese, BorderLayout.CENTER);
        header.add(navigazione, BorderLayout.EAST);


        calendario.add(header, BorderLayout.NORTH);

        // ===============================
        // CONTENUTO DINAMICO
        // ===============================
        pannelloCalendario = new JPanel();
        pannelloCalendario.setLayout(new BoxLayout(pannelloCalendario, BoxLayout.Y_AXIS));
        pannelloCalendario.setOpaque(false);

        calendario.add(pannelloCalendario, BorderLayout.CENTER);

        // primo caricamento
        aggiornaCalendario();

        return calendario;
    }



    private void aggiornaCalendario() {

        pannelloCalendario.removeAll();

        lblTitoloMese.setText(getTitoloMese());

        pannelloCalendario.add(creaGrigliaCalendario());
        pannelloCalendario.add(Box.createVerticalStrut(10));
        pannelloCalendario.add(creaLegendaCalendario());

        pannelloCalendario.revalidate();
        pannelloCalendario.repaint();
    }


    
    private JPanel creaLegendaCalendario() {

        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legenda.setOpaque(false);

        legenda.add(creaVoceLegenda(COL_SCADUTE, "Scadute"));
        legenda.add(creaVoceLegenda(COL_3_GIORNI, "Entro 3 giorni"));
        legenda.add(creaVoceLegenda(COL_7_GIORNI, "Entro 7 giorni"));
        legenda.add(creaVoceLegenda(COL_MESE, "Entro il mese"));

        return legenda;
    }

    
    private JPanel creaVoceLegenda(Color colore, String testo) {

        JPanel voce = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        voce.setOpaque(false);

        JLabel pallino = new JLabel("●");
        pallino.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pallino.setForeground(colore);

        JLabel lblTesto = new JLabel(testo);
        lblTesto.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        voce.add(pallino);
        voce.add(lblTesto);

        return voce;
    }


    private JPanel creaGrigliaCalendario() {

        JPanel griglia = new JPanel(new GridLayout(0, 7, 8, 8));
        griglia.setOpaque(false);

        // ===============================
        // INTESTAZIONE GIORNI SETTIMANA
        // ===============================
        String[] giorni = { "Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom" };

        for (String g : giorni) {
            JLabel lbl = new JLabel(g, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            griglia.add(lbl);
        }

        // ===============================
        // SCADENZE
        // ===============================
        Map<LocalDate, Integer> scadenze = new HashMap<>();
        scadenze.putAll(caricaScadenzeFatture());
        caricaScadenzeProgetti().forEach(
            (data, urgenza) -> scadenze.merge(data, urgenza, Math::max)
        );

        LocalDate oggi = LocalDate.now();
        LocalDate primoGiorno = meseVisualizzato.withDayOfMonth(1);


        int offset = primoGiorno.getDayOfWeek().getValue(); // 1 = Lun

        // Celle vuote iniziali
        for (int i = 1; i < offset; i++) {
            griglia.add(new JLabel(""));
        }

        int giorniMese = meseVisualizzato.lengthOfMonth();


        // ===============================
        // GIORNI DEL MESE
        // ===============================
        for (int giorno = 1; giorno <= giorniMese; giorno++) {

        	LocalDate data = LocalDate.of(
        		    meseVisualizzato.getYear(),
        		    meseVisualizzato.getMonth(),
        		    giorno
        		);


            // CELLA STANDARD (sempre uguale)
            JPanel cella = new JPanel(new GridLayout(2, 1));
            cella.setOpaque(false);

            JLabel lblNumero = new JLabel(String.valueOf(giorno), SwingConstants.CENTER);
            lblNumero.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel lblPallino = new JLabel("●", SwingConstants.CENTER);
            lblPallino.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblPallino.setVisible(false); // sempre presente, ma nascosto

            // Evidenzia oggi
            if (data.equals(oggi)) {
                lblNumero.setOpaque(true);
                lblNumero.setBackground(new Color(220, 230, 245));
                lblNumero.setBorder(BorderFactory.createLineBorder(
                        new Color(180, 200, 230), 1));
                lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }

            // Scadenze
            if (scadenze.containsKey(data)) {

                int urgenza = scadenze.get(data);

                if (urgenza == URGENZA_SCADUTA) {
                    lblPallino.setForeground(COL_SCADUTE);
                }
                else if (urgenza == URGENZA_ENTRO_3GG) {
                    lblPallino.setForeground(COL_3_GIORNI);
                }
                else if (urgenza == URGENZA_ENTRO_7GG) {
                    lblPallino.setForeground(COL_7_GIORNI);
                }
                else {
                    lblPallino.setForeground(COL_MESE);
                }



                lblPallino.setVisible(true);
                cella.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                cella.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        mostraScadenzeGiorno(data);
                    }
                });
            }


            cella.add(lblNumero);
            cella.add(lblPallino);

            griglia.add(cella);
        }

        return griglia;
    }





    private Map<LocalDate, Integer> caricaScadenzeFatture() {

        Map<LocalDate, Integer> mappa = new HashMap<>();

        if (connessione == null) {
            return mappa;
        }

        LocalDate oggi = LocalDate.now();
        LocalDate primoGiorno = meseVisualizzato.withDayOfMonth(1);
        LocalDate ultimoGiorno = meseVisualizzato.withDayOfMonth(
                meseVisualizzato.lengthOfMonth()
        );


        String sql = """
            SELECT data_scadenza
            FROM fattura
            WHERE stato_pagamento <> 'Pagata'
            AND data_scadenza BETWEEN ? AND ?
        """;

        try (PreparedStatement ps = connessione.prepareStatement(sql)) {

            ps.setString(1, primoGiorno.toString());   // YYYY-MM-DD
            ps.setString(2, ultimoGiorno.toString());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    LocalDate data = LocalDate.parse(rs.getString("data_scadenza"));

                    int urgenza = calcolaUrgenza(data, oggi);
                    aggiornaMappaScadenze(mappa, data, urgenza);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mappa;
    }



    private java.util.List<String> caricaFatturePerData(LocalDate data) {

        java.util.List<String> elenco = new java.util.ArrayList<>();

        if (connessione == null) {
            return elenco;
        }

        String sql = """
        	    SELECT numero_fattura
        	    FROM fattura
        	    WHERE stato_pagamento <> 'Pagata'
        	    AND data_scadenza = ?
        	""";


        try (PreparedStatement ps = connessione.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(data));

            try (ResultSet rs = ps.executeQuery()) {

            	while (rs.next()) {
            	    elenco.add("Fattura " + rs.getString("numero_fattura"));
            	}

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return elenco;
    }

    private Map<LocalDate, Integer> caricaScadenzeProgetti() {

        Map<LocalDate, Integer> mappa = new HashMap<>();

        if (connessione == null) {
            return mappa;
        }

        LocalDate oggi = LocalDate.now();
        LocalDate primoGiorno = meseVisualizzato.withDayOfMonth(1);
        LocalDate ultimoGiorno = meseVisualizzato.withDayOfMonth(
                meseVisualizzato.lengthOfMonth()
        );


        String sql = """
            SELECT data_fine
            FROM progetto
            WHERE stato = 'In corso'
            AND data_fine BETWEEN ? AND ?
        """;

        try (PreparedStatement ps = connessione.prepareStatement(sql)) {

            ps.setString(1, primoGiorno.toString());
            ps.setString(2, ultimoGiorno.toString());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    LocalDate data = LocalDate.parse(rs.getString("data_fine"));

                    int urgenza = calcolaUrgenza(data, oggi);
                    aggiornaMappaScadenze(mappa, data, urgenza);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mappa;
    }



    private java.util.List<String> caricaProgettiPerData(LocalDate data) {

        java.util.List<String> elenco = new java.util.ArrayList<>();

        if (connessione == null) {
            return elenco;
        }

        String sql = """
        	    SELECT titolo
        	    FROM progetto
        	    WHERE stato = 'In corso'
        	    AND data_fine = ?
        	""";


        try (PreparedStatement ps = connessione.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(data));

            try (ResultSet rs = ps.executeQuery()) {

            	while (rs.next()) {
            	    elenco.add("Progetto – " + rs.getString("titolo"));
            	}

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return elenco;
    }

    private void mostraScadenzeGiorno(LocalDate data) {

        java.util.List<String> fatture = caricaFatturePerData(data);
        java.util.List<String> progetti = caricaProgettiPerData(data);

        if (fatture.isEmpty() && progetti.isEmpty()) {
            return;
        }

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Scadenze del " + DateUtils.formattaUi(data),
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> model = new DefaultListModel<>();

        if (!fatture.isEmpty()) {
            model.addElement("=== FATTURE ===");
            fatture.forEach(model::addElement);
        }

        if (!progetti.isEmpty()) {
            model.addElement(" ");
            model.addElement("=== PROGETTI ===");
            progetti.forEach(model::addElement);
        }

        JList<String> lista = new JList<>(model);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dialog.add(new JScrollPane(lista), BorderLayout.CENTER);

        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dialog.dispose());

        JPanel south = new JPanel();
        south.add(btnChiudi);

        dialog.add(south, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }




    // ============================================================
    // PLACEHOLDER AVVISI
    // ============================================================
    private JPanel creaAvvisiPlaceholder() {

        RoundedPanel avvisi = new RoundedPanel(20);
        avvisi.setBackground(Color.WHITE);
        avvisi.setLayout(new BorderLayout(10, 10));
        avvisi.setBorder(new EmptyBorder(15, 20, 15, 20));

        java.util.List<String> avvisiCritici = caricaAvvisiCritici();
        
	     // ===============================
	     // HEADER: TITOLO + BADGE
	     // ===============================
	     JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
	     header.setOpaque(false);
	
	     JLabel titolo = new JLabel("Avvisi critici");
	     titolo.setFont(new Font("Segoe UI", Font.BOLD, 18));
	
	     // Badge numerico
	     JLabel badge = new JLabel(String.valueOf(avvisiCritici.size()));
	     badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
	     badge.setForeground(Color.WHITE);
	     badge.setOpaque(true);
	     badge.setBackground(COL_SCADUTE);
	     badge.setBorder(new EmptyBorder(2, 8, 2, 8));
	
	     // Nasconde il badge se non ci sono avvisi
	     badge.setVisible(!avvisiCritici.isEmpty());
	
	     header.add(titolo);
	     header.add(badge);



        // ===============================
        // CONTENUTO SCROLLABILE
        // ===============================
        JPanel contenuto = new JPanel();
        contenuto.setLayout(new BoxLayout(contenuto, BoxLayout.Y_AXIS));
        contenuto.setOpaque(false);


        if (avvisiCritici.isEmpty()) {

            JLabel ok = new JLabel("✔ Nessuna scadenza critica");
            ok.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ok.setForeground(new Color(0, 120, 0));
            contenuto.add(ok);

        } else {

            for (int i = 0; i < avvisiCritici.size(); i++) {

                String testo = avvisiCritici.get(i);

                // Riga avviso
                JPanel riga = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                riga.setOpaque(false);
                riga.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                riga.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {

                    	String t = testo.toLowerCase();

                    	if (t.contains("fattura")) {
                    	    interfaccia.mostraFatture();
                    	}

                    	if (t.contains("progetto")) {
                    	    interfaccia.mostraProgetti();
                    	}

                    }
                });


                boolean scaduto = testo.startsWith("SCADUT");

                JLabel pallino = new JLabel("●");
                pallino.setFont(new Font("Segoe UI", Font.BOLD, 14));
                pallino.setForeground(scaduto ? COL_SCADUTE : COL_3_GIORNI);

                JLabel lblTesto = new JLabel(testo.replace("SCADUTA – ", "").replace("SCADUTO – ", ""));
                lblTesto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblTesto.setForeground(scaduto ? COL_SCADUTE : COL_3_GIORNI);


                riga.add(pallino);
                riga.add(lblTesto);

                contenuto.add(riga);

                // Separatore (non dopo l'ultimo)
                if (i < avvisiCritici.size() - 1) {
                    JSeparator sep = new JSeparator();
                    sep.setForeground(new Color(220, 220, 220));
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    contenuto.add(Box.createVerticalStrut(6));
                    contenuto.add(sep);
                    contenuto.add(Box.createVerticalStrut(6));
                }
            }
        }

        JScrollPane scroll = new JScrollPane(contenuto);
        scroll.setPreferredSize(new Dimension(0, 120));
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        avvisi.add(header, BorderLayout.NORTH);
        avvisi.add(scroll, BorderLayout.CENTER);

        return avvisi;
    }


    
    // ============================================================
    // CARD CLIENTI
    // ============================================================
    private JPanel creaCardClienti() {

        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblClienti = new JLabel("0");
        lblClienti.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel titolo = new JLabel("Clienti");
        titolo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titolo.setForeground(Color.GRAY);

        card.add(lblClienti, BorderLayout.NORTH);
        card.add(titolo, BorderLayout.SOUTH);

        return card;
    }
    
    private void caricaNumeroClienti() {

    	if (connessione == null) {
    	    lblClienti.setText("–");
    	    return;
    	}

        String sql = "SELECT COUNT(*) FROM cliente WHERE attivo = 1";

        try (
            PreparedStatement ps = connessione.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                lblClienti.setText(rs.getString(1));
            }

        } catch (Exception e) {
            lblClienti.setText("–");
            e.printStackTrace();
        }
    }

    // ============================================================
    // CARD PROGETTI
    // ============================================================

    private JPanel creaCardProgetti() {

        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblProgetti = new JLabel("0");
        lblProgetti.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel titolo = new JLabel("Progetti");
        titolo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titolo.setForeground(Color.GRAY);

        card.add(lblProgetti, BorderLayout.NORTH);
        card.add(titolo, BorderLayout.SOUTH);

        return card;
    }

    private void caricaNumeroProgetti() {

        if (connessione == null) {
            lblProgetti.setText("–");
            return;
        }

        String sql = "SELECT COUNT(*) FROM progetto WHERE stato = 'In corso'";

        try (
            PreparedStatement ps = connessione.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                lblProgetti.setText(rs.getString(1));
            }

        } catch (Exception e) {
            lblProgetti.setText("–");
            e.printStackTrace();
        }
    }

    // ============================================================
    // CARD FATTURE
    // ============================================================

    private JPanel creaCardFatture() {

        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblFatture = new JLabel("0");
        lblFatture.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel titolo = new JLabel("Fatture");
        titolo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titolo.setForeground(Color.GRAY);

        card.add(lblFatture, BorderLayout.NORTH);
        card.add(titolo, BorderLayout.SOUTH);

        return card;
    }

    private void caricaNumeroFatture() {

        if (connessione == null) {
            lblFatture.setText("–");
            return;
        }

        String sql = "SELECT COUNT(*) FROM fattura WHERE stato_pagamento <> 'Pagata'";

        try (
            PreparedStatement ps = connessione.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                lblFatture.setText(rs.getString(1));
            }

        } catch (Exception e) {
            lblFatture.setText("–");
            e.printStackTrace();
        }
    }

    // ============================================================
    // CARD SCADENZE
    // ============================================================
    private JPanel creaCardScadenze() {

        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblScadenze = new JLabel("0");
        lblScadenze.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel titolo = new JLabel("Scadenze");
        titolo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titolo.setForeground(Color.GRAY);

        card.add(lblScadenze, BorderLayout.NORTH);
        card.add(titolo, BorderLayout.SOUTH);

        return card;
    }

    private void caricaNumeroScadenze() {

        if (connessione == null) {
            lblScadenze.setText("–");
            return;
        }

        int totale = 0;

        // ==========================
        // FATTURE ENTRO 7 GIORNI
        // ==========================
        String sqlFatture = """
            SELECT COUNT(*)
            FROM fattura
            WHERE stato_pagamento <> 'Pagata'
            AND data_scadenza BETWEEN CURDATE()
                                 AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
        """;

        try (
            PreparedStatement ps = connessione.prepareStatement(sqlFatture);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                totale += rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==========================
        // PROGETTI ENTRO 7 GIORNI
        // ==========================
        String sqlProgetti = """
            SELECT COUNT(*)
            FROM progetto
            WHERE stato = 'In corso'
            AND data_fine BETWEEN CURDATE()
                              AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
        """;

        try (
            PreparedStatement ps = connessione.prepareStatement(sqlProgetti);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                totale += rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==========================
        // AGGIORNAMENTO LABEL
        // ==========================
        lblScadenze.setText(String.valueOf(totale));
    }


    private int calcolaUrgenza(LocalDate dataScadenza, LocalDate oggi) {

        long giorni = java.time.temporal.ChronoUnit.DAYS.between(oggi, dataScadenza);

        // 🔴 SCADUTA
        if (giorni < 0) {
            return URGENZA_SCADUTA;
        }

        // 🟠 ENTRO 3 GIORNI
        if (giorni <= 3) {
            return URGENZA_ENTRO_3GG;
        }

        // 🟡 ENTRO 7 GIORNI
        if (giorni <= 7) {
            return URGENZA_ENTRO_7GG;
        }

        // 🟢 ENTRO IL MESE
        return URGENZA_ENTRO_MESE;
    }




    private void aggiornaMappaScadenze(Map<LocalDate, Integer> scadenze, LocalDate data, int urgenza) {

        if (urgenza < 0) {
            return;
        }

        if (!scadenze.containsKey(data)) {
            scadenze.put(data, urgenza);
            return;
        }

        int attuale = scadenze.get(data);
        if (urgenza > attuale) {
            scadenze.put(data, urgenza);
        }
    }

    private String getTitoloMese() {

        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")
                        .withLocale(java.util.Locale.ITALIAN);

        String titolo = meseVisualizzato.format(formatter);

        // Prima lettera maiuscola (Dicembre invece di dicembre)
        return titolo.substring(0, 1).toUpperCase() + titolo.substring(1);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------
    //AVVISI-----------------AVVISI-------------------------AVVISI---------------------------AVVISI-------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------------
    
    private java.util.List<String> caricaAvvisiCritici() {

        java.util.List<String> avvisi = new java.util.ArrayList<>();

        if (connessione == null) {
            return avvisi;
        }

        // ==========================
        // FATTURE ENTRO 3 GIORNI
        // ==========================
        String sqlFatture = """
            SELECT numero_fattura, data_scadenza
            FROM fattura
            WHERE stato_pagamento <> 'Pagata'
            AND data_scadenza <= DATE_ADD(CURDATE(), INTERVAL 3 DAY)

        """;

        try (
            PreparedStatement ps = connessione.prepareStatement(sqlFatture);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
            	String numero = rs.getString("numero_fattura");
            	LocalDate data = LocalDate.parse(rs.getString("data_scadenza"));

            	String prefisso = data.isBefore(LocalDate.now())
            	        ? "SCADUTA – "
            	        : "ENTRO 3GG – ";

            	avvisi.add(prefisso + "Fattura " + numero + " – " + DateUtils.formattaUi(data));




            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==========================
        // PROGETTI ENTRO 3 GIORNI
        // ==========================
        String sqlProgetti = """
            SELECT titolo, data_fine
            FROM progetto
            WHERE stato = 'In corso'
            AND data_fine <= DATE_ADD(CURDATE(), INTERVAL 3 DAY)

        """;

        try (
            PreparedStatement ps = connessione.prepareStatement(sqlProgetti);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
            	String titolo = rs.getString("titolo");
            	LocalDate data = LocalDate.parse(rs.getString("data_fine"));

            	String prefisso = data.isBefore(LocalDate.now())
            	        ? "SCADUTO – "
            	        : "ENTRO 3GG – ";

            	avvisi.add(prefisso + "Progetto \"" + titolo + "\" – " + DateUtils.formattaUi(data));



            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return avvisi;
    }

    
}
