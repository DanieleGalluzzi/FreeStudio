package freeStudio;

import javax.swing.*;



public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Interfaccia finestraMain = new Interfaccia();
				finestraMain.setVisible(true);
			}
		});

	}

}
