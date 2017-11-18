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
import java.util.StringTokenizer;


public class Scagnostics extends Frame {
    private static final long serialVersionUID = 1L;
	private static int numVars = 0;
    private static int numRows = 0;
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
            javax.swing.JOptionPane.showMessageDialog(null, "File not found!", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            String sText = null;	//To get the line of text from the file
            sText = fin.readLine();
            StringTokenizer st = new StringTokenizer(sText,",");	//default delimiter = space
            numVars = st.countTokens();
            numRows = 0;
            while (sText != null) {
            	 sText = fin.readLine();
            	 numRows++;
            }
            fin.close();
            System.out.println("Number of rows, cols " + numRows + " " + numVars);

            //Read in the data
            fin = new java.io.BufferedReader(new java.io.FileReader(fname));
            data = new double[numVars][numRows];
            initializeMinMax();
            sText = fin.readLine();
            int j = 0;
            while (sText != null) {
            	st = new StringTokenizer(sText,",");
                for (int i = 0; i < numVars; i++) {
                	 String tmp = st.nextToken();
                     try {
                    	 data[i][j] = Double.parseDouble(tmp);
                        updateMinMax(data[i][j], i);
                    } catch (Exception ie) {
                    	ie.printStackTrace();
                        return false;
                    }
                }
                sText = fin.readLine();
                j++;
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
            dataMin[i] = Double.MAX_VALUE;
            dataMax[i] = -dataMin[i];
        }
    }

    private static void updateMinMax(double d, int i) {
        if (d < dataMin[i])
            dataMin[i] = d;
        if (d > dataMax[i])
            dataMax[i] = d;
    }
    

    private static BufferedWriter openOutputFileWithHeaderRecord(int day) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("Output/scagnostics"+day+".txt"));
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
            for (int j = 0; j < numRows; j++) {
                data[i][j] = (data[i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
            }
        }
    }

    private static void computeScagnosticsOnFileData() {
        int numCells = numVars * (numVars - 1) / 2;
        scagnostics = new double[numScagnostics][numCells];
        int k = 0;
        for (int i = 1; i < numVars; i++) {
            for (int j = 0; j < i; j++) {
                Binner b = new Binner();
                BinnedData bdata = b.binHex(data[j], data[i], BinnedData.BINS);
                Triangulation dt = new Triangulation();
                double[] mt = dt.compute(bdata, false);
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
    	for (int d = 1;d<366;d++){
    	    System.out.println("   Day: " + d);
    	    getData("2005-DaysData/day"+d+".txt");
	        normalizeData();
	        computeScagnosticsOnFileData();
	        BufferedWriter outFile = openOutputFileWithHeaderRecord(d);
	        writeMeasures(outFile, 0, 0, false);
	        try {
	            outFile.close();
	        } catch (IOException e) {
	            System.exit(1);
	        }
    	}
    }
}

