package freeStudio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.jdatepicker.impl.JDatePickerImpl;


public class Progetti extends JPanel {

    // ============================================================
    // ======================= COMPONENTI =========================
    // ============================================================

    private JTable tabProgetti;
    private DefaultTableModel modelloTab;

    private JButton btnNuovo;
    private JButton btnModifica;
    private JButton btnMostraInfo;
    private JButton btnCerca;

    private JComboBox<String> comboCerca;
    private JTextField campoCerca;

    // Dialog
    private JDialog pannelloProgetto;
    private int idProgettoInModifica = -1;

    // Campi
    private JTextField campoTitolo;
    private JTextArea campoDescrizione;
    private JComboBox<String> comboStato;
    private JDatePickerImpl pickerDataInizio;
    private JDatePickerImpl pickerDataFine;
    private JTextField campoPreventivo;
    private JTextField campoCostoEffettivo;
    private JCheckBox checkFatturabile;
    private JComboBox<Integer> comboPriorita;
    private JTextArea campoNote;
    private JButton btnArchivia;
    private JButton btnRecupera;
    private JButton btnMostraArchivio;

    private JLabel lblArchivio;
    private boolean archivioVisibile = false;

    private JComboBox<String> comboClienti;
    private Map<String, Integer> mappaClienti = new HashMap<>();

    private Interfaccia interfaccia;

    // ============================================================
    // ======================== COSTRUTTORE =======================
    // ============================================================

    public Progetti(Interfaccia interfaccia) {
        this.interfaccia = interfaccia;

        setLayout(new BorderLayout());

        inizializzaHeader();
        creaTabella();
        caricaProgettiAttivi();
    }

    // ============================================================
    // ======================== HEADER ============================
    // ============================================================

    private void inizializzaHeader() {

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TITOLO
        JLabel titolo = new JLabel("FreeStudio - Progetti");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        
        JPanel pnlTitolo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitolo.add(titolo);
        pnlHeader.add(pnlTitolo, BorderLayout.NORTH);

        // BOTTONI
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
        
        
        // CERCA
        JPanel pnlCerca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboCerca = new JComboBox<>(new String[]{"Titolo", "Stato", "Tutti"});
        campoCerca = new JTextField(15);
        btnCerca = new JButton("Cerca");

        pnlCerca.add(new JLabel("Cerca per:"));
        pnlCerca.add(comboCerca);
        pnlCerca.add(campoCerca);
        pnlCerca.add(btnCerca);

        JPanel pnlOperazioni = new JPanel();
        pnlOperazioni.setLayout(new BoxLayout(pnlOperazioni, BoxLayout.Y_AXIS));
        pnlOperazioni.add(pnlBottoni);
        pnlOperazioni.add(pnlCerca);

        pnlHeader.add(pnlOperazioni, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        inizializzaListener();
    }

    // ============================================================
    // ======================== LISTENER ==========================
    // ============================================================

    private void inizializzaListener() {

        btnNuovo.addActionListener(e -> apriNuovoProgetto());
        btnModifica.addActionListener(e -> apriModificaProgetto());
        btnMostraInfo.addActionListener(e -> mostraInfoProgetto());
        btnCerca.addActionListener(e -> cercaProgetti());
        btnArchivia.addActionListener(e -> archiviaProgettoSelezionato());
        btnRecupera.addActionListener(e -> recuperaProgettoSelezionato());
        btnMostraArchivio.addActionListener(e -> toggleArchivio());

    }

    // ============================================================
    // ======================== TABELLA ===========================
    // ============================================================

    private void creaTabella() {

        // Modello dati della tabella
        modelloTab = new DefaultTableModel(
                new String[]{"ID", "Titolo", "Stato", "Data Inizio", "Priorità", "Costo"}, 0
        );

        // JTable
        tabProgetti = new JTable(modelloTab);

        // Etichetta ARCHIVIO (visibile solo in modalità archivio)
        lblArchivio = new JLabel("Archivio", SwingConstants.CENTER);
        lblArchivio.setFont(new Font("Arial", Font.BOLD, 16));
        lblArchivio.setVisible(false);

        // Pannello contenitore tabella + label
        JPanel pannelloTabella = new JPanel(new BorderLayout());
        pannelloTabella.add(lblArchivio, BorderLayout.NORTH);
        pannelloTabella.add(new JScrollPane(tabProgetti), BorderLayout.CENTER);

        // Inserimento nel pannello principale
        add(pannelloTabella, BorderLayout.CENTER);
    }


    // ============================================================
    // ======================== DIALOG ============================
    // ============================================================

    private void creaDialogProgetto() {

        pannelloProgetto = new JDialog((Frame) null, true);
        pannelloProgetto.setTitle("Progetto");
        pannelloProgetto.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridLayout(0, 4, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        pnl.setPreferredSize(new Dimension(700, 300));

        // =========================
        // CAMPI
        // =========================
        campoTitolo = new JTextField();
        FormStyle.textField(campoTitolo);

        comboStato = new JComboBox<>(new String[]{
                "In corso", "Completato", "Sospeso", "Annullato"
        });
        FormStyle.comboBox(comboStato);

        comboClienti = new JComboBox<>();
        FormStyle.comboBox(comboClienti);
        caricaClientiNelCombo();

        pickerDataInizio = DatePickerFactory.creaDatePicker();
        pickerDataFine   = DatePickerFactory.creaDatePicker();


        campoPreventivo = new JTextField();
        FormStyle.textField(campoPreventivo);

        campoCostoEffettivo = new JTextField();
        FormStyle.textField(campoCostoEffettivo);

        comboPriorita = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        FormStyle.comboBox(comboPriorita);

        checkFatturabile = new JCheckBox();

        // =========================
        // TEXT AREA
        // =========================
        campoDescrizione = new JTextArea();
        JScrollPane scrollDescrizione = FormStyle.textArea(campoDescrizione);

        campoNote = new JTextArea();
        JScrollPane scrollNote = FormStyle.textArea(campoNote);

        // =========================
        // RIGHE (7 × 2 CAMPI)
        // =========================
        aggiungiCampo(pnl, "Titolo", campoTitolo);
        aggiungiCampo(pnl, "Stato", comboStato);

        aggiungiCampo(pnl, "Cliente", comboClienti);
        aggiungiCampo(pnl, "Data Inizio", pickerDataInizio);
        
        aggiungiCampo(pnl, "Data Fine", pickerDataFine);
        aggiungiCampo(pnl, "Preventivo", campoPreventivo);

        aggiungiCampo(pnl, "Costo Effettivo", campoCostoEffettivo);
        aggiungiCampo(pnl, "Priorità", comboPriorita);

        aggiungiCampo(pnl, "Fatturabile", checkFatturabile);
        aggiungiCampo(pnl, "Descrizione", scrollDescrizione);

        aggiungiCampo(pnl, "Note", scrollNote);
        aggiungiCampo(pnl, "", new JLabel("")); // filler per chiudere la griglia

        // =========================
        // BOTTONE
        // =========================
        JButton btnConferma = new JButton("Conferma");
        btnConferma.addActionListener(e -> confermaProgetto());

        pannelloProgetto.add(pnl, BorderLayout.CENTER);
        pannelloProgetto.add(btnConferma, BorderLayout.SOUTH);

        pannelloProgetto.pack();
        pannelloProgetto.setLocationRelativeTo(null);
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
    // ======================== APERTURA ==========================
    // ============================================================

    private void apriNuovoProgetto() {
        idProgettoInModifica = -1;
        creaDialogProgetto();
        LocalDate oggi = LocalDate.now();

        pickerDataInizio.getModel().setDate(
                oggi.getYear(),
                oggi.getMonthValue() - 1,
                oggi.getDayOfMonth()
        );
        pickerDataInizio.getModel().setSelected(true);

        pickerDataFine.getModel().setSelected(false);

        pannelloProgetto.setTitle("Nuovo Progetto");
        pannelloProgetto.setVisible(true);
    }

    private void apriModificaProgetto() {

        int riga = tabProgetti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un progetto.");
            return;
        }

        idProgettoInModifica = (int) tabProgetti.getValueAt(riga, 0);

        creaDialogProgetto(); // prima creo i picker
        pannelloProgetto.setTitle("Modifica Progetto");
        caricaDatiProgettoNelDialog(idProgettoInModifica); // poi precompilo
        pannelloProgetto.setVisible(true);
    }


    // ============================================================
    // ======================== SALVATAGGIO =======================
    // ============================================================

    private void confermaProgetto() {

    	java.util.Date dInizio =
    	        (java.util.Date) pickerDataInizio.getModel().getValue();

    	java.util.Date dFine =
    	        (java.util.Date) pickerDataFine.getModel().getValue();

    	if (dInizio == null) {
    	    JOptionPane.showMessageDialog(this, "La data di inizio è obbligatoria.");
    	    return;
    	}

    	// ⛔ Data fine non può essere prima della data inizio
    	if (dFine != null && dFine.before(dInizio)) {
    	    JOptionPane.showMessageDialog(
    	        this,
    	        "La data di fine non può essere antecedente alla data di inizio.",
    	        "Errore date",
    	        JOptionPane.ERROR_MESSAGE
    	    );
    	    return;
    	}

    	
        if (campoTitolo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il titolo è obbligatorio.");
            return;
        }

        

        int conferma = JOptionPane.showConfirmDialog(
                this,
                idProgettoInModifica == -1 ?
                        "Confermare inserimento?" :
                        "Confermare modifica?",
                "Conferma",
                JOptionPane.YES_NO_OPTION);

        if (conferma != JOptionPane.YES_OPTION) return;

        

        Connection conn = interfaccia.getDbManager().getConnessione();
        try {


            PreparedStatement ps;

            if (idProgettoInModifica == -1)
                ps = conn.prepareStatement(Query.aggiungiProgetto());
            else
                ps = conn.prepareStatement(Query.modificaProgetto());

            int idCliente = mappaClienti.get(comboClienti.getSelectedItem().toString());

            ps.setInt(1, idCliente);
            ps.setString(2, campoTitolo.getText());
            ps.setString(3, campoDescrizione.getText());
            ps.setString(4, comboStato.getSelectedItem().toString());
            ps.setDate(5, new java.sql.Date(dInizio.getTime()));

            if (dFine != null)
                ps.setDate(6, new java.sql.Date(dFine.getTime()));
            else
                ps.setNull(6, Types.DATE);

            double preventivo = parseDoubleSafe(campoPreventivo.getText());
            double costoEffettivo = parseDoubleSafe(campoCostoEffettivo.getText());
            
         // ⛔ Non permettere valori negativi
            if (preventivo < 0 || costoEffettivo < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Preventivo e costo effettivo non possono essere negativi.",
                    "Errore importi",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }


            ps.setDouble(7, preventivo);
            ps.setDouble(8, costoEffettivo);

            ps.setBoolean(9, checkFatturabile.isSelected());
            ps.setInt(10, (Integer) comboPriorita.getSelectedItem());
            ps.setString(11, campoNote.getText());

            if (idProgettoInModifica != -1)
                ps.setInt(12, idProgettoInModifica);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    idProgettoInModifica == -1 ?
                            "Progetto inserito!" :
                            "Progetto modificato!");

            pannelloProgetto.dispose();
            aggiornaVistaCorrente();

           

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il salvataggio del progetto.\n\n" + ex.getMessage(),
                "Salvataggio progetto",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

    }

    // ============================================================
    // ======================== CARICAMENTO =======================
    // ============================================================

    private void caricaProgettiAttivi() {

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getProgettiAttivi());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titolo"),
                        rs.getString("stato"),
                        DateUtils.dbToUi(rs.getString("data_inizio")),
                        rs.getInt("priorita"),
                        rs.getDouble("costo_effettivo")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei progetti.\n\n" + e.getMessage(),
                "Caricamento progetti",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }
    
    private void caricaProgettiArchiviati() {

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getProgettiArchiviati());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titolo"),
                        rs.getString("stato"),
                        DateUtils.dbToUi(rs.getString("data_inizio")),
                        rs.getInt("priorita"),
                        rs.getDouble("costo_effettivo")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei progetti.\n\n" + e.getMessage(),
                "Caricamento progetti",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }


    // ============================================================
    // ========================== CERCA ===========================
    // ============================================================

    private void cercaProgetti() {

        String valore = campoCerca.getText().trim();
        String filtro = comboCerca.getSelectedItem().toString();

        // Controllo UX: evita ricerche inutili
        if (!filtro.equals("Tutti") && valore.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un valore di ricerca.");
            return;
        }

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {

            PreparedStatement ps;

            // =========================
            // MODALITÀ ARCHIVIO
            // =========================
            if (archivioVisibile) {

                switch (filtro) {
                    case "Titolo" -> {
                        ps = conn.prepareStatement(Query.getProgettiArchiviatiByTitolo());
                        ps.setString(1, "%" + valore + "%");
                    }
                    case "Stato" -> {
                        ps = conn.prepareStatement(Query.getProgettiArchiviatiByStato());
                        ps.setString(1, valore);
                    }
                    default -> {
                        ps = conn.prepareStatement(Query.getProgettiArchiviati());
                    }
                }

            // =========================
            // MODALITÀ ATTIVI
            // =========================
            } else {

                switch (filtro) {
                    case "Titolo" -> {
                        ps = conn.prepareStatement(Query.getProgettiAttiviByTitolo());
                        ps.setString(1, "%" + valore + "%");
                    }
                    case "Stato" -> {
                        ps = conn.prepareStatement(Query.getProgettiAttiviByStato());
                        ps.setString(1, valore);
                    }
                    default -> {
                        ps = conn.prepareStatement(Query.getProgettiAttivi());
                    }
                }
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titolo"),
                        rs.getString("stato"),
                        DateUtils.dbToUi(rs.getString("data_inizio")),
                        rs.getInt("priorita"),
                        rs.getDouble("costo_effettivo")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante la ricerca dei progetti.\n\n" + ex.getMessage(),
                "Ricerca progetti",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }


    // ============================================================
    // ====================== CLIENTI COMBO =======================
    // ============================================================

    private void caricaClientiNelCombo() {

        mappaClienti.clear();
        comboClienti.removeAllItems();

        Connection conn = interfaccia.getDbManager().getConnessione();
        try (PreparedStatement ps = conn.prepareStatement(Query.getClientiAttivi());
             ResultSet rs = ps.executeQuery()) {


            while (rs.next()) {
                String label = rs.getString("nome") + " " + rs.getString("cognome");
                comboClienti.addItem(label);
                mappaClienti.put(label, rs.getInt("id"));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei clienti.\n\n" + ex.getMessage(),
                "Caricamento clienti",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

    }

    

    // ============================================================
    // ========================= MOSTRA INFO ======================
    // ============================================================

    private void mostraInfoProgetto() {

        int riga = tabProgetti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un progetto.");
            return;
        }

        int id = (int) tabProgetti.getValueAt(riga, 0);
        Connection conn = interfaccia.getDbManager().getConnessione();
        try (
             PreparedStatement ps = conn.prepareStatement(Query.getProgettoById())) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String info =
                        "ID: " + rs.getInt("id") + "\n\n" +
                                "Titolo: " + rs.getString("titolo") + "\n" +
                                "Descrizione: " + rs.getString("descrizione") + "\n\n" +
                                "Cliente: " + rs.getString("nome") + " " + rs.getString("cognome") + "\n\n" +
                                "Stato: " + rs.getString("stato") + "\n" +
                                "Data Inizio: " + DateUtils.dbToUi(rs.getString("data_inizio")) + "\n" +
                                "Data Fine: " + DateUtils.dbToUi(rs.getString("data_fine")) + "\n\n" +
                                "Preventivo: € " + rs.getDouble("preventivo") + "\n" +
                                "Costo Effettivo: € " + rs.getDouble("costo_effettivo") + "\n\n" +
                                "Priorità: " + rs.getInt("priorita") + "\n" +
                                "Fatturabile: " + (rs.getBoolean("fatturabile") ? "Sì" : "No") + "\n\n" +
                                "Note: " + rs.getString("note");

                JOptionPane.showMessageDialog(this, info, "Dettagli Progetto",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento delle informazioni del progetto.\n\n" + ex.getMessage(),
                "Dettagli progetto",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

    }

    // ============================================================
    // ======================= DATI PER DIALOG ====================
    // ============================================================

    private void caricaDatiProgettoNelDialog(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getProgettoById())) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    campoTitolo.setText(rs.getString("titolo"));
                    campoDescrizione.setText(rs.getString("descrizione"));
                    comboStato.setSelectedItem(rs.getString("stato"));

                    // =========================
                    // DATA INIZIO (FIX)
                    // =========================
                    LocalDate dataInizio = DateUtils.dbToLocalDate(rs.getString("data_inizio"));
                    if (dataInizio != null) {

                        pickerDataInizio.getModel().setDate(
                                dataInizio.getYear(),
                                dataInizio.getMonthValue() - 1,
                                dataInizio.getDayOfMonth()
                        );
                        pickerDataInizio.getModel().setSelected(true);

                        // 🔑 FIX VISIVO
                        pickerDataInizio.getJFormattedTextField().setText(
                                DateUtils.formattaUi(dataInizio)
                        );
                    }


                    // =========================
                    // DATA FINE (FIX)
                    // =========================
                    LocalDate dataFine = DateUtils.dbToLocalDate(rs.getString("data_fine"));
                    if (dataFine != null) {

                        pickerDataFine.getModel().setDate(
                                dataFine.getYear(),
                                dataFine.getMonthValue() - 1,
                                dataFine.getDayOfMonth()
                        );
                        pickerDataFine.getModel().setSelected(true);

                        // 🔑 FIX VISIVO
                        pickerDataFine.getJFormattedTextField().setText(
                                DateUtils.formattaUi(dataFine)
                        );
                    }


                    campoPreventivo.setText(String.valueOf(rs.getDouble("preventivo")));
                    campoCostoEffettivo.setText(String.valueOf(rs.getDouble("costo_effettivo")));

                    comboPriorita.setSelectedItem(rs.getInt("priorita"));
                    checkFatturabile.setSelected(rs.getBoolean("fatturabile"));
                    campoNote.setText(rs.getString("note"));

                    String cliente = rs.getString("nome") + " " + rs.getString("cognome");
                    comboClienti.setSelectedItem(cliente);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei dati del progetto.\n\n" + ex.getMessage(),
                "Modifica progetto",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

    }



    // ============================================================
    // ================== PROGETTI IN SCADENZA ====================
    // ============================================================

    public int countProgettiInScadenza() {

        int totale = 0;

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT data_fine FROM progetto WHERE data_fine IS NOT NULL");
             ResultSet rs = ps.executeQuery()) {

            LocalDate oggi = LocalDate.now();

            while (rs.next()) {

                LocalDate dataFine = rs.getDate("data_fine").toLocalDate();
                long giorni = ChronoUnit.DAYS.between(oggi, dataFine);

                if (giorni >= 0 && giorni <= 7)
                    totale++;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totale;
    }

    private void archiviaProgettoSelezionato() {

        int riga = tabProgetti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un progetto.");
            return;
        }

        int id = (int) tabProgetti.getValueAt(riga, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Archiviare questo progetto?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            archiviaProgetto(id);
            aggiornaVistaCorrente();
        }
    }

    private void recuperaProgettoSelezionato() {

        int riga = tabProgetti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un progetto dall'archivio.");
            return;
        }

        int id = (int) tabProgetti.getValueAt(riga, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Recuperare questo progetto?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            recuperaProgetto(id);
            aggiornaVistaCorrente();
        }
    }


    private void archiviaProgetto(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.archiviaProgetto())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Progetto archiviato!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante l'archiviazione del progetto.\n\n" + e.getMessage(),
                    "Archivio progetti",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void recuperaProgetto(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.recuperaProgetto())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Progetto recuperato!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante il recupero del progetto.\n\n" + e.getMessage(),
                    "Archivio progetti",
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
            btnMostraArchivio.setText("Mostra Attivi");
            caricaProgettiArchiviati();
        } else {
            lblArchivio.setVisible(false);
            btnRecupera.setVisible(false);
            btnArchivia.setVisible(true);
            btnMostraArchivio.setText("Mostra Archivio");
            caricaProgettiAttivi();
        }
    }

    public void aggiornaVistaCorrente() {
        if (archivioVisibile) {
            caricaProgettiArchiviati();
        } else {
            caricaProgettiAttivi();
        }
    }


    /**
     * Converte una stringa in double in modo sicuro.
     * - Accetta virgola o punto
     * - Campo vuoto → 0
     */
    private double parseDoubleSafe(String testo) {

        if (testo == null) {
            return 0.0;
        }

        testo = testo.trim();

        if (testo.isEmpty()) {
            return 0.0;
        }

        // Converte la virgola in punto (formato italiano)
        testo = testo.replace(",", ".");

        try {
            return Double.parseDouble(testo);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    
}
