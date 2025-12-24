package freeStudio;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.jdatepicker.impl.JDatePickerImpl;




public class Fatture extends JPanel {

    private JTable tabFatture;
    private DefaultTableModel modelloTab;

    private JButton btnNuovo;
    private JButton btnModifica;
    private JButton btnMostraInfo;
    private JButton btnCerca;

    private JComboBox<String> comboCerca;

    private JLabel lblTotaleEmesso;
    private JLabel lblTotaleIncassato;
    private JLabel lblTotaleDaIncassare;

    private JDialog pannelloFattura;
    private int idFatturaInModifica = -1;

    private JComboBox<String> comboClienti;
    private Map<String, Integer> mappaClienti = new HashMap<>();

    private JTextField campoCerca;
    private JTextField campoNumeroFattura;
    private JTextField campoImporto;
    private JTextArea campoNote;

    private Interfaccia interfaccia;
    private final Color COLORE_SIDEBAR = new Color(0, 91, 150);
    private JDatePickerImpl pickerDataFattura;
    private JDatePickerImpl pickerDataScadenza;

    private JDatePickerImpl pickerCercaDataFattura;
    private JPanel pnlCercaInput;
    private CardLayout cardCerca;
    private JButton btnArchivia;
    private JButton btnRecupera;
    private JButton btnMostraArchivio;

    private JLabel lblArchivio;
    private boolean archivioVisibile = false;


    // ============================================================
    // COSTRUTTORE
    // ============================================================
    public Fatture(Interfaccia interfaccia) {
        this.interfaccia = interfaccia;
        setLayout(new BorderLayout());

        inizializzaHeader();
        creaTabella();
        caricaFatture();
        aggiornaCardTotali();
    }

    // ============================================================
    // HEADER
    // ============================================================
    private void inizializzaHeader() {

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titolo = new JLabel("FreeStudio - Fatture");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        
        JPanel pnlTitolo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitolo.add(titolo);
        pnlHeader.add(pnlTitolo, BorderLayout.NORTH);

        JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuovo = new JButton("Nuovo");
        btnModifica = new JButton("Modifica");
        btnArchivia = new JButton("Archivia");
        btnRecupera = new JButton("Recupera");
        btnMostraArchivio = new JButton("Mostra Archivio");
        btnMostraInfo = new JButton("Mostra Info");

        btnRecupera.setVisible(false);

        pnlBottoni.add(btnNuovo);
        pnlBottoni.add(btnModifica);
        pnlBottoni.add(btnArchivia);
        pnlBottoni.add(btnRecupera);
        pnlBottoni.add(btnMostraArchivio);
        pnlBottoni.add(btnMostraInfo);


        JPanel pnlCerca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboCerca = new JComboBox<>(new String[]{"Numero Fattura", "Data Fattura", "Stato", "Tutti"});
        
        campoCerca = new JTextField(15);
        btnCerca = new JButton("Cerca");

        // picker ricerca data
        pickerCercaDataFattura = DatePickerFactory.creaDatePicker();

        // card layout per input ricerca
        cardCerca = new CardLayout();
        pnlCercaInput = new JPanel(cardCerca);
        pnlCercaInput.add(campoCerca, "TEXT");
        pnlCercaInput.add(pickerCercaDataFattura, "DATE");

        // default
        cardCerca.show(pnlCercaInput, "TEXT");

        pnlCerca.add(new JLabel("Cerca per:"));
        pnlCerca.add(comboCerca);
        pnlCerca.add(pnlCercaInput);
        pnlCerca.add(btnCerca);

        // listener cambio filtro
        comboCerca.addActionListener(e -> {
            String filtro = comboCerca.getSelectedItem().toString();
            if (filtro.equals("Data Fattura")) {
                cardCerca.show(pnlCercaInput, "DATE");
                campoCerca.setText("");
            } else {
                cardCerca.show(pnlCercaInput, "TEXT");
                // reset picker
                pickerCercaDataFattura.getModel().setValue(null);
                pickerCercaDataFattura.getModel().setSelected(false);
                pickerCercaDataFattura.getJFormattedTextField().setText("");
            }
        });


        JPanel pnlOperazioni = new JPanel();
        pnlOperazioni.setLayout(new BoxLayout(pnlOperazioni, BoxLayout.Y_AXIS));
        pnlOperazioni.add(pnlBottoni);
        pnlOperazioni.add(pnlCerca);

        pnlHeader.add(pnlOperazioni, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // LISTENER
        btnNuovo.addActionListener(e -> apriNuovaFattura());
        btnModifica.addActionListener(e -> apriModificaFattura());
        btnMostraInfo.addActionListener(e -> mostraInfoFattura());
        btnCerca.addActionListener(e -> cercaFatture());
        btnArchivia.addActionListener(e -> archiviaFatturaSelezionata());
        btnRecupera.addActionListener(e -> recuperaFatturaSelezionata());
        btnMostraArchivio.addActionListener(e -> toggleArchivio());

    }

    // ============================================================
    // TABELLA + CARD TOTALI
    // ============================================================
    /**
     * Crea la tabella delle fatture e il pannello contenitore.
     * La tabella mostra:
     * - Data fattura (creazione)
     * - Data scadenza
     * - Stato pagamento
     *
     * Include anche l'etichetta "Archivio" visibile solo
     * quando la vista archivio è attiva.
     */
    private void creaTabella() {

        // =========================
        // MODELLO DATI DELLA TABELLA
        // =========================
        modelloTab = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Numero Fattura",
                        "Data Fattura",
                        "Data Scadenza",
                        "Importo",
                        "Stato Pagamento"
                },
                0
        ) {
            // Rende la tabella NON modificabile dall’utente
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // =========================
        // JTABLE
        // =========================
        tabFatture = new JTable(modelloTab);
        tabFatture.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabFatture.setRowHeight(24);

        // =========================
        // LABEL ARCHIVIO
        // =========================
        lblArchivio = new JLabel("Archivio", SwingConstants.CENTER);
        lblArchivio.setFont(new Font("Arial", Font.BOLD, 16));
        lblArchivio.setForeground(Color.DARK_GRAY);
        lblArchivio.setVisible(false);

        // =========================
        // PANNELLO TABELLA
        // =========================
        JPanel pannelloTabella = new JPanel(new BorderLayout());
        pannelloTabella.add(lblArchivio, BorderLayout.NORTH);
        pannelloTabella.add(new JScrollPane(tabFatture), BorderLayout.CENTER);

        // =========================
        // INSERIMENTO NEL LAYOUT
        // =========================
        add(pannelloTabella, BorderLayout.CENTER);
        add(creaPannelloCardTotali(), BorderLayout.SOUTH);
    }


    private JPanel creaPannelloCardTotali() {

        JPanel pnl = new JPanel(new GridLayout(1, 3, 20, 20));
        pnl.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTotaleEmesso = new JLabel("0 €", SwingConstants.CENTER);
        lblTotaleIncassato = new JLabel("0 €", SwingConstants.CENTER);
        lblTotaleDaIncassare = new JLabel("0 €", SwingConstants.CENTER);

        pnl.add(creaCard("Totale Emesso", lblTotaleEmesso));
        pnl.add(creaCard("Totale Incassato", lblTotaleIncassato));
        pnl.add(creaCard("Da Incassare", lblTotaleDaIncassare));

        return pnl;
    }

    private JPanel creaCard(String titolo, JLabel lblValore) {

        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        JLabel lblTitolo = new JLabel(titolo, SwingConstants.CENTER);
        lblTitolo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitolo.setForeground(COLORE_SIDEBAR);

        lblValore.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValore.setForeground(Color.DARK_GRAY);

        card.add(lblTitolo);
        card.add(lblValore);

        return card;
    }
    
    private void archiviaFatturaSelezionata() {

        int riga = tabFatture.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una fattura.");
            return;
        }

        int id = (int) tabFatture.getValueAt(riga, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Archiviare questa fattura?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            archiviaFattura(id);
            aggiornaVistaCorrente();
        }
    }

    private void recuperaFatturaSelezionata() {

        int riga = tabFatture.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una fattura dall'archivio.");
            return;
        }

        int id = (int) tabFatture.getValueAt(riga, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Recuperare questa fattura?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            recuperaFattura(id);
            aggiornaVistaCorrente();
        }
    }

    private void archiviaFattura(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps =
                     conn.prepareStatement(Query.archiviaFattura())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Fattura archiviata!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante l'archiviazione della fattura.\n\n" + e.getMessage(),
                    "Archivio fatture",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void recuperaFattura(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps =
                     conn.prepareStatement(Query.recuperaFattura())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Fattura recuperata!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante il recupero della fattura.\n\n" + e.getMessage(),
                    "Archivio fatture",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }


    // ============================================================
    // CARICA + AGGIORNA TOTALI
    // ============================================================
    private void caricaFatture() {

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getFattureAttive());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("numero_fattura"),
                        DateUtils.dbToUi(rs.getString("data_fattura")),
                        DateUtils.dbToUi(rs.getString("data_scadenza")),
                        rs.getDouble("importo"),
                        rs.getString("stato_pagamento")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante il caricamento delle fatture.\n\n" + e.getMessage(),
                    "Caricamento fatture",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void caricaFattureArchiviate() {

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getFattureArchiviate());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("numero_fattura"),
                        DateUtils.dbToUi(rs.getString("data_fattura")),
                        DateUtils.dbToUi(rs.getString("data_scadenza")),
                        rs.getDouble("importo"),
                        rs.getString("stato_pagamento")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante il caricamento delle fatture archiviate.\n\n" + e.getMessage(),
                    "Archivio fatture",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void toggleArchivio() {

        archivioVisibile = !archivioVisibile;

        if (archivioVisibile) {
            lblArchivio.setVisible(true);
            btnRecupera.setVisible(true);
            btnArchivia.setVisible(false);
            btnMostraArchivio.setText("Mostra Attive");
            caricaFattureArchiviate();
        } else {
            lblArchivio.setVisible(false);
            btnRecupera.setVisible(false);
            btnArchivia.setVisible(true);
            btnMostraArchivio.setText("Mostra Archivio");
            caricaFatture();
        }
    }


    

    private void aggiornaCardTotali() {
        lblTotaleEmesso.setText(getSomma(Query.totaleEmesso()) + " €");
        lblTotaleIncassato.setText(getSomma(Query.sumTotaleIncassato()) + " €");
        lblTotaleDaIncassare.setText(getSomma(Query.sumTotaleNonPagato()) + " €");
       // lblTotaleDaIncassare.setText(getSomma(Query.totaleDaIncassare()) + " €");
    }

    private double getSomma(String query) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0.0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // ============================================================
    // CERCA
    // ============================================================
    private void cercaFatture() {

        modelloTab.setRowCount(0);

        String filtro = comboCerca.getSelectedItem().toString();
        String valore = campoCerca.getText().trim();

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {

            PreparedStatement ps;

            // =========================
            // MODALITÀ ARCHIVIO
            // =========================
            if (archivioVisibile) {

                switch (filtro) {

                    case "Numero Fattura" -> {
                        if (valore.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Inserisci un numero fattura.");
                            return;
                        }
                        ps = conn.prepareStatement(Query.getFattureArchiviateByNumero());
                        ps.setString(1, "%" + valore + "%");
                    }

                    case "Data Fattura" -> {
                        java.util.Date d =
                                (java.util.Date) pickerCercaDataFattura.getModel().getValue();

                        if (d == null) {
                            JOptionPane.showMessageDialog(this, "Seleziona una data.");
                            return;
                        }

                        ps = conn.prepareStatement(Query.getFattureArchiviateByData());
                        ps.setDate(1, new java.sql.Date(d.getTime()));
                    }

                    case "Stato" -> {
                        if (valore.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Inserisci uno stato.");
                            return;
                        }
                        ps = conn.prepareStatement(Query.getFattureArchiviateByStato());
                        ps.setString(1, valore);
                    }

                    default -> {
                        ps = conn.prepareStatement(Query.getFattureArchiviate());
                    }
                }

            // =========================
            // MODALITÀ ATTIVE
            // =========================
            } else {

                switch (filtro) {

                    case "Numero Fattura" -> {
                        if (valore.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Inserisci un numero fattura.");
                            return;
                        }
                        ps = conn.prepareStatement(Query.getFattureAttiveByNumero());
                        ps.setString(1, "%" + valore + "%");
                    }

                    case "Data Fattura" -> {
                        java.util.Date d =
                                (java.util.Date) pickerCercaDataFattura.getModel().getValue();

                        if (d == null) {
                            JOptionPane.showMessageDialog(this, "Seleziona una data.");
                            return;
                        }

                        ps = conn.prepareStatement(Query.getFattureAttiveByData());
                        ps.setDate(1, new java.sql.Date(d.getTime()));
                    }

                    case "Stato" -> {
                        if (valore.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Inserisci uno stato.");
                            return;
                        }
                        ps = conn.prepareStatement(Query.getFattureAttiveByStato());
                        ps.setString(1, valore);
                    }

                    default -> {
                        ps = conn.prepareStatement(Query.getFattureAttive());
                    }
                }
            }

            // =========================
            // ESECUZIONE
            // =========================
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modelloTab.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("numero_fattura"),
                            DateUtils.dbToUi(rs.getString("data_fattura")),
                            DateUtils.dbToUi(rs.getString("data_scadenza")),
                            rs.getDouble("importo"),
                            rs.getString("stato_pagamento")
                    });
                }
            }

            aggiornaCardTotali();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante la ricerca delle fatture.\n\n" + e.getMessage(),
                    "Ricerca fatture",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }



    // ============================================================
    // DIALOG FATTURA
    // ============================================================
    private void apriNuovaFattura() {

        idFatturaInModifica = -1;
        creaDialogFattura();

        campoNumeroFattura.setText("");
        campoNumeroFattura.setEditable(true);


        // ===============================
        // DATA FATTURA = OGGI
        // ===============================
        LocalDate oggi = LocalDate.now();

        pickerDataFattura.getModel().setDate(
                oggi.getYear(),
                oggi.getMonthValue() - 1,
                oggi.getDayOfMonth()
        );
        pickerDataFattura.getModel().setSelected(true);

        // ===============================
        // DATA SCADENZA VUOTA
        // ===============================
        pickerDataScadenza.getModel().setValue(null);
        pickerDataScadenza.getModel().setSelected(false);

        // ===============================
        // APERTURA DIALOG
        // ===============================
        pannelloFattura.setTitle("Nuova Fattura");
        pannelloFattura.setVisible(true);
    }



    private void apriModificaFattura() {

        int riga = tabFatture.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una fattura.");
            return;
        }

        idFatturaInModifica = (int) tabFatture.getValueAt(riga, 0);

        creaDialogFattura();
        pannelloFattura.setTitle("Modifica Fattura");
        caricaDatiFatturaNelDialog(idFatturaInModifica);
        pannelloFattura.setVisible(true);
    }

    private void creaDialogFattura() {

        pannelloFattura = new JDialog((Frame) null, true);
        pannelloFattura.setTitle("Fattura");
        pannelloFattura.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridLayout(0, 4, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        pnl.setPreferredSize(new Dimension(700, 300));

        // =========================
        // CAMPI
        // =========================
        comboClienti = new JComboBox<>();
        FormStyle.comboBox(comboClienti);
        mappaClienti.clear();
        caricaClientiNelCombo();

        campoNumeroFattura = new JTextField();
        FormStyle.textField(campoNumeroFattura);

        pickerDataFattura = DatePickerFactory.creaDatePicker();
        pickerDataScadenza = DatePickerFactory.creaDatePicker();


        campoImporto = new JTextField();
        FormStyle.textField(campoImporto);

        

        // =========================
        // TEXT AREA NOTE
        // =========================
        campoNote = new JTextArea();
        JScrollPane scrollNote = FormStyle.textArea(campoNote);

        // =========================
        // RIGHE (7 × 2 CAMPI)
        // =========================
        aggiungiCampo(pnl, "Cliente", comboClienti);
        aggiungiCampo(pnl, "Numero Fattura", campoNumeroFattura);

        aggiungiCampo(pnl, "Data Fattura", pickerDataFattura);
        aggiungiCampo(pnl, "Data Scadenza", pickerDataScadenza);


        aggiungiCampo(pnl, "Importo", campoImporto);

        aggiungiCampo(pnl, "Note", scrollNote);
        aggiungiCampo(pnl, "", new JLabel("")); // filler per chiudere la griglia
        
     // ===== filler per uniformare altezza =====
        aggiungiCampo(pnl, "", new JLabel(""));
        aggiungiCampo(pnl, "", new JLabel(""));

        aggiungiCampo(pnl, "", new JLabel(""));
        aggiungiCampo(pnl, "", new JLabel(""));

        aggiungiCampo(pnl, "", new JLabel(""));
        aggiungiCampo(pnl, "", new JLabel(""));


        // =========================
        // BOTTONE
        // =========================
        JButton btnConferma = new JButton("Conferma");
        btnConferma.addActionListener(e -> confermaFattura());

        pannelloFattura.add(pnl, BorderLayout.CENTER);
        pannelloFattura.add(btnConferma, BorderLayout.SOUTH);

        pannelloFattura.pack();
        pannelloFattura.setLocationRelativeTo(null);
    }




    private void aggiungiCampo(JPanel pnl, String testo, JComponent campo) {
        JLabel lbl = new JLabel(testo);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(campo, BorderLayout.CENTER);

        pnl.add(lbl);
        pnl.add(wrapper);
    }


    // ============================================================
    // SALVATAGGIO FATTURA
    // ============================================================
    private void confermaFattura() {

    	java.util.Date dFattura = (java.util.Date) pickerDataFattura.getModel().getValue();
    	java.util.Date dScadenza = (java.util.Date) pickerDataScadenza.getModel().getValue();

    	if (dFattura == null) {
    	    JOptionPane.showMessageDialog(this, "Data fattura obbligatoria.");
    	    return;
    	}

    	if (dScadenza == null) {
    	    JOptionPane.showMessageDialog(this, "Data scadenza obbligatoria.");
    	    return;
    	}

    	// ⛔ Data scadenza non può essere prima della data fattura
    	if (dScadenza.before(dFattura)) {
    	    JOptionPane.showMessageDialog(
    	        this,
    	        "La data di scadenza non può essere antecedente alla data della fattura.",
    	        "Errore date",
    	        JOptionPane.ERROR_MESSAGE
    	    );
    	    return;
    	}

    	
        if (campoNumeroFattura.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Numero fattura obbligatorio.");
            return;
        }

        

        if (campoImporto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un importo.");
            return;
        }

        double importo;

        try {
            importo = Double.parseDouble(campoImporto.getText().replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Importo non valido.");
            return;
        }

        if (importo < 0) {
            JOptionPane.showMessageDialog(this, "L'importo non può essere negativo.");
            return;
        }


        

        
        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = (idFatturaInModifica == -1)
                    ? conn.prepareStatement(Query.aggiungiFattura())
                    : conn.prepareStatement(Query.modificaFattura());

            int idCliente = mappaClienti.get(comboClienti.getSelectedItem().toString());

            ps.setInt(1, idCliente);
            ps.setString(2, campoNumeroFattura.getText());
            ps.setDate(3, new java.sql.Date(dFattura.getTime()));
            ps.setDate(4, new java.sql.Date(dScadenza.getTime()));
            ps.setDouble(5, importo);
            ps.setString(6, "Non pagata");
            ps.setString(7, campoNote.getText());

            if (idFatturaInModifica != -1)
                ps.setInt(8, idFatturaInModifica);

            ps.executeUpdate();
            ps.close();

            pannelloFattura.dispose();

            JOptionPane.showMessageDialog(this,
                    idFatturaInModifica == -1 ? "Fattura aggiunta!" : "Fattura modificata!");

            caricaFatture();
            aggiornaCardTotali();



        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il salvataggio della fattura.\n\n" + e.getMessage(),
                "Salvataggio fattura",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    // ============================================================
    // CARICA CLIENTI NEL COMBO
    // ============================================================
    private void caricaClientiNelCombo() {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getClientiAttivi());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome") + " " + rs.getString("cognome");
                comboClienti.addItem(nome);
                mappaClienti.put(nome, rs.getInt("id"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei clienti.\n\n" + e.getMessage(),
                "Caricamento clienti",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    // ============================================================
    // MODIFICA FATTURA → CARICAMENTO DATI
    // ============================================================
    private void caricaDatiFatturaNelDialog(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getFatturaById())) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                comboClienti.setSelectedItem(rs.getString("nome") + " " + rs.getString("cognome"));
                campoNumeroFattura.setText(rs.getString("numero_fattura"));
                campoNumeroFattura.setEditable(false);
                LocalDate dataFattura = DateUtils.dbToLocalDate(rs.getString("data_fattura"));
                if (dataFattura != null) {
                    pickerDataFattura.getModel().setDate(
                            dataFattura.getYear(),
                            dataFattura.getMonthValue() - 1,
                            dataFattura.getDayOfMonth()
                    );
                    pickerDataFattura.getModel().setSelected(true);

                    
                    pickerDataFattura.getJFormattedTextField().setText(
                            DateUtils.formattaUi(dataFattura)
                    );
                }

                LocalDate dataScadenza = DateUtils.dbToLocalDate(rs.getString("data_scadenza"));
                if (dataScadenza != null) {
                    pickerDataScadenza.getModel().setDate(
                            dataScadenza.getYear(),
                            dataScadenza.getMonthValue() - 1,
                            dataScadenza.getDayOfMonth()
                    );
                    pickerDataScadenza.getModel().setSelected(true);

                    
                    pickerDataScadenza.getJFormattedTextField().setText(
                            DateUtils.formattaUi(dataScadenza)
                    );
                }



                campoImporto.setText(String.valueOf(rs.getDouble("importo")));
               
                campoNote.setText(rs.getString("note"));
            }

            rs.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei dati della fattura.\n\n" + e.getMessage(),
                "Modifica fattura",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    

    // ============================================================
    // MOSTRA INFO
    // ============================================================
    private void mostraInfoFattura() {

        int riga = tabFatture.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una fattura.");
            return;
        }

        int id = (int) tabFatture.getValueAt(riga, 0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getFatturaById())) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String info =
                        "ID: " + rs.getInt("id") + "\n" +
                                "Cliente: " + rs.getString("nome") + " " + rs.getString("cognome") + "\n" +
                                "Numero: " + rs.getString("numero_fattura") + "\n" +
                                "Data: " + DateUtils.dbToUi(rs.getString("data_fattura")) + "\n" +
                                "Scadenza: " + DateUtils.dbToUi(rs.getString("data_scadenza")) + "\n" +
                                "Importo: €" + rs.getDouble("importo") + "\n" +
                                "Stato: " + rs.getString("stato_pagamento") + "\n" +
                                "Note:\n" + rs.getString("note");

                JOptionPane.showMessageDialog(this, info,
                        "Dettagli Fattura", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento delle informazioni della fattura.\n\n" + e.getMessage(),
                "Dettagli fattura",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    // ============================================================
    // USATO DA PAGAMENTI
    // ============================================================
    public void aggiornaTabella() {
        caricaFatture();
        aggiornaCardTotali();
    }

    // ============================================================
    // FATTURE IN SCADENZA
    // ============================================================
    public int countFattureInScadenza() {

        int totale = 0;
        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT data_scadenza FROM fattura WHERE data_scadenza IS NOT NULL");
             ResultSet rs = ps.executeQuery()) {

            LocalDate oggi = LocalDate.now();

            while (rs.next()) {

                String data = rs.getString("data_scadenza");
                if (data == null || data.isBlank()) continue;

                LocalDate scadenza = DateUtils.dbToLocalDate(data);
                long giorni = ChronoUnit.DAYS.between(oggi, scadenza);

                if (giorni >= 0 && giorni <= 7)
                    totale++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totale;
    }
    
 // ============================================================
 // FATTURE SCADUTE
 // ============================================================
 public int countFattureScadute() {

     int totale = 0;
     Connection conn = interfaccia.getDbManager().getConnessione();

     if (conn == null) return 0;

     try (PreparedStatement ps = conn.prepareStatement(
             """
             SELECT data_scadenza
             FROM fattura
             WHERE attivo = 1
               AND stato_pagamento != 'Pagata'
               AND data_scadenza IS NOT NULL
             """
     );
          ResultSet rs = ps.executeQuery()) {

         LocalDate oggi = LocalDate.now();

         while (rs.next()) {

             String data = rs.getString("data_scadenza");
             if (data == null || data.isBlank()) continue;

             LocalDate scadenza = DateUtils.dbToLocalDate(data);

             if (scadenza != null && scadenza.isBefore(oggi)) {
                 totale++;
             }
         }

     } catch (Exception e) {
         e.printStackTrace();
     }

     return totale;
 }


    public void aggiornaVistaCorrente() {
        if (archivioVisibile) {
            caricaFattureArchiviate();
        } else {
            caricaFatture();
        }
        aggiornaCardTotali();
    }


}
