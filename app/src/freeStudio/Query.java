package freeStudio;

/**
 * Classe Query
 *
 * Contiene tutte le query SQL utilizzate dall’applicazione FreeStudio.
 * È una classe di utilità: non deve essere istanziata.
 *
 * Le query sono organizzate per dominio funzionale:
 * - Clienti
 * - Progetti
 * - Fatture
 * - Pagamenti
 * - Attività
 * - Dashboard 
 * - Grafici
 */
public class Query {

    private Query() {}

    // =====================================================================
    // ============================== CLIENTE ===============================
    // =====================================================================

    // ---------- INSERT / UPDATE / DELETE ----------

    public static String aggiungiCliente() {
        return """
            INSERT INTO cliente (
                nome, cognome, ragione_sociale, partita_iva, codice_fiscale,
                email, telefono, indirizzo, citta, cap, provincia, paese,
                note, data_creazione, attivo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }

    public static String modificaCliente() {
        return """
            UPDATE cliente SET
                nome = ?, cognome = ?, ragione_sociale = ?, partita_iva = ?,
                codice_fiscale = ?, email = ?, telefono = ?, indirizzo = ?,
                citta = ?, cap = ?, provincia = ?, paese = ?, note = ?,
                attivo = ?
            WHERE id = ?
        """;
    }

    

    // ---------- SELECT CLIENTI ----------

    public static String getClienteById() {
        return "SELECT * FROM cliente WHERE id = ?";
    }

    public static String getTuttiClienti() {
        return "SELECT * FROM cliente ORDER BY cognome, nome";
    }



    public static String getClientiByNome() {
        return "SELECT * FROM cliente WHERE nome = ? ORDER BY cognome, nome";
    }

    public static String getClientiByCognome() {
        return "SELECT * FROM cliente WHERE cognome = ? ORDER BY cognome, nome";
    }

    public static String getClientiByRagioneSociale() {
        return "SELECT * FROM cliente WHERE ragione_sociale = ? ORDER BY ragione_sociale";
    }

    public static String getClientiByPartitaIVA() {
        return "SELECT * FROM cliente WHERE partita_iva = ?";
    }

    public static String getClientiByCodiceFiscale() {
        return "SELECT * FROM cliente WHERE codice_fiscale = ?";
    }

    public static String getClientiByEmail() {
        return "SELECT * FROM cliente WHERE email = ?";
    }

    public static String getClientiByTelefono() {
        return "SELECT * FROM cliente WHERE telefono = ?";
    }

    public static String getClientiByIndirizzo() {
        return "SELECT * FROM cliente WHERE indirizzo = ? ORDER BY cognome, nome";
    }

    public static String getClientiByCitta() {
        return "SELECT * FROM cliente WHERE citta = ? ORDER BY cognome, nome";
    }

    public static String getClientiByCap() {
        return "SELECT * FROM cliente WHERE cap = ? ORDER BY cognome, nome";
    }

    public static String getClientiByProvincia() {
        return "SELECT * FROM cliente WHERE provincia = ? ORDER BY cognome, nome";
    }

    public static String getClientiByPaese() {
        return "SELECT * FROM cliente WHERE paese = ? ORDER BY cognome, nome";
    }

    public static String getClientiByAttivo() {
        return "SELECT * FROM cliente WHERE attivo = ? ORDER BY cognome, nome";
    }

    public static String getClientiByDataCreazione() {
        return "SELECT * FROM cliente WHERE data_creazione = ? ORDER BY cognome, nome";
    }

    public static String getClienteByDataCreazioneASC() {
        return "SELECT * FROM cliente ORDER BY data_creazione ASC";
    }

    public static String getClienteByDataCreazioneDESC() {
        return "SELECT * FROM cliente ORDER BY data_creazione DESC";
    }

    public static String getClientiAttivi() {
        return "SELECT id, nome, cognome FROM cliente WHERE attivo = 1";
    }

    public static String countClientiAttivi() {
        return "SELECT COUNT(*) FROM cliente WHERE attivo = 1";
    }
    
    public static String getClientiAttiviCompleti() {
        return "SELECT * FROM cliente WHERE attivo = 1 ORDER BY cognome, nome";
    }

    public static String getClientiArchiviatiCompleti() {
        return "SELECT * FROM cliente WHERE attivo = 0 ORDER BY cognome, nome";
    }

    public static String archiviaCliente() {
        return "UPDATE cliente SET attivo = 0 WHERE id = ?";
    }

    public static String recuperaCliente() {
        return "UPDATE cliente SET attivo = 1 WHERE id = ?";
    }


    // =====================================================================
    // ============================== PROGETTO ==============================
    // =====================================================================

    // ---------- INSERT / UPDATE / DELETE ----------

    public static String aggiungiProgetto() {
        return """
            INSERT INTO progetto (
                id_cliente, titolo, descrizione, stato, data_inizio,
                data_fine, preventivo, costo_effettivo, fatturabile,
                priorita, note, attivo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
        """;
    }


    public static String modificaProgetto() {
        return """
            UPDATE progetto SET
                id_cliente = ?, titolo = ?, descrizione = ?, stato = ?,
                data_inizio = ?, data_fine = ?, preventivo = ?,
                costo_effettivo = ?, fatturabile = ?, priorita = ?, note = ?
            WHERE id = ?
        """;
    }

    public static String modificaStatoProgetto() {
        return "UPDATE progetto SET stato = ? WHERE id = ?";
    }

    public static String modificaPrioritaProgetto() {
        return "UPDATE progetto SET priorita = ? WHERE id = ?";
    }

    public static String getProgettiAttivi() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 1
            ORDER BY p.data_inizio DESC
        """;
    }

    public static String getProgettiArchiviati() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 0
            ORDER BY p.data_inizio DESC
        """;
    }

    public static String archiviaProgetto() {
        return "UPDATE progetto SET attivo = 0 WHERE id = ?";
    }

    public static String recuperaProgetto() {
        return "UPDATE progetto SET attivo = 1 WHERE id = ?";
    }


    public static String getProgettiByTitolo() {
        return "SELECT * FROM progetto WHERE titolo = ?";
    }

    public static String getProgettiByCliente() {
        return "SELECT * FROM progetto WHERE id_cliente = ?";
    }

    public static String getProgettiByStato() {
        return "SELECT * FROM progetto WHERE stato = ?";
    }

    public static String getProgettiByDataASC() {
        return "SELECT * FROM progetto ORDER BY data_inizio ASC";
    }

    public static String getProgettiByDataDESC() {
        return "SELECT * FROM progetto ORDER BY data_inizio DESC";
    }

    public static String getProgettiByPrioritaASC() {
        return "SELECT * FROM progetto WHERE priorita = ? ORDER BY priorita, data_inizio ASC";
    }

    public static String getProgettiByPrioritaDESC() {
        return "SELECT * FROM progetto WHERE priorita = ? ORDER BY priorita, data_inizio DESC";
    }

    // ---------- SELECT CON JOIN CLIENTE ----------

    public static String getTuttiProgettiConCliente() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            ORDER BY p.data_inizio DESC
        """;
    }

    public static String getProgettiConClienteByStato() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.stato = ?
            ORDER BY p.data_inizio ASC
        """;
    }

    public static String getProgettoById() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.id = ?
        """;
    }

    // ---------- COUNT PROGETTI (HOME / DASHBOARD) ----------

    public static String countProgettiInCorso() {
        return "SELECT COUNT(*) FROM progetto WHERE stato = 'In corso'";
    }

    public static String countProgettiCompletati() {
        return "SELECT COUNT(*) FROM progetto WHERE stato = 'Completato'";
    }

    public static String countProgettiSospesi() {
        return "SELECT COUNT(*) FROM progetto WHERE stato = 'Sospeso'";
    }

    public static String countProgettiAnnullati() {
        return "SELECT COUNT(*) FROM progetto WHERE stato = 'Annullato'";
    }

    /**
     * Progetti attivi filtrati per titolo
     */
    public static String getProgettiAttiviByTitolo() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 1 AND p.titolo LIKE ?
            ORDER BY p.data_inizio DESC
        """;
    }

    /**
     * Progetti attivi filtrati per stato
     */
    public static String getProgettiAttiviByStato() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 1 AND p.stato = ?
            ORDER BY p.data_inizio DESC
        """;
    }

    /**
     * Progetti archiviati filtrati per titolo
     */
    public static String getProgettiArchiviatiByTitolo() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 0 AND p.titolo LIKE ?
            ORDER BY p.data_inizio DESC
        """;
    }

    /**
     * Progetti archiviati filtrati per stato
     */
    public static String getProgettiArchiviatiByStato() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 0 AND p.stato = ?
            ORDER BY p.data_inizio DESC
        """;
    }

    public static String getProgettiAttiviSenzaFiltro() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 1
            ORDER BY p.data_inizio DESC
        """;
    }

    public static String getProgettiArchiviatiSenzaFiltro() {
        return """
            SELECT p.*, c.nome, c.cognome
            FROM progetto p
            JOIN cliente c ON p.id_cliente = c.id
            WHERE p.attivo = 0
            ORDER BY p.data_inizio DESC
        """;
    }

    
 // =====================================================================
 // ============================== FATTURE ===============================
 // =====================================================================

 /**
  * Inserisce una nuova fattura (attiva di default).
  */
 public static String aggiungiFattura() {
     return """
         INSERT INTO fattura (
             id_cliente,
             numero_fattura,
             data_fattura,
             data_scadenza,
             importo,
             stato_pagamento,
             note,
             attivo
         )
         VALUES (?, ?, ?, ?, ?, ?, ?, 1)
     """;
 }

 /**
  * Modifica una fattura esistente.
  */
 public static String modificaFattura() {
     return """
         UPDATE fattura SET
             id_cliente = ?,
             numero_fattura = ?,
             data_fattura = ?,
             data_scadenza = ?,
             importo = ?,
             stato_pagamento = ?,
             note = ?
         WHERE id = ?
     """;
 }

 //
 // ========================== CARICAMENTO ===============================
 //

 /**
  * Tutte le fatture ATTIVE (vista principale).
  */
 public static String getFattureAttive() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 1
         ORDER BY f.data_fattura DESC
     """;
 }

 /**
  * Tutte le fatture ARCHIVIATE.
  */
 public static String getFattureArchiviate() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 0
         ORDER BY f.data_fattura DESC
     """;
 }

 //
 // ========================== ARCHIVIAZIONE ==============================
 //

 public static String archiviaFattura() {
     return "UPDATE fattura SET attivo = 0 WHERE id = ?";
 }

 public static String recuperaFattura() {
     return "UPDATE fattura SET attivo = 1 WHERE id = ?";
 }

 //
 // ========================== RICERCA (ATTIVE) ===========================
 //

 public static String getTutteFatture() {
	    return "SELECT * FROM fattura ORDER BY data_fattura DESC";
	}

 
 public static String getFattureAttiveByNumero() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 1
           AND f.numero_fattura LIKE ?
         ORDER BY f.data_fattura DESC
     """;
 }

 public static String getFattureAttiveByData() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 1
           AND f.data_fattura = ?
         ORDER BY f.data_fattura DESC
     """;
 }

 public static String getFattureAttiveByStato() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 1
           AND f.stato_pagamento = ?
         ORDER BY f.data_fattura DESC
     """;
 }

 //
 // ======================== RICERCA (ARCHIVIATE) =========================
 //

 public static String getFattureArchiviateByNumero() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 0
           AND f.numero_fattura LIKE ?
         ORDER BY f.data_fattura DESC
     """;
 }

 public static String getFattureArchiviateByData() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 0
           AND f.data_fattura = ?
         ORDER BY f.data_fattura DESC
     """;
 }

 public static String getFattureArchiviateByStato() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.attivo = 0
           AND f.stato_pagamento = ?
         ORDER BY f.data_fattura DESC
     """;
 }

 //
 // ========================== DETTAGLIO ===============================
 //

 public static String getFatturaById() {
     return """
         SELECT f.*, c.nome, c.cognome
         FROM fattura f
         JOIN cliente c ON f.id_cliente = c.id
         WHERE f.id = ?
     """;
 }

 //
 // ========================== TOTALI ===============================
 //

 /**
  * Totale importi fatture ATTIVE.
  */
 public static String totaleEmesso() {
     return """
         SELECT COALESCE(SUM(importo), 0)
         FROM fattura
         WHERE attivo = 1
     """;
 }

 /**
  * Numero fatture attive.
  */
 public static String countFattureAttive() {
     return "SELECT COUNT(*) FROM fattura WHERE attivo = 1";
 }

 /**
  * Totale pagato per una fattura
  */
 public static String totalePagatoFattura() {
     return """
         SELECT COALESCE(SUM(importo_pagato), 0)
         FROM pagamento
         WHERE id_fattura = ? AND attivo = 1
     """;
 }

 /**
  * Importo totale della fattura
  */
 public static String importoFattura() {
     return """
         SELECT importo
         FROM fattura
         WHERE id = ?
     """;
 }

 /**
  * Aggiorna stato fattura
  */
 public static String aggiornaStatoFattura() {
     return """
         UPDATE fattura
         SET stato_pagamento = ?
         WHERE id = ?
     """;
 }

 public static String countFattureScadute() {
	    return """
	        SELECT COUNT(*)
	        FROM fattura
	        WHERE attivo = 1
	          AND stato_pagamento <> 'Pagata'
	          AND data_scadenza < CURDATE()
	    """;
	}

 

//=====================================================================
//============================== PAGAMENTI =============================
//=====================================================================

//---------- INSERT / UPDATE ----------

public static String aggiungiPagamento() {
  return """
      INSERT INTO pagamento (
          id_fattura,
          data_pagamento,
          importo_pagato,
          metodo,
          note,
          attivo
      )
      VALUES (?, ?, ?, ?, ?, 1)
  """;
}

public static String modificaPagamento() {
  return """
      UPDATE pagamento SET
          id_fattura = ?,
          data_pagamento = ?,
          importo_pagato = ?,
          metodo = ?,
          note = ?
      WHERE id = ?
  """;
}

//---------- ARCHIVIA / RECUPERA ----------

public static String archiviaPagamento() {
  return "UPDATE pagamento SET attivo = 0 WHERE id = ?";
}

public static String recuperaPagamento() {
  return "UPDATE pagamento SET attivo = 1 WHERE id = ?";
}

//---------- SELECT BASE ----------

public static String getPagamentiAttivi() {
  return """
      SELECT p.id,
             f.numero_fattura,
             p.data_pagamento,
             p.importo_pagato,
             p.metodo
      FROM pagamento p
      JOIN fattura f ON p.id_fattura = f.id
      WHERE p.attivo = 1
      ORDER BY p.data_pagamento DESC
  """;
}

public static String getPagamentiArchiviati() {
  return """
      SELECT p.id,
             f.numero_fattura,
             p.data_pagamento,
             p.importo_pagato,
             p.metodo
      FROM pagamento p
      JOIN fattura f ON p.id_fattura = f.id
      WHERE p.attivo = 0
      ORDER BY p.data_pagamento DESC
  """;
}

//---------- RICERCHE ----------

public static String getPagamentiByMetodo() {
  return """
      SELECT p.id,
             f.numero_fattura,
             p.data_pagamento,
             p.importo_pagato,
             p.metodo
      FROM pagamento p
      JOIN fattura f ON p.id_fattura = f.id
      WHERE p.attivo = 1 AND p.metodo = ?
      ORDER BY p.data_pagamento DESC
  """;
}



//---------- DETTAGLIO PAGAMENTO ----------

public static String getPagamentoById() {
  return """
      SELECT p.*,
             f.numero_fattura,
             f.importo
      FROM pagamento p
      JOIN fattura f ON p.id_fattura = f.id
      WHERE p.id = ?
  """;
}

public static String getFatturePagabili() {
    return """
        SELECT id, numero_fattura, importo
        FROM fattura
        WHERE attivo = 1
          AND stato_pagamento IN ('Non pagata', 'Parziale')
        ORDER BY data_fattura DESC
    """;
}






    // =====================================================================
    // ============================== ATTIVITÀ ==============================
    // =====================================================================

    public static String aggiungiAttivita() {
        return """
            INSERT INTO attivita (
                id_progetto, descrizione, data_attivita,
                ore_lavorate, costo_orario, note
            )
            VALUES (?, ?, ?, ?, ?, ?)
        """;
    }

    public static String getAttivitaByProgetto() {
        return "SELECT * FROM attivita WHERE id_progetto = ? ORDER BY data_attivita DESC";
    }

    public static String eliminaAttivita() {
        return "DELETE FROM attivita WHERE id = ?";
    }

    // =====================================================================
    // =========================== DASHBOARD / KPI ==========================
    // =====================================================================

    // Somma di tutti i pagamenti effettuati
    public static String sumTotaleIncassato() {
        return """
            SELECT COALESCE(SUM(importo_pagato), 0)
            FROM pagamento
        """;
    }

    // Somma di ciò che resta da incassare (fatture non pagate o parziali)
    public static String sumTotaleNonPagato() {
        return """
            SELECT COALESCE(SUM(f.importo - IFNULL(p.totale_pagato,0)), 0)
            FROM fattura f
            LEFT JOIN (
                SELECT id_fattura, SUM(importo_pagato) AS totale_pagato
                FROM pagamento
                GROUP BY id_fattura
            ) p ON f.id = p.id_fattura
            WHERE f.stato_pagamento != 'Pagata'
        """;
    }

    // =====================================================================
    // ============================= GRAFICI ================================
    // =====================================================================

    // ---------- CLIENTI ----------

    public static String clientiTotaliPerMese() {
        return """
            SELECT SUBSTRING(data_creazione, 1, 7) AS periodo, COUNT(*) AS totale
            FROM cliente
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String clientiAttiviPerMese() {
        return """
            SELECT SUBSTRING(data_creazione, 1, 7) AS periodo, COUNT(*) AS totale
            FROM cliente
            WHERE attivo = 1
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String clientiArchiviatiPerMese() {
        return """
            SELECT SUBSTRING(data_creazione, 1, 7) AS periodo, COUNT(*) AS totale
            FROM cliente
            WHERE attivo = 0
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String clientiTotaliPerAnno() {
        return """
            SELECT SUBSTRING(data_creazione, 1, 4) AS periodo, COUNT(*) AS totale
            FROM cliente
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String clientiAttiviPerAnno() {
        return """
            SELECT SUBSTRING(data_creazione, 1, 4) AS periodo, COUNT(*) AS totale
            FROM cliente
            WHERE attivo = 1
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String clientiArchiviatiPerAnno() {
        return """
            SELECT SUBSTRING(data_creazione, 1, 4) AS periodo, COUNT(*) AS totale
            FROM cliente
            WHERE attivo = 0
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    // ---------- PROGETTI ----------

    public static String progettiPerMese() {
        return """
            SELECT SUBSTRING(data_inizio, 1, 7) AS periodo, COUNT(*) AS totale
            FROM progetto
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String progettiPerAnno() {
        return """
            SELECT SUBSTRING(data_inizio, 1, 4) AS periodo, COUNT(*) AS totale
            FROM progetto
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    // ---------- FATTURE ----------

    public static String fatturePerMese() {
        return """
            SELECT SUBSTRING(data_fattura, 1, 7) AS periodo, COUNT(*) AS totale
            FROM fattura
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    public static String fatturePerAnno() {
        return """
            SELECT SUBSTRING(data_fattura, 1, 4) AS periodo, COUNT(*) AS totale
            FROM fattura
            GROUP BY periodo
            ORDER BY periodo
        """;
    }

    // ---------- PAGAMENTI ----------

    public static String conteggioPagamentiPerMetodo() {
        return """
            SELECT metodo, COUNT(*) AS totale
            FROM pagamento
            GROUP BY metodo
            ORDER BY totale DESC
        """;
    }

    // ---------- RICERCHE ----------

    public static String getFattureByNumero() {
        return "SELECT * FROM fattura WHERE numero_fattura LIKE ?";
    }

    public static String getFattureByData() {
        return "SELECT * FROM fattura WHERE data_fattura = ?";
    }

    public static String getPagamentiByData() {
        return """
            SELECT pagamento.id, fattura.numero_fattura, pagamento.data_pagamento,
                   pagamento.importo_pagato, pagamento.metodo
            FROM pagamento
            JOIN fattura ON pagamento.id_fattura = fattura.id
            WHERE pagamento.data_pagamento = ?
            ORDER BY pagamento.data_pagamento DESC
        """;
    }

    public static String getPagamentiByImporto() {
        return """
            SELECT pagamento.id, fattura.numero_fattura, pagamento.data_pagamento,
                   pagamento.importo_pagato, pagamento.metodo
            FROM pagamento
            JOIN fattura ON pagamento.id_fattura = fattura.id
            WHERE pagamento.importo_pagato = ?
            ORDER BY pagamento.data_pagamento DESC
        """;
    }
}
