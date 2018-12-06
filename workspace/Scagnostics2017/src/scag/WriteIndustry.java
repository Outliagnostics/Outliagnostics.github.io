package scag;

import java.io.*;
import java.util.ArrayList;

import processing.core.PApplet;

public class WriteIndustry extends PApplet {

	public static void main(String argv[]) {
		WriteIndustry w = new WriteIndustry();
		String[] lines = w.loadStrings("IndustryFull.txt");
		String[][] p = new String[lines.length][];
		for (int k = 0; k < lines.length; k++) {
			p[k] = lines[k].split(" ");
			System.out.println(p[k][0] + " " + p[k][1]);
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"src/IndustryFull2.txt"));
			for (int k = 0; k < lines.length; k++) {
				for (int i = 2;i<p[k].length;i++){
					if (i==2)
						out.write(p[k][i]+"\t");
					else	
						out.write(p[k][i]+" ");
				}
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			javax.swing.JOptionPane.showMessageDialog(null,
					"Error writing file", "Alert",
					javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}

}