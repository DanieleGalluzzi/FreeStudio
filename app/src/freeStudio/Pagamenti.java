package freeStudio;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import org.jdatepicker.impl.JDatePickerImpl;
import java.time.LocalDate;


public class Pagamenti extends JPanel {

    private JTable tabPagamenti;
    private DefaultTableModel modelloTab;

    private JButton btnNuovo;
    private JButton btnModifica;
    private JButton btnMostraInfo;
    private JButton btnCerca;

    private JComboBox<String> comboCercaMetodo;
    private JComboBox<String> comboCerca;
    private JTextField campoCerca;

    private JDialog pannelloPagamento;
    private int idPagamentoInModifica = -1;

    private JComboBox<String> comboFatture;
    private JTextField campoImportoPagato;
    private JComboBox<String> comboMetodo;
    private JTextArea campoNote;

    private JLabel lblResiduo;

    private Map<String, Integer> mappaFatture = new HashMap<>();
    private JDatePickerImpl pickerDataPagamento;
    private final Interfaccia interfaccia;
    
    private JPanel pnlCercaInput;
    private CardLayout cardCerca;
    private JDatePickerImpl pickerCercaDataPagamento;
    private JButton btnArchivia;
    private JButton btnRecupera;
    private JButton btnMostraArchivio;

    private JLabel lblArchivio;
    private boolean archivioVisibile = false;


    public Pagamenti(Interfaccia interfaccia) {
        this.interfaccia = interfaccia;

        setLayout(new BorderLayout());
        inizializzaUI();
        creaTabella();
        caricaPagamenti();
    }

    // ================================================================
    // HEADER + BOTTONI
    // ================================================================
    private void inizializzaUI() {

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titolo = new JLabel("FreeStudio – Pagamenti");
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

        comboCerca = new JComboBox<>(new String[]{
                "Data",
                "Importo",
                "Metodo",
                "Tutti"
        });

        campoCerca = new JTextField(15);
        btnCerca = new JButton("Cerca");

        pickerCercaDataPagamento = DatePickerFactory.creaDatePicker();

        cardCerca = new CardLayout();
        pnlCercaInput = new JPanel(cardCerca);
        pnlCercaInput.add(campoCerca, "TEXT");
        pnlCercaInput.add(pickerCercaDataPagamento, "DATE");

        cardCerca.show(pnlCercaInput, "TEXT");

        pnlCerca.add(new JLabel("Cerca per:"));
        pnlCerca.add(comboCerca);
        pnlCerca.add(pnlCercaInput);
        pnlCerca.add(btnCerca);

        comboCerca.addActionListener(e -> {
            String filtro = comboCerca.getSelectedItem().toString();
            if (filtro.equals("Data")) {
                cardCerca.show(pnlCercaInput, "DATE");
                campoCerca.setText("");
            } else {
                cardCerca.show(pnlCercaInput, "TEXT");
                pickerCercaDataPagamento.getModel().setValue(null);
                pickerCercaDataPagamento.getModel().setSelected(false);
                pickerCercaDataPagamento.getJFormattedTextField().setText("");
            }
        });



        JPanel pnlOperazioni = new JPanel();
        pnlOperazioni.setLayout(new BoxLayout(pnlOperazioni, BoxLayout.Y_AXIS));
        pnlOperazioni.add(pnlBottoni);
        pnlOperazioni.add(pnlCerca);

        pnlHeader.add(pnlOperazioni, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // LISTENER
        btnNuovo.addActionListener(e -> apriNuovoPagamento());
        btnModifica.addActionListener(e -> apriModificaPagamento());
        btnMostraInfo.addActionListener(e -> mostraInfoPagamento());
        btnCerca.addActionListener(e -> cercaPagamenti());
        btnArchivia.addActionListener(e -> archiviaPagamentoSelezionato());
        btnRecupera.addActionListener(e -> recuperaPagamentoSelezionato());
        btnMostraArchivio.addActionListener(e -> toggleArchivio());

    }

    // ================================================================
    // TABELLA
    // ================================================================
    private void creaTabella() {

        // =========================
        // MODELLO DATI TABELLA
        // =========================
        modelloTab = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Numero Fattura",
                        "Data Pagamento",
                        "Importo Pagato",
                        "Metodo"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabella sola lettura
            }
        };

        // =========================
        // JTABLE
        // =========================
        tabPagamenti = new JTable(modelloTab);
        tabPagamenti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabPagamenti.setRowHeight(24);

        // =========================
        // LABEL ARCHIVIO
        // =========================
        lblArchivio = new JLabel("Archivio", SwingConstants.CENTER);
        lblArchivio.setFont(new Font("Arial", Font.BOLD, 16));
        lblArchivio.setForeground(Color.DARK_GRAY);
        lblArchivio.setVisible(false);

        // =========================
        // PANNELLO CONTENITORE
        // =========================
        JPanel pannelloTabella = new JPanel(new BorderLayout());
        pannelloTabella.add(lblArchivio, BorderLayout.NORTH);
        pannelloTabella.add(new JScrollPane(tabPagamenti), BorderLayout.CENTER);

        // =========================
        // AGGIUNTA AL LAYOUT
        // =========================
        add(pannelloTabella, BorderLayout.CENTER);
    }


    // ================================================================
    // CARICA PAGAMENTI
    // ================================================================
    private void caricaPagamenti() {

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps = conn.prepareStatement(Query.getPagamentiAttivi());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("numero_fattura"),
                        DateUtils.dbToUi(rs.getString("data_pagamento")),
                        rs.getDouble("importo_pagato"),
                        rs.getString("metodo")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei pagamenti.\n\n" + e.getMessage(),
                "Caricamento pagamenti",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    // ================================================================
    // CERCA PAGAMENTI
    // ================================================================
    private void cercaPagamenti() {

        // Svuota la tabella
        modelloTab.setRowCount(0);

        String filtro = comboCerca.getSelectedItem().toString();
        String valore = campoCerca.getText().trim();

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {

            PreparedStatement ps;

            switch (filtro) {

                case "Data": {

                    java.util.Date d =
                            (java.util.Date) pickerCercaDataPagamento.getModel().getValue();

                    if (d == null) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Seleziona una data dal calendario."
                        );
                        return;
                    }

                    ps = conn.prepareStatement(Query.getPagamentiByData());
                    ps.setDate(1, new java.sql.Date(d.getTime()));
                    break;
                }

                case "Importo": {

                    if (valore.isEmpty()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Inserisci un importo."
                        );
                        return;
                    }

                    double importo = Double.parseDouble(valore.replace(",", "."));
                    ps = conn.prepareStatement(Query.getPagamentiByImporto());
                    ps.setDouble(1, importo);
                    break;
                }

                case "Metodo": {

                    if (valore.isEmpty()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Inserisci un metodo di pagamento."
                        );
                        return;
                    }

                    ps = conn.prepareStatement(Query.getPagamentiByMetodo());
                    ps.setString(1, valore);
                    break;
                }

                default: {
                    // ✅ QUERY CORRETTA
                    ps = conn.prepareStatement(Query.getPagamentiAttivi());
                    break;
                }
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("numero_fattura"),
                        DateUtils.dbToUi(rs.getString("data_pagamento")),
                        rs.getDouble("importo_pagato"),
                        rs.getString("metodo")
                });
            }

            rs.close();
            ps.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Importo non valido.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante la ricerca dei pagamenti.\n\n" + e.getMessage(),
                    "Ricerca pagamenti",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }



    // ================================================================
    // NUOVO PAGAMENTO
    // ================================================================
    private void apriNuovoPagamento() {
        idPagamentoInModifica = -1;
        creaDialogPagamento();
        LocalDate oggi = LocalDate.now();

        pickerDataPagamento.getModel().setDate(
                oggi.getYear(),
                oggi.getMonthValue() - 1,
                oggi.getDayOfMonth()
        );
        pickerDataPagamento.getModel().setSelected(true);

        pannelloPagamento.setTitle("Nuovo Pagamento");
        pannelloPagamento.setVisible(true);
    }

    // ================================================================
    // MODIFICA PAGAMENTO
    // ================================================================
    private void apriModificaPagamento() {

        int riga = tabPagamenti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un pagamento.");
            return;
        }

        idPagamentoInModifica = (int) tabPagamenti.getValueAt(riga, 0);

        creaDialogPagamento();
        pannelloPagamento.setTitle("Modifica Pagamento");
        caricaDatiPagamentoNelDialog(idPagamentoInModifica);
        pannelloPagamento.setVisible(true);
    }

    // ================================================================
    // DIALOG PAGAMENTO
    // ================================================================
    private void creaDialogPagamento() {

        pannelloPagamento = new JDialog((Frame) null, true);
        pannelloPagamento.setTitle("Pagamento");
        pannelloPagamento.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridLayout(0, 4, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        pnl.setPreferredSize(new Dimension(700, 300));

        // =========================
        // CAMPI
        // =========================
        comboFatture = new JComboBox<>();
        FormStyle.comboBox(comboFatture);
        mappaFatture.clear();
        caricaFattureNelCombo();

        pickerDataPagamento = DatePickerFactory.creaDatePicker();


        campoImportoPagato = new JTextField();
        FormStyle.textField(campoImportoPagato);

        comboMetodo = new JComboBox<>(new String[]{
                "Contanti", "Bonifico", "Carta", "POS"
        });
        FormStyle.comboBox(comboMetodo);

        lblResiduo = new JLabel("€0.00");
        FormStyle.labelValue(lblResiduo);

        // =========================
        // TEXT AREA NOTE
        // =========================
        campoNote = new JTextArea();
        JScrollPane scrollNote = FormStyle.textArea(campoNote);

        // =========================
        // RIGHE (7 × 2 CAMPI)
        // =========================
        aggiungiCampo(pnl, "Fattura", comboFatture);
        aggiungiCampo(pnl, "Data Pagamento", pickerDataPagamento);

        aggiungiCampo(pnl, "Importo Pagato", campoImportoPagato);
        aggiungiCampo(pnl, "Metodo", comboMetodo);

        aggiungiCampo(pnl, "Residuo", lblResiduo);
        aggiungiCampo(pnl, "Note", scrollNote);

        // ===== filler per uniformare altezza =====
        aggiungiCampo(pnl, "", new JLabel(""));
        aggiungiCampo(pnl, "", new JLabel(""));
        
        aggiungiCampo(pnl, "", new JLabel(""));
        aggiungiCampo(pnl, "", new JLabel(""));

        aggiungiCampo(pnl, "", new JLabel(""));
        aggiungiCampo(pnl, "", new JLabel(""));


        // =========================
        // LISTENER
        // =========================
        comboFatture.addActionListener(e -> aggiornaResiduo());
        campoImportoPagato.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { aggiornaResiduo(); }
            public void removeUpdate(DocumentEvent e) { aggiornaResiduo(); }
            public void insertUpdate(DocumentEvent e) { aggiornaResiduo(); }
        });

        // =========================
        // BOTTONE
        // =========================
        JButton btnConferma = new JButton("Conferma");
        btnConferma.addActionListener(e -> confermaPagamento());

        pannelloPagamento.add(pnl, BorderLayout.CENTER);
        pannelloPagamento.add(btnConferma, BorderLayout.SOUTH);

        pannelloPagamento.pack();
        pannelloPagamento.setLocationRelativeTo(null);
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


    // ================================================================
    // RESIDUO DINAMICO
    // ================================================================
    private void aggiornaResiduo() {

        if (comboFatture.getSelectedItem() == null) {
            lblResiduo.setText("Residuo: €0.00");
            return;
        }

        int idFattura = mappaFatture.get(comboFatture.getSelectedItem().toString());

        double totale = getImportoTotaleFattura(idFattura);
        double pagato = getTotalePagamenti(idFattura, idPagamentoInModifica);

        double residuo = Math.max(0, totale - pagato);
        lblResiduo.setText("Residuo: €" + residuo);

    }

    // ================================================================
    // CONFERMA PAGAMENTO
    // ================================================================
    private void confermaPagamento() {

        // =========================
        // DATA (DATE PICKER)
        // =========================
        java.util.Date dPagamento =
                (java.util.Date) pickerDataPagamento.getModel().getValue();

        if (dPagamento == null) {
            JOptionPane.showMessageDialog(this, "Data pagamento obbligatoria.");
            return;
        }

        // =========================
        // FATTURA
        // =========================
        if (comboFatture.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una fattura.");
            return;
        }

        int idFattura = mappaFatture.get(
                comboFatture.getSelectedItem().toString()
        );

        // =========================
        // IMPORTO
        // =========================
        if (campoImportoPagato.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un importo.");
            return;
        }

        double nuovoImporto;
        try {
            nuovoImporto = Double.parseDouble(
                    campoImportoPagato.getText().replace(",", ".")
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Importo non valido.");
            return;
        }

        // =========================
        // CONTROLLO RESIDUO
        // =========================
        double totale = getImportoTotaleFattura(idFattura);
        double pagato = getTotalePagamenti(idFattura, idPagamentoInModifica);
        double residuo = totale - pagato;

        if (nuovoImporto > residuo) {
            JOptionPane.showMessageDialog(
                    this,
                    "Importo maggiore del residuo!\n\n" +
                            "Totale: €" + totale +
                            "\nPagato: €" + pagato +
                            "\nResiduo: €" + residuo,
                    "Errore",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // =========================
        // SALVATAGGIO DB
        // =========================
        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps =
                    (idPagamentoInModifica == -1)
                            ? conn.prepareStatement(Query.aggiungiPagamento())
                            : conn.prepareStatement(Query.modificaPagamento());

            ps.setInt(1, idFattura);
            ps.setDate(2, new java.sql.Date(dPagamento.getTime()));
            ps.setDouble(3, nuovoImporto);
            ps.setString(4, comboMetodo.getSelectedItem().toString());
            ps.setString(5, campoNote.getText());

            if (idPagamentoInModifica != -1) {
                ps.setInt(6, idPagamentoInModifica);
            }

            ps.executeUpdate();
            ps.close();

            // =========================
            // POST-SAVE
            // =========================
            pannelloPagamento.dispose();

            aggiornaStatoFattura(idFattura);
            interfaccia.aggiornaFatture();
            caricaPagamenti();

            JOptionPane.showMessageDialog(
                    this,
                    idPagamentoInModifica == -1
                            ? "Pagamento aggiunto!"
                            : "Pagamento modificato!"
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il salvataggio del pagamento.\n\n" + e.getMessage(),
                "Salvataggio pagamento",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    private void archiviaPagamentoSelezionato() {

        int riga = tabPagamenti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un pagamento.");
            return;
        }

        int id = (int) tabPagamenti.getValueAt(riga, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Archiviare questo pagamento?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            archiviaPagamento(id);
            aggiornaVistaCorrente();
        }
    }

    private void recuperaPagamentoSelezionato() {

        int riga = tabPagamenti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un pagamento dall'archivio.");
            return;
        }

        int id = (int) tabPagamenti.getValueAt(riga, 0);

        if (JOptionPane.showConfirmDialog(
                this,
                "Recuperare questo pagamento?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {

            recuperaPagamento(id);
            aggiornaVistaCorrente();
        }
    }

    private void archiviaPagamento(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps =
                     conn.prepareStatement(Query.archiviaPagamento())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pagamento archiviato!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recuperaPagamento(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps =
                     conn.prepareStatement(Query.recuperaPagamento())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pagamento recuperato!");

        } catch (Exception e) {
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
            caricaPagamentiArchiviati();
        } else {
            lblArchivio.setVisible(false);
            btnRecupera.setVisible(false);
            btnArchivia.setVisible(true);
            btnMostraArchivio.setText("Mostra Archivio");
            caricaPagamenti();
        }
    }

    private void caricaPagamentiArchiviati() {

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try (PreparedStatement ps =
                     conn.prepareStatement(Query.getPagamentiArchiviati());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("numero_fattura"),
                        DateUtils.dbToUi(rs.getString("data_pagamento")),
                        rs.getDouble("importo_pagato"),
                        rs.getString("metodo")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aggiornaVistaCorrente() {
        if (archivioVisibile) {
            caricaPagamentiArchiviati();
        } else {
            caricaPagamenti();
        }
    }


    // ================================================================
    // METODI DI CALCOLO
    // ================================================================
    private double getImportoTotaleFattura(int idFattura) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT importo FROM fattura WHERE id = ?");
            ps.setInt(1, idFattura);

            ResultSet rs = ps.executeQuery();

            double val = rs.next() ? rs.getDouble("importo") : 0;

            rs.close();
            ps.close();

            return val;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double getTotalePagamenti(int idFattura, int idDaEscludere) {

        String sql = "SELECT SUM(importo_pagato) FROM pagamento WHERE id_fattura = ?";

        if (idDaEscludere != -1)
            sql += " AND id <> ?";

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idFattura);

            if (idDaEscludere != -1)
                ps.setInt(2, idDaEscludere);

            ResultSet rs = ps.executeQuery();

            double val = rs.next() ? rs.getDouble(1) : 0;

            rs.close();
            ps.close();

            return val;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ================================================================
    // CARICA FATTURE NEL COMBOBOX
    // ================================================================
    private void caricaFattureNelCombo() {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = conn.prepareStatement(Query.getFatturePagabili());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String label = rs.getString("numero_fattura") + " – €" + rs.getDouble("importo");
                comboFatture.addItem(label);
                mappaFatture.put(label, rs.getInt("id"));
            }

            rs.close();
            ps.close();

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

    // ================================================================
    // CARICA DATI PER MODIFICA
    // ================================================================
    private void caricaDatiPagamentoNelDialog(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = conn.prepareStatement(Query.getPagamentoById());
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String label = rs.getString("numero_fattura") + " – €" + rs.getDouble("importo");
                comboFatture.setSelectedItem(label);

                LocalDate data = DateUtils.dbToLocalDate(rs.getString("data_pagamento"));
                if (data != null) {

                    // 1️⃣ aggiorna il MODEL
                    pickerDataPagamento.getModel().setDate(
                            data.getYear(),
                            data.getMonthValue() - 1,
                            data.getDayOfMonth()
                    );
                    pickerDataPagamento.getModel().setSelected(true);

                    // 2️⃣ aggiorna MANUALMENTE il campo testo (fondamentale)
                    pickerDataPagamento.getJFormattedTextField().setText(
                            DateUtils.formattaUi(data)
                    );
                }

                campoImportoPagato.setText(String.valueOf(rs.getDouble("importo_pagato")));
                comboMetodo.setSelectedItem(rs.getString("metodo"));
                campoNote.setText(rs.getString("note"));

                aggiornaResiduo();
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento del pagamento.\n\n" + e.getMessage(),
                "Modifica pagamento",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

   

    // ================================================================
    // MOSTRA INFO
    // ================================================================
    private void mostraInfoPagamento() {

        int riga = tabPagamenti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un pagamento.");
            return;
        }

        int id = (int) tabPagamenti.getValueAt(riga, 0);

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = conn.prepareStatement(Query.getPagamentoById());
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String info =
                        "ID: " + rs.getInt("id") + "\n" +
                                "Fattura: " + rs.getString("numero_fattura") + "\n" +
                                "Data: " + DateUtils.dbToUi(rs.getString("data_pagamento")) + "\n" +
                                "Importo: €" + rs.getDouble("importo_pagato") + "\n" +
                                "Metodo: " + rs.getString("metodo") + "\n" +
                                "\nNote:\n" + rs.getString("note");

                JOptionPane.showMessageDialog(this, info,
                        "Dettagli Pagamento", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento delle informazioni del pagamento.\n\n" + e.getMessage(),
                "Dettagli pagamento",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    // ================================================================
    // RECUPERA ID FATTURA DA PAGAMENTO
    // ================================================================
    private int getIdFatturaDaPagamento(int idPagamento) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id_fattura FROM pagamento WHERE id = ?"
            );

            ps.setInt(1, idPagamento);

            ResultSet rs = ps.executeQuery();

            int val = rs.next() ? rs.getInt("id_fattura") : -1;

            rs.close();
            ps.close();

            return val;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ================================================================
    // AGGIORNA STATO FATTURA
    // ================================================================
    private void aggiornaStatoFattura(int idFattura) {

        Connection conn = interfaccia.getDbManager().getConnessione();

        try {

            double totalePagato = 0.0;
            double importoFattura = 0.0;

            // =========================
            // TOTALE PAGATO
            // =========================
            try (PreparedStatement ps = conn.prepareStatement(
                    Query.totalePagatoFattura())) {

                ps.setInt(1, idFattura);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    totalePagato = rs.getDouble(1);
                }
            }

            // =========================
            // IMPORTO FATTURA
            // =========================
            try (PreparedStatement ps = conn.prepareStatement(
                    Query.importoFattura())) {

                ps.setInt(1, idFattura);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    importoFattura = rs.getDouble(1);
                }
            }

            // =========================
            // CALCOLO STATO
            // =========================
            String stato;

            if (totalePagato <= 0) {
                stato = "Non pagata";
            } else if (totalePagato < importoFattura) {
                stato = "Parziale";
            } else {
                stato = "Pagata";
            }

            // =========================
            // UPDATE FATTURA
            // =========================
            try (PreparedStatement ps = conn.prepareStatement(
                    Query.aggiornaStatoFattura())) {

                ps.setString(1, stato);
                ps.setInt(2, idFattura);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
}
