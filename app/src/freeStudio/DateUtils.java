package freeStudio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    // Formato DB (MySQL DATE)
    public static final DateTimeFormatter DB_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Formato UI italiano
    public static final DateTimeFormatter UI_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // =========================
    // DB → UI
    // =========================
    public static String dbToUi(String dataDb) {
        if (dataDb == null || dataDb.isBlank()) return "";
        LocalDate date = LocalDate.parse(dataDb, DB_FORMAT);
        return date.format(UI_FORMAT);
    }

    // =========================
    // UI → DB
    // =========================
    public static String uiToDb(String dataUi) {
        if (dataUi == null || dataUi.isBlank()) return null;
        LocalDate date = LocalDate.parse(dataUi, UI_FORMAT);
        return date.format(DB_FORMAT);
    }

    // =========================
    // OGGI (UI)
    // =========================
    public static String oggiUi() {
        return LocalDate.now().format(UI_FORMAT);
    }

    // =========================
    // STRING → LocalDate (DB)
    // =========================
    public static LocalDate dbToLocalDate(String dataDb) {
        if (dataDb == null || dataDb.isBlank()) return null;
        return LocalDate.parse(dataDb, DB_FORMAT);
    }
    
	 // =========================
	 // VALIDAZIONE UI
	 // =========================
	 public static boolean isDataUiValida(String dataUi) {
	     try {
	         LocalDate.parse(dataUi, UI_FORMAT);
	         return true;
	     } catch (Exception e) {
	         return false;
	     }
 }

	 public static String formattaUi(LocalDate data) {
		    if (data == null) return "";
		    return data.format(UI_FORMAT);
		}

	// =========================
	// DB (String) → java.util.Date
	// =========================
	public static java.util.Date dbToDate(String dataDb) {
	    if (dataDb == null || dataDb.isBlank()) return null;
	    return java.sql.Date.valueOf(dataDb); // yyyy-MM-dd
	}

	// =========================
	// java.util.Date → DB (String)
	// =========================
	public static String dateToDb(java.util.Date date) {
	    if (date == null) return null;
	    return new java.sql.Date(date.getTime()).toString();
	}

	 
}
