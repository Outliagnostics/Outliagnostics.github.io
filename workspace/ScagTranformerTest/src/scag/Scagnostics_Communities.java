package scag;
/*
 * Scagnostics
 *
 * Leland Wilkinson (SPSS, Inc.) and Anushka Anand (University of Illinois at Chicago)
 * This program accompanies the paper by Leland Wilkinson, Anushka Anand, and Robert Grossman
 * called Graph-Theoretic Scagnostics
 * Proceedings of the IEEE Symposium on Information Visualization
 * Minneapolis, MN October 23-25, 2005.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software. Supporting documentation must also include a citation of
 * the abovementioned article, Graph-Theoretic Scagnostics
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class Scagnostics_Communities extends Frame {
    private static final long serialVersionUID = 1L;
	private static int numVars = 0;
    private static int numPoints = 0;
    private static int numScagnostics = Triangulation.numScagnostics;
    private static double[] dataMin, dataMax;
    public static double[][] data = null;
    public static double[][] scagnostics = null;
    public static double[][] sscagnostics = null;
    public static String[] scagnosticsLabels = Triangulation.scagnosticsLabels;
	public static int sx, sy;
		
    
    private static boolean getData(String fname) {
        java.io.BufferedReader fin;
        try {
            fin = new java.io.BufferedReader(new java.io.FileReader(fname));
        } catch (java.io.FileNotFoundException fe) {
            javax.swing.JOptionPane.showMessageDialog(null, "1 File not found!", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            numPoints = 1994;
            numVars = 20;
            System.out.println("Number of rows, cols " +numVars+" "+numPoints);

            //Read in the data
            data = new double[numVars][numPoints];
            fin = new java.io.BufferedReader(new java.io.FileReader(fname));
            initializeMinMax();
            String sText = fin.readLine();
            int row = 0;
            while (sText != null) {
            	//System.out.println("Reading row "+row+": "+sText);
            	StringTokenizer st = new StringTokenizer(sText.trim(),",");
                
            	for (int i = 0; i < numVars; i++) {
        			String tmp = st.nextToken();
        			// System.out.println("i "+i+" "+p+" "+tmp);
                	 try {
                    		if (!tmp.equals("?")){
                    			if (i==3){  // Read city names
                    				data[i][row] = row;
                    			}
                    			else{
                    				data[i][row] = Double.parseDouble(tmp);
                    			}
                    		}
                    		else{
                    			//System.out.println("i "+i+" ****"+tmp+"*****");
                    			data[i][row] =0;
                    		}	
                    		updateMinMax(data[i][row], i);
                    } catch (Exception ie) {
                    	ie.printStackTrace();
                        return false;
                    }
            	}
                sText = fin.readLine();
                row++;
            }
            fin.close();
            return true;
        } catch (java.io.IOException ie) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error reading from the file", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void initializeMinMax() {
        dataMin = new double[numVars];
        dataMax = new double[numVars];
        for (int i = 0; i < numVars; i++) {
            dataMin[i] = Double.POSITIVE_INFINITY;
            dataMax[i] = Double.NEGATIVE_INFINITY;
        }
    }

    private static void updateMinMax(double d, int i) {
        if (d < dataMin[i])
            dataMin[i] = d;
        if (d > dataMax[i])
            dataMax[i] = d;
    }
    

    private static BufferedWriter openOutputFileWithHeaderRecord() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("CommunitiesOutput.txt"));
            for (int i = 0; i < scagnosticsLabels.length; i++) {
                out.write(scagnosticsLabels[i] + " ");
            }
            out.newLine();
            return out;
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error writing file", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static boolean writeStandardizedData() {
        try {
        	System.out.println("data.length:"+data.length+" data[0].length:"+data[0].length);
        	BufferedWriter out1 = new BufferedWriter(new FileWriter("CommunitiesStandardized.txt"));
        	for (int j = 0; j < data[0].length; j++) {
        		 for (int i = 0; i < data.length; i++) {
                	out1.write(data[i][j]+"\t");
            	}
        		out1.newLine();
                    
                
            }
            out1.close();
            return true;
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error writing file", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private static boolean writeMeasures(BufferedWriter out, int sample, int n, boolean isRandom) {
        try {
            for (int j = 0; j < scagnostics[0].length; j++) {
                if (isRandom)
                    out.write((sample + 1) + " " + n + " " + (j + 1) + " ");
                for (int i = 0; i < scagnostics.length; i++) {
                    out.write(scagnostics[i][j] + " ");
                }
                out.newLine();
            }
            return true;
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error writing file", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
   
    
    private static void normalizeData() {
        for (int i = 0; i < numVars; i++) {
            for (int j = 0; j < numPoints; j++) {
                data[i][j] = (data[i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
                if (data[i][j]<0 || data[i][j]>1){
              //  	System.err.println(" i:"+i+" j:"+j+" data[i][j]"+data[i][j]);
                }
            }
        }
    }

    private static void computeScagnosticsOnFileData() {
        int numCells = numVars * (numVars - 1) / 2;
        scagnostics = new double[numScagnostics][numCells];
        int k = 0;
        for (int i = 1; i < numVars; i++) {
            for (int j = 0; j < i; j++) {
            	/*if (k<=8060){//1257){  // Exception in SwapTest
            		k++;
            		continue;
            	}*/
            	System.out.println("computeScagnosticsOnFileData: "+k);
            	
                Triangulation dt = new Triangulation();
                double[] mt = dt.compute(data[j], data[i], false);
                if (mt == null)
                    continue;
                for (int m = 0; m < numScagnostics; m++) {
                    if (Double.isNaN(mt[m]))
                        mt[m] = 0;
                    scagnostics[m][k] = mt[m];
                }
                k++;
            }
        }
    }
   
    public static void main(String argv[]) {
    	String fileName = "CommunitiesData.txt";
    	System.out.println("   fileName: " + fileName);
    	if (!getData(fileName)){
    		System.out.println(" FAIL to getData: " + fileName);
        		
    	}
    		
    	normalizeData();
        writeStandardizedData();
        computeScagnosticsOnFileData();
        BufferedWriter outFile = openOutputFileWithHeaderRecord();
        writeMeasures(outFile, 0, 0, false);
        try {
            outFile.close();
        } catch (IOException e) {
            System.exit(1);
        }
    }
}

