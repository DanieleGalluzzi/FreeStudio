package freeStudio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;

public class Clienti extends JPanel {

    // ============================================================
    // COMPONENTI
    // ============================================================

    private JTable tabClienti;
    private DefaultTableModel modelloTab;
    private JLabel lblArchivio;
    private boolean archivioVisibile = false;

    private JButton btnNuovo;
    private JButton btnModifica;
    private JButton btnArchivia;
    private JButton btnMostraArchivio;
    private JButton btnMostraInfo;
    private JButton btnRecupera;

    private JComboBox<String> comboCerca;
    private JTextField campoCerca;
    private JButton btnCerca;

    private JDialog pannelloCliente;
    private int idClienteInModifica = -1;

    private JTextField campoNome;
    private JTextField campoCognome;
    private JTextField campoRagioneSociale;
    private JTextField campoPartitaIVA;
    private JTextField campoCodiceFiscale;
    private JTextField campoEmail;
    private JTextField campoTelefono;
    private JTextField campoIndirizzo;
    private JTextField campoCitta;
    private JTextField campoCap;
    private JTextField campoProvincia;
    private JTextField campoPaese;
    private JTextArea campoNote;
    private JTextField campoDataCreazione;

    private final Interfaccia interfaccia;

    // ============================================================
    // COSTRUTTORE
    // ============================================================

    public Clienti(Interfaccia interfaccia) {
        this.interfaccia = interfaccia;
        setLayout(new BorderLayout());

        inizializzaComponenti();
        creaTabella();
        caricaDatiAttiviInTabella();
    }

    // ============================================================
    // COSTRUZIONE HEADER
    // ============================================================

    private void inizializzaComponenti() {

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titolo = new JLabel("FreeStudio - Clienti");
        titolo.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel pnlTitolo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTitolo.add(titolo);
        pnlHeader.add(pnlTitolo, BorderLayout.NORTH);

        JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnNuovo = new JButton("Nuovo");
        btnModifica = new JButton("Modifica");
        btnArchivia = new JButton("Archivia");
        btnMostraArchivio = new JButton("Mostra Archivio");
        btnMostraInfo = new JButton("Mostra Info");
        btnRecupera = new JButton("Recupera");
        btnRecupera.setVisible(false);

        pnlBottoni.add(btnNuovo);
        pnlBottoni.add(btnModifica);
        pnlBottoni.add(btnArchivia);
        pnlBottoni.add(btnRecupera);
        pnlBottoni.add(btnMostraArchivio);
        pnlBottoni.add(btnMostraInfo);

        JPanel pnlCerca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboCerca = new JComboBox<>(new String[]{
                "Nome", "Cognome", "Ragione Sociale", "Email", "Telefono", "Tutti"
        });

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

        aggiungiListenerBottoni();
    }

    // ============================================================
    // LISTENER
    // ============================================================

    private void aggiungiListenerBottoni() {

        btnNuovo.addActionListener(e -> apriPannelloNuovoCliente());

        btnModifica.addActionListener(e -> {
            int riga = tabClienti.getSelectedRow();
            if (riga == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un cliente.");
                return;
            }
            idClienteInModifica = (int) tabClienti.getValueAt(riga, 0);
            apriPannelloModificaCliente(idClienteInModifica);
        });

        btnArchivia.addActionListener(e -> archiviaClienteSelezionato());
        btnRecupera.addActionListener(e -> recuperaClienteSelezionato());
        btnMostraArchivio.addActionListener(e -> toggleArchivio());
        btnMostraInfo.addActionListener(e -> mostraInfoClienteSelezionato());
        btnCerca.addActionListener(e -> cercaClienti());
    }

    // ============================================================
    // TABELLA
    // ============================================================

    private void creaTabella() {

        modelloTab = new DefaultTableModel(
                new String[]{"ID", "Nome", "Cognome", "Ragione Sociale", "Email", "Telefono"}, 0
        );

        tabClienti = new JTable(modelloTab);

        lblArchivio = new JLabel("Archivio", SwingConstants.CENTER);
        lblArchivio.setFont(new Font("Arial", Font.BOLD, 16));
        lblArchivio.setVisible(false);

        JPanel pannelloTabella = new JPanel(new BorderLayout());
        pannelloTabella.add(lblArchivio, BorderLayout.NORTH);
        pannelloTabella.add(new JScrollPane(tabClienti), BorderLayout.CENTER);

        add(pannelloTabella, BorderLayout.CENTER);
    }

    // ============================================================
    // DIALOG CLIENTE
    // ============================================================

    private void creaDialogCliente() {

        pannelloCliente = new JDialog((Frame) null, true);
        pannelloCliente.setTitle("Cliente");
        pannelloCliente.setLayout(new BorderLayout());

        // GridLayout standard FreeStudio
        JPanel pnl = new JPanel(new GridLayout(0, 4, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        pnl.setPreferredSize(new Dimension(700, 300));

        // =========================
        // CREAZIONE CAMPI
        // =========================
        campoNome = new JTextField();
        FormStyle.textField(campoNome);

        campoCognome = new JTextField();
        FormStyle.textField(campoCognome);

        campoRagioneSociale = new JTextField();
        FormStyle.textField(campoRagioneSociale);

        campoPartitaIVA = new JTextField();
        FormStyle.textField(campoPartitaIVA);

        campoCodiceFiscale = new JTextField();
        FormStyle.textField(campoCodiceFiscale);

        campoEmail = new JTextField();
        FormStyle.textField(campoEmail);

        campoTelefono = new JTextField();
        campoTelefono.setToolTipText("Inserire solo numeri. È ammesso il prefisso + solo all'inizio (es. +393401234567)");
        FormStyle.textField(campoTelefono);

        campoIndirizzo = new JTextField();
        FormStyle.textField(campoIndirizzo);

        campoCitta = new JTextField();
        FormStyle.textField(campoCitta);

        campoCap = new JTextField();
        FormStyle.textField(campoCap);

        campoProvincia = new JTextField();
        FormStyle.textField(campoProvincia);

        campoPaese = new JTextField();
        FormStyle.textField(campoPaese);

        campoDataCreazione = new JTextField();
        campoDataCreazione.setEditable(false);
        FormStyle.textField(campoDataCreazione);

        // =========================
        // TEXT AREA NOTE (con scroll)
        // =========================
        campoNote = new JTextArea();
        JScrollPane scrollNote = FormStyle.textArea(campoNote);

        // =========================
        // RIGHE (7 × 2 CAMPI)
        // =========================
        aggiungiCampo(pnl, "Nome*", campoNome);
        aggiungiCampo(pnl, "Cognome*", campoCognome);

        aggiungiCampo(pnl, "Ragione Sociale", campoRagioneSociale);
        aggiungiCampo(pnl, "Partita IVA", campoPartitaIVA);

        aggiungiCampo(pnl, "Codice Fiscale", campoCodiceFiscale);
        aggiungiCampo(pnl, "Email*", campoEmail);

        aggiungiCampo(pnl, "Telefono*", campoTelefono);
        aggiungiCampo(pnl, "Indirizzo", campoIndirizzo);

        aggiungiCampo(pnl, "Città", campoCitta);
        aggiungiCampo(pnl, "CAP", campoCap);

        aggiungiCampo(pnl, "Provincia", campoProvincia);
        aggiungiCampo(pnl, "Paese", campoPaese);

        aggiungiCampo(pnl, "Note", scrollNote);
        aggiungiCampo(pnl, "Data Creazione", campoDataCreazione);

        // =========================
        // DATA AUTOMATICA (NUOVO)
        // =========================
        if (idClienteInModifica == -1) {
            campoDataCreazione.setText(DateUtils.oggiUi());
        }

        // =========================
        // BOTTONE CONFERMA
        // =========================
        JButton btnConferma = new JButton("Conferma");
        btnConferma.addActionListener(e -> confermaCliente());

        pannelloCliente.add(pnl, BorderLayout.CENTER);
        pannelloCliente.add(btnConferma, BorderLayout.SOUTH);

        pannelloCliente.pack();
        pannelloCliente.setLocationRelativeTo(null);
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
    // APERTURA DIALOG
    // ============================================================

    private void apriPannelloNuovoCliente() {
        idClienteInModifica = -1;
        creaDialogCliente();
        pulisciCampiCliente();
        campoDataCreazione.setText(DateUtils.oggiUi());
        pannelloCliente.setTitle("Nuovo Cliente");
        pannelloCliente.setVisible(true);
    }

    private void apriPannelloModificaCliente(int id) {
        creaDialogCliente();
        caricaDatiClienteNelPannello(id);
        pannelloCliente.setTitle("Modifica Cliente");
        pannelloCliente.setVisible(true);
    }

    // ============================================================
    // CRUD CLIENTE
    // ============================================================

    private void confermaCliente() {

        StringBuilder errori = new StringBuilder();

        // =========================
        // NOME (obbligatorio)
        // =========================
        if (campoObbligatorioVuoto(campoNome)) {
            errori.append("- Il campo Nome è obbligatorio\n");
            evidenziaCampo(campoNome, true);
        } else {
            evidenziaCampo(campoNome, false);
        }

        // =========================
        // COGNOME (obbligatorio)
        // =========================
        if (campoObbligatorioVuoto(campoCognome)) {
            errori.append("- Il campo Cognome è obbligatorio\n");
            evidenziaCampo(campoCognome, true);
        } else {
            evidenziaCampo(campoCognome, false);
        }

        // =========================
        // EMAIL (obbligatoria + formato)
        // =========================
        if (campoObbligatorioVuoto(campoEmail)) {
            errori.append("- Il campo Email è obbligatorio\n");
            evidenziaCampo(campoEmail, true);
        } else if (!emailValida(campoEmail.getText())) {
            errori.append("- Il formato dell'Email non è valido\n");
            evidenziaCampo(campoEmail, true);
        } else {
            evidenziaCampo(campoEmail, false);
        }

        // =========================
        // TELEFONO (obbligatorio + formato)
        // =========================
        if (campoObbligatorioVuoto(campoTelefono)) {
            errori.append("- Il campo Telefono è obbligatorio\n");
            evidenziaCampo(campoTelefono, true);
        } else if (!telefonoValido(campoTelefono.getText())) {
            errori.append("- Il formato del Telefono non è valido\n");
            evidenziaCampo(campoTelefono, true);
        } else {
            evidenziaCampo(campoTelefono, false);
        }

        // =========================
        // SE CI SONO ERRORI → UN SOLO POPUP
        // =========================
        if (errori.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    errori.toString(),
                    "Dati cliente non validi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // =========================
        // SALVATAGGIO
        // =========================
        if (idClienteInModifica == -1) {
            inserisciCliente();
        } else {
            modificaCliente(idClienteInModifica);
        }

        pannelloCliente.dispose();
        aggiornaVistaCorrente();
    }


    private void inserisciCliente() {

        Connection conn = interfaccia.getDbManager().getConnessione();
        try {

            PreparedStatement ps = conn.prepareStatement(Query.aggiungiCliente());

            ps.setString(1, campoNome.getText());
            ps.setString(2, campoCognome.getText());
            ps.setString(3, campoRagioneSociale.getText());
            ps.setString(4, campoPartitaIVA.getText());
            ps.setString(5, campoCodiceFiscale.getText());
            ps.setString(6, campoEmail.getText());
            ps.setString(7, campoTelefono.getText());
            ps.setString(8, campoIndirizzo.getText());
            ps.setString(9, campoCitta.getText());
            ps.setString(10, campoCap.getText());
            ps.setString(11, campoProvincia.getText());
            ps.setString(12, campoPaese.getText());
            ps.setString(13, campoNote.getText());
            ps.setString(14, DateUtils.uiToDb(campoDataCreazione.getText()));
            ps.setInt(15, 1); // attivo

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente inserito!");
            aggiornaVistaCorrente();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante l'inserimento del cliente.\n\n" + e.getMessage(),
                "Inserimento cliente",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    private void modificaCliente(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();
        try {

            PreparedStatement ps = conn.prepareStatement(Query.modificaCliente());

            ps.setString(1, campoNome.getText());
            ps.setString(2, campoCognome.getText());
            ps.setString(3, campoRagioneSociale.getText());
            ps.setString(4, campoPartitaIVA.getText());
            ps.setString(5, campoCodiceFiscale.getText());
            ps.setString(6, campoEmail.getText());
            ps.setString(7, campoTelefono.getText());
            ps.setString(8, campoIndirizzo.getText());
            ps.setString(9, campoCitta.getText());
            ps.setString(10, campoCap.getText());
            ps.setString(11, campoProvincia.getText());
            ps.setString(12, campoPaese.getText());
            ps.setString(13, campoNote.getText());
            int attivoCorrente = 1;
            try (PreparedStatement psAtt = conn.prepareStatement("SELECT attivo FROM cliente WHERE id = ?")) {
                psAtt.setInt(1, id);
                try (ResultSet rsAtt = psAtt.executeQuery()) {
                    if (rsAtt.next()) {
                        attivoCorrente = rsAtt.getInt("attivo");
                    }
                }
            }
            ps.setInt(14, attivoCorrente);
            ps.setInt(15, id);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente modificato!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante la modifica del cliente.\n\n" + e.getMessage(),
                "Modifica cliente",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    

    // ============================================================
    // ARCHIVIA / RECUPERA
    // ============================================================

    private void archiviaCliente(int id) {
        Connection conn = interfaccia.getDbManager().getConnessione();
        try (PreparedStatement ps = conn.prepareStatement(Query.archiviaCliente())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente archiviato!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante l'archiviazione del cliente.\n\n" + e.getMessage(),
                    "Archivio clienti",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void recuperaCliente(int id) {
        Connection conn = interfaccia.getDbManager().getConnessione();
        try (PreparedStatement ps = conn.prepareStatement(Query.recuperaCliente())) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente recuperato!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Errore durante il recupero del cliente.\n\n" + e.getMessage(),
                    "Archivio clienti",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }


    

 // ============================================================
 //  / ARCHIVIA / RECUPERA – SELEZIONE
 // ============================================================

 
 
 private void archiviaClienteSelezionato() {
     int riga = tabClienti.getSelectedRow();
     if (riga == -1) {
         JOptionPane.showMessageDialog(this, "Seleziona un cliente.");
         return;
     }

     int id = (int) tabClienti.getValueAt(riga, 0);

     if (JOptionPane.showConfirmDialog(this,
             "Archiviare questo cliente?",
             "Conferma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
         archiviaCliente(id);
         aggiornaVistaCorrente();
     }
 }

 private void recuperaClienteSelezionato() {
     int riga = tabClienti.getSelectedRow();
     if (riga == -1) {
         JOptionPane.showMessageDialog(this, "Seleziona un cliente dall'archivio.");
         return;
     }

     int id = (int) tabClienti.getValueAt(riga, 0);

     if (JOptionPane.showConfirmDialog(this,
             "Recuperare questo cliente?",
             "Conferma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
         recuperaCliente(id);
         aggiornaVistaCorrente();
     }
 }

    
    // ============================================================
    // CARICAMENTO TABELLA
    // ============================================================

 private void caricaDatiAttiviInTabella() {
	    modelloTab.setRowCount(0);

	    Connection conn = interfaccia.getDbManager().getConnessione();
	    try (PreparedStatement ps = conn.prepareStatement(Query.getClientiAttiviCompleti());
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            modelloTab.addRow(new Object[]{
	                    rs.getInt("id"),
	                    rs.getString("nome"),
	                    rs.getString("cognome"),
	                    rs.getString("ragione_sociale"),
	                    rs.getString("email"),
	                    rs.getString("telefono")
	            });
	        }

	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Errore durante il caricamento dei clienti attivi.\n\n" + e.getMessage(),
	                "Caricamento clienti",
	                JOptionPane.ERROR_MESSAGE
	        );
	        e.printStackTrace();
	    }
	}

	private void caricaDatiArchivioInTabella() {
	    modelloTab.setRowCount(0);

	    Connection conn = interfaccia.getDbManager().getConnessione();
	    try (PreparedStatement ps = conn.prepareStatement(Query.getClientiArchiviatiCompleti());
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            modelloTab.addRow(new Object[]{
	                    rs.getInt("id"),
	                    rs.getString("nome"),
	                    rs.getString("cognome"),
	                    rs.getString("ragione_sociale"),
	                    rs.getString("email"),
	                    rs.getString("telefono")
	            });
	        }

	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Errore durante il caricamento dei clienti archiviati.\n\n" + e.getMessage(),
	                "Caricamento clienti",
	                JOptionPane.ERROR_MESSAGE
	        );
	        e.printStackTrace();
	    }
	}


    

    private void aggiornaVistaCorrente() {
        if (archivioVisibile) caricaDatiArchivioInTabella();
        else caricaDatiAttiviInTabella();
    }

    // ============================================================
    // ARCHIVIO
    // ============================================================

    private void toggleArchivio() {

        archivioVisibile = !archivioVisibile;

        if (archivioVisibile) {
            lblArchivio.setVisible(true);
            btnRecupera.setVisible(true);
            btnArchivia.setVisible(false);
            btnMostraArchivio.setText("Mostra Attivi");
            caricaDatiArchivioInTabella();
        } else {
            lblArchivio.setVisible(false);
            btnRecupera.setVisible(false);
            btnArchivia.setVisible(true);
            btnMostraArchivio.setText("Mostra Archivio");
            caricaDatiAttiviInTabella();
        }
    }

    // ============================================================
    // INFO CLIENTE
    // ============================================================

    private void mostraInfoCliente(int idCliente) {

        Connection conn = interfaccia.getDbManager().getConnessione();
        try {

            PreparedStatement ps = conn.prepareStatement(Query.getClienteById());
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String info =
                        "ID: " + rs.getInt("id") + "\n" +
                        "Nome: " + rs.getString("nome") + "\n" +
                        "Cognome: " + rs.getString("cognome") + "\n" +
                        "Ragione Sociale: " + rs.getString("ragione_sociale") + "\n" +
                        "Email: " + rs.getString("email") + "\n" +
                        "Telefono: " + rs.getString("telefono") + "\n" +
                        "Indirizzo: " + rs.getString("indirizzo") + "\n" +
                        "Città: " + rs.getString("citta") + "\n" +
                        "CAP: " + rs.getString("cap") + "\n" +
                        "Provincia: " + rs.getString("provincia") + "\n" +
                        "Paese: " + rs.getString("paese") + "\n" +
                        "Note: " + rs.getString("note") + "\n" +
                        "Data creazione: " + DateUtils.dbToUi(rs.getString("data_creazione")) + "\n" +
                        "Attivo: " + (rs.getInt("attivo") == 1 ? "Sì" : "No");

                JOptionPane.showMessageDialog(this, info,
                        "Dettagli Cliente", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento delle informazioni del cliente.\n\n" + e.getMessage(),
                "Dettagli cliente",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    private void mostraInfoClienteSelezionato() {
        int riga = tabClienti.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un cliente.");
            return;
        }
        mostraInfoCliente((int) tabClienti.getValueAt(riga, 0));
    }

    // ============================================================
    // SUPPORTO
    // ============================================================

    private void pulisciCampiCliente() {
        campoNome.setText("");
        campoCognome.setText("");
        campoRagioneSociale.setText("");
        campoPartitaIVA.setText("");
        campoCodiceFiscale.setText("");
        campoEmail.setText("");
        campoTelefono.setText("");
        campoIndirizzo.setText("");
        campoCitta.setText("");
        campoCap.setText("");
        campoProvincia.setText("");
        campoPaese.setText("");
        campoNote.setText("");
       
    }

    private void caricaDatiClienteNelPannello(int id) {

        Connection conn = interfaccia.getDbManager().getConnessione();
        try {

            PreparedStatement ps = conn.prepareStatement(Query.getClienteById());
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                campoNome.setText(rs.getString("nome"));
                campoCognome.setText(rs.getString("cognome"));
                campoRagioneSociale.setText(rs.getString("ragione_sociale"));
                campoPartitaIVA.setText(rs.getString("partita_iva"));
                campoCodiceFiscale.setText(rs.getString("codice_fiscale"));
                campoEmail.setText(rs.getString("email"));
                campoTelefono.setText(rs.getString("telefono"));
                campoIndirizzo.setText(rs.getString("indirizzo"));
                campoCitta.setText(rs.getString("citta"));
                campoCap.setText(rs.getString("cap"));
                campoProvincia.setText(rs.getString("provincia"));
                campoPaese.setText(rs.getString("paese"));
                campoNote.setText(rs.getString("note"));
                campoDataCreazione.setText(DateUtils.dbToUi(rs.getString("data_creazione")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante il caricamento dei dati del cliente.\n\n" + e.getMessage(),
                "Modifica cliente",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }

    // ============================================================
    // CERCA CLIENTI
    // ============================================================

    private void cercaClienti() {

        String valore = campoCerca.getText().trim();
        String filtro = comboCerca.getSelectedItem().toString();

        if (valore.isEmpty() && !filtro.equals("Tutti")) {
            JOptionPane.showMessageDialog(this, "Inserisci un valore da cercare.");
            return;
        }

        modelloTab.setRowCount(0);

        Connection conn = interfaccia.getDbManager().getConnessione();
        try {

            PreparedStatement ps;

            switch (filtro) {
                case "Nome" -> {
                    ps = conn.prepareStatement(Query.getClientiByNome());
                    ps.setString(1, valore);
                }
                case "Cognome" -> {
                    ps = conn.prepareStatement(Query.getClientiByCognome());
                    ps.setString(1, valore);
                }
                case "Ragione Sociale" -> {
                    ps = conn.prepareStatement(Query.getClientiByRagioneSociale());
                    ps.setString(1, valore);
                }
                case "Email" -> {
                    ps = conn.prepareStatement(Query.getClientiByEmail());
                    ps.setString(1, valore);
                }
                case "Telefono" -> {
                    ps = conn.prepareStatement(Query.getClientiByTelefono());
                    ps.setString(1, valore);
                }
                default ->  { if (archivioVisibile) {
                    ps = conn.prepareStatement(Query.getClientiArchiviatiCompleti());
                } else {
                    ps = conn.prepareStatement(Query.getClientiAttiviCompleti());
                }

            }
         }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modelloTab.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("ragione_sociale"),
                        rs.getString("email"),
                        rs.getString("telefono")
                });
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Errore durante la ricerca dei clienti.\n\n" + e.getMessage(),
                "Ricerca clienti",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }

    }
    
	 // ============================================================
	 // VALIDAZIONE CAMPI
	 // ============================================================
	
	 /**
	  * Controlla se un campo di testo obbligatorio è vuoto.
	  */
	 private boolean campoObbligatorioVuoto(JTextField campo) {
	     String testo = campo.getText();
	     if (testo == null) {
	         return true;
	     }
	     return testo.trim().isEmpty();
	 }
	
	 /**
	  * Controlla in modo semplice la validità dell'email.
	  * Per il progetto è sufficiente verificare la presenza della @.
	  */
	 private boolean emailValida(String email) {
	     if (email == null) {
	         return false;
	     }
	     return email.contains("@");
	 }
	
	 /**
	  * Controlla che il telefono contenga solo numeri
	  * (eventualmente con prefisso +).
	  */
	 private boolean telefonoValido(String telefono) {
	     if (telefono == null) {
	         return false;
	     }
	     return telefono.matches("\\+?[0-9]+");
	 }
	
	 /**
	  * Evidenzia un campo come errato o valido.
	  */
	 private void evidenziaCampo(JComponent campo, boolean errore) {
	     if (errore) {
	         campo.setBorder(BorderFactory.createLineBorder(Color.RED));
	     } else {
	         campo.setBorder(UIManager.getBorder("TextField.border"));
	     }
	 }

}
