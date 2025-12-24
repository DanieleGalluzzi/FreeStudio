package freeStudio;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;



public class Attivita extends JPanel{

	private final Interfaccia interfaccia;
	
	//Dataset e grafico
	
	private DefaultCategoryDataset datasetClienti;
	private JFreeChart chartClienti;
	private DefaultCategoryDataset datasetProgetti;
	private JFreeChart chartProgetti;
	private DefaultCategoryDataset datasetFatture;
	private JFreeChart chartFatture;
	private DefaultPieDataset datasetMetodiPagamento;
	private JFreeChart chartMetodiPagamento;
	
	
	//Controlli
	private JCheckBox chkAttivi;
	private JCheckBox chkArchiviati;
	private JCheckBox chkTotali;
	
	private JRadioButton rbClientiMese;
	private JRadioButton rbClientiAnno;
	private JRadioButton rbProgettiMese;
	private JRadioButton rbProgettiAnno;
	private JRadioButton rbFattureMese;
	private JRadioButton rbFattureAnno;
	
	public Attivita(Interfaccia interfaccia) {
		this.interfaccia = interfaccia;
		
		setLayout(new BorderLayout());
		
		JPanel contenuto = new JPanel();
		contenuto.setLayout(new BoxLayout(contenuto, BoxLayout.Y_AXIS));
		contenuto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		contenuto.add(creaPannelloClienti());
		contenuto.add(Box.createVerticalStrut(30));
		contenuto.add(creaPannelloProgetti());
		contenuto.add(Box.createVerticalStrut(30));
		contenuto.add(creaPannelloFatture());
		contenuto.add(Box.createVerticalStrut(30));
		contenuto.add(creaPannelloMetodiPagamento());

		
		JScrollPane scroll = new JScrollPane(contenuto);
		scroll.setBorder(null);
		
		add(scroll, BorderLayout.CENTER);			
	}
	
	
	//---------------------------------
	// PANNELLO CLIENTI
	//---------------------------------
	
	private JPanel creaPannelloClienti() {
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Andamento Clienti"));
		
		// CONTROLLI
		chkAttivi = new JCheckBox("Attivi", true);
		chkArchiviati = new JCheckBox("Archiviati", true);
		chkTotali = new JCheckBox("Totali");
		
		rbClientiMese = new JRadioButton("Mese", true);
		rbClientiAnno = new JRadioButton("Anno");
		
		ButtonGroup gruppoPeriodo = new ButtonGroup();
		gruppoPeriodo.add(rbClientiMese);
		gruppoPeriodo.add(rbClientiAnno);
		
		JPanel pnlControlli = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlControlli.add(new JLabel("Mostra:"));
		pnlControlli.add(chkAttivi);
		pnlControlli.add(chkArchiviati);
		pnlControlli.add(chkTotali);
		
		pnlControlli.add(Box.createHorizontalStrut(20)); // spazio 20px
		pnlControlli.add(new JLabel("Periodo:"));
		pnlControlli.add(rbClientiMese);
		pnlControlli.add(rbClientiAnno);
		
		pnl.add(pnlControlli, BorderLayout.NORTH);
		
		// GRAFICO
		datasetClienti = new DefaultCategoryDataset();
		chartClienti = ChartFactory.createLineChart(
				"Clienti nel Tempo",
				"Periodo",
				"Numero Clienti",
				datasetClienti
				);
		
		ChartPanel chartPanel = new ChartPanel(chartClienti);
		chartPanel.setPreferredSize(new Dimension(900, 400));
		
		pnl.add(chartPanel, BorderLayout.CENTER);
		
		// LISTENER
		chkAttivi.addActionListener(e -> aggiornaDatasetClienti());
        chkArchiviati.addActionListener(e -> aggiornaDatasetClienti());
        chkTotali.addActionListener(e -> aggiornaDatasetClienti());

        rbClientiMese.addActionListener(e -> aggiornaDatasetClienti());
        rbClientiAnno.addActionListener(e -> aggiornaDatasetClienti());

        

        return pnl;
	}
	
	//-------------------------------
	// AGGIORNAMENTO DATASET CLIENTI
	//-------------------------------
	private void aggiornaDatasetClienti() {
	    datasetClienti.clear();
	    boolean perMese = rbClientiMese.isSelected();

	    // =========================
	    // TOTALI
	    // =========================
	    if (chkTotali.isSelected()) {

	        if (perMese) {
	            caricaSerie(Query.clientiTotaliPerMese(), "Totali");
	        } else {
	            caricaSerie(Query.clientiTotaliPerAnno(), "Totali");
	        }
	    }
	    
	    // ATTIVI
	    if (chkAttivi.isSelected()) {

	        if (perMese) {
	            caricaSerie(Query.clientiAttiviPerMese(), "Attivi");
	        } else {
	            caricaSerie(Query.clientiAttiviPerAnno(), "Attivi");
	        }
	    }
	    
	    // =========================
	    // ARCHIVIATI
	    // =========================
	    if (chkArchiviati.isSelected()) {

	        if (perMese) {
	            caricaSerie(Query.clientiArchiviatiPerMese(), "Archiviati");
	        } else {
	            caricaSerie(Query.clientiArchiviatiPerAnno(), "Archiviati");
	        }
	    }
	
	}
	
	//----------------------
	// CARICA SERIE
	//----------------------
	private void caricaSerie(String query, String nomeSerie) {
		
		Connection conn = interfaccia.getDbManager().getConnessione();
		
		try(PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {
			
			while(rs.next()) {
				String periodo = rs.getString("periodo");
				int valore = rs.getInt("totale");
				
				datasetClienti.addValue(valore, nomeSerie, periodo);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//----------------------------------
	//----------PROGETTI----------------
	//----------------------------------
	
	private JPanel creaPannelloProgetti() {
		
		JPanel pnl = new JPanel(new BorderLayout());
		
		// CONTROLLI
		rbProgettiMese = new JRadioButton("Mese", true);
		rbProgettiAnno = new JRadioButton("Anno");
		
		ButtonGroup gruppo = new ButtonGroup();
		gruppo.add(rbProgettiMese);
		gruppo.add(rbProgettiAnno);
		
		JPanel pnlControlli = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlControlli.add(new JLabel("Periodo:"));
		pnlControlli.add(rbProgettiMese);
		pnlControlli.add(rbProgettiAnno);
		
		pnl.add(pnlControlli, BorderLayout.NORTH);
		
		// GRAFICO
		datasetProgetti = new DefaultCategoryDataset();
		
		chartProgetti = ChartFactory.createBarChart(
				"Numero Progetti",
				"Perido",
				"Progetti",
				datasetProgetti
				);
		
		ChartPanel chartPanel = new ChartPanel(chartProgetti);
		chartPanel.setPreferredSize(new Dimension(900, 400));
		
		pnl.add(chartPanel, BorderLayout.CENTER);
		
		// ================= LISTENER =================
	    rbProgettiMese.addActionListener(e -> aggiornaDatasetProgetti());
	    rbProgettiAnno.addActionListener(e -> aggiornaDatasetProgetti());

	    

	    return pnl;
		
	}
	
	private void aggiornaDatasetProgetti() {

	    datasetProgetti.clear();

	    Connection conn = interfaccia.getDbManager().getConnessione();

	    String query;

	    if (rbProgettiMese.isSelected()) {
	        query = Query.progettiPerMese();
	    } else {
	        query = Query.progettiPerAnno();
	    }

	    try (PreparedStatement ps = conn.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {

	            String periodo = rs.getString("periodo");
	            int totale = rs.getInt("totale");

	            datasetProgetti.addValue(totale, "Progetti", periodo);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	//----------------------------
	// PANNELLO FATTURE
	//----------------------------
	private JPanel creaPannelloFatture() {
		
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Fatture emesse nel tempo"));
		
		//--------CONTROLLI--------------
		rbFattureMese = new JRadioButton("Mese", true);
		rbFattureAnno = new JRadioButton("Anno");
		
		ButtonGroup gruppo = new ButtonGroup();
		gruppo.add(rbFattureMese);
		gruppo.add(rbFattureAnno);
		
		JPanel pnlControlli = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlControlli.add(new JLabel("Periodo:"));
		pnlControlli.add(rbFattureMese);
		pnlControlli.add(rbFattureAnno);
		
		pnl.add(pnlControlli, BorderLayout.NORTH);
		
		//------------GARFICO------------
		datasetFatture = new DefaultCategoryDataset();
		
		chartFatture = ChartFactory.createBarChart(
				"Numero Fatture",
				"Periodo",
				"Fatture",
				datasetFatture
				);
		
		ChartPanel chartPanel = new ChartPanel(chartFatture);
		chartPanel.setPreferredSize(new Dimension(900, 400));
		
		pnl.add(chartPanel, BorderLayout.CENTER);
		
		// ================= LISTENER =================
	    rbFattureMese.addActionListener(e -> aggiornaDatasetFatture());
	    rbFattureAnno.addActionListener(e -> aggiornaDatasetFatture());

	    

	    return pnl;
	}
	
	private void aggiornaDatasetFatture() {

	    datasetFatture.clear();

	    Connection conn = interfaccia.getDbManager().getConnessione();

	    String query;

	    if (rbFattureMese.isSelected()) {
	        query = Query.fatturePerMese();
	    } else {
	        query = Query.fatturePerAnno();
	    }

	    try (PreparedStatement ps = conn.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {

	            String periodo = rs.getString("periodo");
	            int totale = rs.getInt("totale");

	            datasetFatture.addValue(totale, "Fatture", periodo);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	//-----------------------------
	// PANNELLO PAGAMENTI----------
	//-----------------------------
	
	private JPanel creaPannelloMetodiPagamento() {
		
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Metodi di pagamento"));
		
		datasetMetodiPagamento = new DefaultPieDataset();
		
		chartMetodiPagamento = ChartFactory.createPieChart(
				"Metodi di pagamento più utilizzato",
				datasetMetodiPagamento,
				true, //legenda
				true, //tooltip etichetta quando passi sopra col mouse
				false // URL
				);
		
		ChartPanel chartPanel = new ChartPanel(chartMetodiPagamento);
		chartPanel.setPreferredSize(new Dimension(700, 400));
		
		pnl.add(chartPanel, BorderLayout.CENTER);
		
		
		
		return pnl;
	}
	
	private void aggiornaDatasetMetodiPagamento() {

	    datasetMetodiPagamento.clear();

	    Connection conn = interfaccia.getDbManager().getConnessione();

	    try (PreparedStatement ps = conn.prepareStatement(Query.conteggioPagamentiPerMetodo());
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {

	            String metodo = rs.getString("metodo");
	            int totale = rs.getInt("totale");

	            datasetMetodiPagamento.setValue(metodo, totale);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void aggiornaAttivita() {

	    if (interfaccia.getDbManager() == null) return;
	    if (interfaccia.getDbManager().getConnessione() == null) return;

	    aggiornaDatasetClienti();
	    aggiornaDatasetProgetti();
	    aggiornaDatasetFatture();
	    aggiornaDatasetMetodiPagamento();
	}

	
}
