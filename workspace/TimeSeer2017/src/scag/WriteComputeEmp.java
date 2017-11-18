package scag;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;

public class WriteComputeEmp extends PApplet {
	public static int nI = 341;
	public static HashMap<String,Integer> hi = new HashMap<String,Integer>();
	public static HashMap<String,String> hm = new HashMap<String,String>();
	public static ArrayList<String> a = new ArrayList<String>();
	public static String line1 =""; 
	
	public static void main(String argv[]) {
		ScagnosticsWBD2017 w = new ScagnosticsWBD2017();
		String[] lines = w.loadStrings("IndustryFull2.txt");
		for (int k = 0; k < lines.length; k++) {
			String[] p = lines[k].split("\t");
			hi.put(p[0], k);
			hm.put(p[0], p[1]);
			a.add(p[0]);
		}
		lines = w.loadStrings("employ0.txt");
		line1 = lines[0];
		String pS ="";
		int[] count = new int[nI];
		for (int k = 1; k < lines.length; k++) {
			String[] p = lines[k].split(",");
			
			if (!p[0].substring(0, 5).equals(pS)){
				pS = p[0].substring(0, 5);
			}
			String code = p[0].substring(10, 18);
			//System.out.println("code:"+code);
			int ins = hi.get(code);
			count[ins]++;
		}
		
		for (int i =0; i<nI;i++){
			if (count[i]>=47){
				System.out.println(i+" "+count[i]+"   "+hm.get(a.get(i)));
			}
				
		}
		
		
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("src/employ1.txt"));
			out.write(line1);
			out.newLine();
			for (int k = 1; k < lines.length; k++) {
				String[] p = lines[k].split("\t");
				String code = p[0].substring(10, 18);
				//System.out.println(" code:"+code);
				int ins = hi.get(code);
				if (count[ins]>=47){
					out.write(lines[k]);
					if(k<lines.length-1)
						out.newLine();
				}
			}
			out.close();
		} catch (IOException e) {
			javax.swing.JOptionPane.showMessageDialog(null,
					"Error writing file", "Alert",
					javax.swing.JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("src/IndustryFiltered.txt"));
			for (int k = 0; k < nI; k++) {
				String code = a.get(k);
				int ins = hi.get(code);
				if (count[ins]>=47){
					out.write(code+"\t"+hm.get(code));
					if(k<nI-1)
						out.newLine();
				}
			}
			out.close();
		} catch (IOException e) {
			javax.swing.JOptionPane.showMessageDialog(null,
					"Error writing file", "Alert",
					javax.swing.JOptionPane.ERROR_MESSAGE);
		}
		
	}

}