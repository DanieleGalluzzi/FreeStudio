package freeStudio;

import java.sql.*;
import javax.swing.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/?serverTimezone=Europe/Rome";

    private String USER;
    private String PASSWORD;

    private Connection conn;

    // ========================================================
    // COSTRUTTORE — NON CREA SUBITO LA CONNESSIONE
    // ========================================================
    public DatabaseManager(String user, String password) {
        this.USER = user;
        this.PASSWORD = password;
    }


    // ========================================================
    // METODO LOGIN → CREA LA CONNESSIONE SOLO SE LE CREDENZIALI SONO CORRETTE
    // ========================================================
    public boolean connetti(String user, String password) {
        this.USER = user;
        this.PASSWORD = password;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connessione temporanea per creare DB
            try (Connection tmp = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = tmp.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS free_studio");
            }

            // Connessione definitiva
            String urlConDb = "jdbc:mysql://localhost:3306/free_studio?serverTimezone=Europe/Rome";
            conn = DriverManager.getConnection(urlConDb, USER, PASSWORD);

            // Creazione tabelle SOLO ORA
            creaTabelle();

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Impossibile connettersi al database.\n" +
                "Verifica che l'utente abbia i permessi su MySQL.\n\n" +
                "Dettagli: " + e.getMessage(),
                "Errore Connessione",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

    }


    // ========================================================
    // CREAZIONE TABELLE
    // ========================================================
    private void creaTabelle() {

        String cliente = """
                CREATE TABLE IF NOT EXISTS cliente (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(50) NOT NULL,
                    cognome VARCHAR(50) NOT NULL,
                    ragione_sociale VARCHAR(100),
                    partita_iva VARCHAR(20),
                    codice_fiscale VARCHAR(20),
                    email VARCHAR(80) NOT NULL,
                    telefono VARCHAR(30) NOT NULL,
                    indirizzo VARCHAR(150),
                    citta VARCHAR(80),
                    cap VARCHAR(10),
                    provincia VARCHAR(20),
                    paese VARCHAR(50),
                    note TEXT,
                    data_creazione VARCHAR(10),
                    attivo TINYINT(1)
                );
                """;

        String progetto = """
                CREATE TABLE IF NOT EXISTS progetto (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_cliente INT,
                    titolo VARCHAR(100) NOT NULL,
                    descrizione TEXT,
                    stato VARCHAR(30),
                    data_inizio VARCHAR(10),
                    data_fine VARCHAR(10),
                    preventivo DOUBLE,
                    costo_effettivo DOUBLE,
                    fatturabile TINYINT(1),
                    priorita INT,
                    note TEXT,
                    attivo TINYINT(1),
                    FOREIGN KEY (id_cliente) REFERENCES cliente(id)
                );
                """;

        String fattura = """
                CREATE TABLE IF NOT EXISTS fattura (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_cliente INT,
                    numero_fattura VARCHAR(30) NOT NULL,
                    data_fattura VARCHAR(10) NOT NULL,
                    data_scadenza VARCHAR(10) NOT NULL,
                    importo DOUBLE NOT NULL CHECK (importo >= 0),
                    stato_pagamento VARCHAR(30),
                    metodo_pagamento VARCHAR(30),
                    note TEXT,
                    attivo TINYINT(1) NOT NULL DEFAULT 1,
                    FOREIGN KEY (id_cliente) REFERENCES cliente(id)
                );
                """;

        String pagamento = """
                CREATE TABLE IF NOT EXISTS pagamento (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_fattura INT,
                    data_pagamento VARCHAR(10),
                    importo_pagato DOUBLE NOT NULL CHECK (importo_pagato >= 0),
                    metodo VARCHAR(30),
                    note TEXT,
                    attivo TINYINT(1) NOT NULL DEFAULT 1,
                    FOREIGN KEY (id_fattura) REFERENCES fattura(id)
                );
                """;

        String attivita = """
                CREATE TABLE IF NOT EXISTS attivita (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_progetto INT,
                    descrizione VARCHAR(200),
                    data_attivita VARCHAR(10),
                    ore_lavorate DOUBLE NOT NULL CHECK (ore_lavorate >= 0),
        			costo_orario DOUBLE NOT NULL CHECK (costo_orario >= 0),
                    note TEXT,
                    attivo TINYINT(1) NOT NULL DEFAULT 1,
                    FOREIGN KEY (id_progetto) REFERENCES progetto(id)
                );
                """;

        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(cliente);
            stmt.executeUpdate(progetto);
            stmt.executeUpdate(fattura);
            stmt.executeUpdate(pagamento);
            stmt.executeUpdate(attivita);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Errore durante la creazione delle tabelle.\nDettagli: " + e.getMessage(),
                    "Errore Tabelle",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================================
    // OTTIENI CONNESSIONE
    // ========================================================
    public Connection getConnessione() {
        return conn;
    }

    // ========================================================
    // CHIUDI CONNESSIONE
    // ========================================================
    public void chiudiConnessione() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Errore durante la chiusura della connessione.\nDettagli: " + e.getMessage(),
                        "Errore DB",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
