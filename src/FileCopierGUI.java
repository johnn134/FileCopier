/*
 * FileCopierGUI.java
 * 
 * @authors: John Nelson
 */

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings("serial")
public class FileCopierGUI extends JFrame implements ActionListener {
	
	//Variables
	private JButton runButton;
	private JButton fileFindButton;
	private JButton csvFindButton;
	private JButton folderFindButton;
	
	private JTextArea filePathTextArea;
	private JTextArea csvPathTextArea;
	private JTextArea folderPathTextArea;
	
	private JLabel progressLabel;
	
	private JFileChooser fc;
	
	private String filePath = "";
	private String csvPath = "";
	private String copyPath = "";
	
	
	/*
	 * ImageCopierGUI
	 * 
	 * Default Constructor for the GUI
	 */
	public FileCopierGUI() {
		init();
	}
	
	/*
	 * init
	 * 
	 * Initializes the GUI Element
	 */
	private void init() {
		//Initialize the default path to the copying folder
		copyPath = FileCopy.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		copyPath = copyPath.substring(0, copyPath.lastIndexOf("/") + 1);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = 500;
		int h = 200;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		
		this.setLocation(x, y);
		
		//Create the GUI Elements
		progressLabel = new JLabel();
		filePathTextArea = new JTextArea(1,30);
		csvPathTextArea = new JTextArea(1,30);
		folderPathTextArea = new JTextArea(1,30);
		folderPathTextArea.setText(copyPath);
		fc = new JFileChooser();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("File Copier");
		
		//Create Image Finder Button
		fileFindButton = new JButton("Find Source File");
		fileFindButton.addActionListener(this);
		
		//Create CSV Finder Button
		csvFindButton = new JButton("Find CSV File");
		csvFindButton.addActionListener(this);
		
		//Create folder Finder Button
		folderFindButton = new JButton("Find Target Folder");
		folderFindButton.addActionListener(this);
		
		//Create Run Button
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		
		//Set Progress label
		progressLabel.setText("Please find a source and csv file.");
		
		//Set up the group layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//Fill layout with components
		layout.setHorizontalGroup(
				layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(fileFindButton)
								.addComponent(filePathTextArea))
						.addGroup(layout.createSequentialGroup()
								.addComponent(csvFindButton)
								.addComponent(csvPathTextArea))
						.addGroup(layout.createSequentialGroup()
								.addComponent(folderFindButton)
								.addComponent(folderPathTextArea))
						.addComponent(runButton)
						.addComponent(progressLabel));
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(fileFindButton)
								.addComponent(filePathTextArea))
						.addGroup(layout.createParallelGroup()
								.addComponent(csvFindButton)
								.addComponent(csvPathTextArea))
						.addGroup(layout.createParallelGroup()
								.addComponent(folderFindButton)
								.addComponent(folderPathTextArea))
						.addComponent(runButton)
						.addComponent(progressLabel)
		);
		
		pack();
	}
	
	/*
	 * actionPerformed
	 * 
	 * Overloaded function for ActionListener
	 * Called whenever the listener catches a button click
	 * 
	 * @param e - button click action
	 */
	public void actionPerformed(ActionEvent e) {
		/*
		 * Process actions from clicking the image find button
		 */
		if(e.getSource() == fileFindButton) {
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int returnVal = fc.showOpenDialog(FileCopierGUI.this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {	//set variables for src file
				File file = fc.getSelectedFile();
				
				filePath = file.getPath();
				filePathTextArea.setText(filePath);
				progressLabel.setText("File Found.");
			}
		}
		/*
		 * Process actions from clicking the csv find button
		 */
		else if(e.getSource() == csvFindButton) {
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int returnVal = fc.showOpenDialog(FileCopierGUI.this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {	//set variables for csv file
				File file = fc.getSelectedFile();

				if(!file.getPath().substring(file.getPath().length() - 3, 
											 file.getPath().length()).equals("csv")) {	//check file type
					progressLabel.setText("Please select a .csv file.");
				}
				else {
					csvPath = file.getPath();
					csvPathTextArea.setText(csvPath);
					progressLabel.setText("CSV Found.");
				}
			}
		}
		/*
		 * Process actions from clicking the folder find button
		 */
		else if(e.getSource() == folderFindButton) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int returnVal = fc.showOpenDialog(FileCopierGUI.this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {	//set variables for folder
				File file = fc.getSelectedFile();
				
				copyPath = file.getPath();
				folderPathTextArea.setText(copyPath);
			}
		}
		/*
		 * Process actions from clicking the run button
		 */
		else if(e.getSource() == runButton) {
			if(filePath.equals("") && csvPath.equals("")) {	//error check for present files
				progressLabel.setText("No source file or csv selected.");
			}
			else {
				if(filePath.equals(""))		//ensure src presence
					progressLabel.setText("No source file selected");
				else if(csvPath.equals(""))	//ensure csv presence
					progressLabel.setText("No csv selected");
				else {
					progressLabel.setText("Copying Files");
					process(filePath, csvPath);
				}
			}
		}
	}
	
	/*
	 * process
	 * 
	 * Reads an image file and a csv file and 
	 * copies the image for each name found in the csv file
	 * 
	 * @param im - path to the image file
	 * @param csv - path to the csv file
	 */
	private void process(String src, String csv) {
		BufferedReader br = null;
		FileInputStream in = null;
		FileOutputStream out = null;
		File outputFile;
		String line = "";
		String cvsSplitBy = ",";

		try {
			//Read in the csv and image files
			br = new BufferedReader(new FileReader(new File(csv)));
			
			//Read through each line of the csv
			while((line = br.readLine()) != null) {
				String[] newName = line.split(cvsSplitBy);	//get the next filename

				outputFile = new File(copyPath + "\\" + newName[0] + getExtension(src));	//set the new output name

				in = new FileInputStream(src);
				out = new FileOutputStream(outputFile);
				
				byte[] buffer = new byte[1024];
				
				while(in.read(buffer) != -1) {
					out.write(buffer);	//copy src to output file
				}

				in.close();	//close src so we can read it again
				out.close();
			}
			
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			if (br != null) {
				try {
					br.close();		//close csv reader
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		progressLabel.setText("Finished");	//update progress text
	}
	
	/*
	 * getExtension
	 * 
	 * Returns the extension of a given path
	 * 
	 * @param path - string containing the file path
	 * 
	 * @return String
	 */
	public String getExtension(String path) {
		int i = 0;
		while(!path.substring(i, i+1).equals(".")) {
			i++;
		}
		return path.substring(i, path.length());
	}
}


