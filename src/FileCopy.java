/*
 * FileCopy
 * 
 * @authors: John Nelson
 */

public class FileCopy {

	public static void main(String[] args) {
		//Run the program as a runnable jar
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new FileCopierGUI().setVisible(true);
			}
		});

	}

}
