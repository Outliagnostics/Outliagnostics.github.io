package scagOriginal;
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

import static main.MainTestCompareRatio.*;

import java.util.List;

import processing.core.PApplet;

public class ScagnosticsRatior{
	public static List[][] MST;
	public static double[][] areaAlpha;
	public static double[][] periAlpha;
	public static Triangulation[][] DT;
	public static int[] FinkMinEdge;
	public static int[] FinkMinUncompactness;
	
	public static void computeFuncData(int f) {
		
		for (int i = 0; i < nVars; i++) {
    		for (int p = 0; p < nPoints; p++) {
    			if (f == SQUARE)
    				data[f][i][p] = Math.pow(data[0][i][p],2);
    			if (f == SQRT)
    				data[f][i][p] = Math.pow(data[0][i][p],0.25);
    			if (f == LOG){
    				double d = data[0][i][p]+0.000001; 
    				data[f][i][p] = Math.log(d);
    			
    			}
    			if (f == LOGIT){
    				double d = data[0][i][p]*0.99998+0.00001; 
    				data[f][i][p] = Math.log(d*(1-d));
    			}
    			if (f == INVERSE){
    				double d = data[0][i][p]+0.02; 
    				data[f][i][p] = 1/d;
    			}
    		}
    	}
    	
		// Restandardized LOG, LOGIT, and REVERSE
		if (f == LOG || f == LOGIT || f == INVERSE){
			double[] dataMin = new double[nVars];
	        double[] dataMax = new double[nVars];
	        for (int i = 0; i < nVars; i++) {
	            dataMin[i] = Double.POSITIVE_INFINITY;
	            dataMax[i] = Double.NEGATIVE_INFINITY;
	        }
	        for (int i = 0; i < nVars; i++) {
	    		for (int p = 0; p < nPoints; p++) {
	    			if (data[f][i][p] < dataMin[i])
	    				dataMin[i] = data[f][i][p];
	    			if (data[f][i][p] > dataMax[i])
	    				dataMax[i] = data[f][i][p];
	    		} 
	        }
	        // Normalize Data
	        for (int i = 0; i < nVars; i++) {
	            for (int j = 0; j < nPoints; j++) {
	            	data[f][i][j] = (data[f][i][j] - dataMin[i]) / (dataMax[i] - dataMin[i]);
	            }
	        }
		}
		
	}
	  
	public static void computeRatioData() {
		for (int r=0; r<numRatio; r++){
			double ratio = ratios[r];
			if (ratio<1){
		    	for (int i = 0; i < nVars; i++) {
		    		for (int p = 0; p < nPoints; p++) {
		    			dataRatio[r][i][p] = data[0][i][p]*ratio;
		    		}
		    	}
	    	}
	    	else {
	    		for (int i = 0; i < nVars; i++) {
		    		for (int p = 0; p < nPoints; p++) {
		    			dataRatio[r][i][p] = data[0][i][p]/ratio;
		    		}
		    	}
	    	}
	     	for (int i = 0; i < nVars; i++) {
	    		for (int p = 0; p < nPoints; p++) {
	    			if (dataRatio[r][i][p]>1)
	    				System.out.println("dataRatio[i][p]: "+dataRatio[i][p]);
	    		}
	    	}	
		}	
	}
		
	public static void computeMaxRatio(int f) {
		// Compute Max
		for (int r=0; r<numRatio; r++){
			int k=0;
	        for (int i = 1; i < nVars; i++) {
	    		for (int j = 0; j < i; j++) {
	    			 for (int m = 0; m < 9; m++) {
	 	                    int maxR = maxRatio[f][k][m];
	 	                    if (scagRatio[f][r][k][m]>scagRatio[f][maxR][k][m]){
	 	                    	maxRatio[f][k][m] = r;
	 	                    	if (m==2){
	 	                    	//	System.out.println("maxRatio="+maxRatio[k][m] + " maxFunc="+funcNames[f]);
	 	                    	//	System.out.println("	scagRatio[f][r][k][m]="+scagRatio[f][r][k][m] + " scagRatio[maxF][maxR][k][m]="+scagRatio[f][maxR][k][m]);
		 	                    	
	 	                    	}	
	 	                    }	   
	 	             }
	                 k++;
	    		}
	        }	
		}
			
	}
		
// For find the big differences    
	public static void computeFink() {
		MST = new List[nPairs][numRatio];
    	areaAlpha = new double[nPairs][numRatio];
    	periAlpha = new double[nPairs][numRatio];
    	DT = new Triangulation[nPairs][numRatio];
    	FinkMinEdge = new int[nPairs];
    	FinkMinUncompactness = new int[nPairs];
    	
    	System.out.println("nVars="+nVars+" "+nPairs);
		for (int r=0; r<numRatio; r++){
			int k = 0;
	    	for (int i = 1; i < nVars; i++) {
    			 for (int j = 0; j < i; j++) {
    				Binner b = new Binner();
		            BinnedData bdata = null;
		            if (ratios[r]<1){
		            	bdata = b.binHex(dataRatio[r][j], data[0][i], BinnedData.BINS);
		            }
		            else{
		            	bdata = b.binHex(data[0][j], dataRatio[r][i], BinnedData.BINS);
		            }
		          
		            Triangulation dt = new Triangulation();
		            double[] mt = dt.compute(bdata, false);
		            MST[k][r] = dt.mstEdges;
		        	areaAlpha[k][r] = dt.alphaArea;
		        	periAlpha[k][r] = dt.alphaPerimeter;
		        	DT[k][r] = dt;
					k++;
				}
		    }	
    	}
    	
    	
    	int k = 0;
    	double minEdge = Double.POSITIVE_INFINITY;
    	double minUncom = Double.POSITIVE_INFINITY;
    	for (int i = 1; i < nVars; i++) {
    		for (int j = 0; j < i; j++) {
    			for (int r=0; r<numRatio; r++){
    		    	if (periAlpha[k][r]<minEdge){
    		    		minEdge = periAlpha[k][r];
    		    		FinkMinEdge[k] =r;
    		    	}
    		    	
    		    	float f2 = (float) (periAlpha[k][r]/PApplet.sqrt((float) areaAlpha[k][r]));
    		    	if (f2<minEdge){
    		    		minUncom = f2;
    		    		FinkMinUncompactness[k] =r;
    		    	}
    			 }	
    			System.out.println("FINK Edge ratio: " + ratios[FinkMinEdge[k]]);
    			System.out.println("FINK FinkMinUncompactness: " + ratios[FinkMinUncompactness[k]]);
    		    
    			k++;
			}
	    }
	}
	    
	public static void computeWithFunction(int f) {
    	for (int r=0; r<numRatio; r++){
		//	System.out.println("\t COMPUTE for function="+funcNames[f]+"  for scale="+ratios[r]);
			int k = 0;
		    for (int i = 1; i < nVars; i++) {
				for (int j = 0; j < i; j++) {
					Binner b = new Binner();
		            BinnedData bdata = null;
		            if (ratios[r]<1){
		            	bdata = b.binHex(dataRatio[r][j], data[f][i], BinnedData.BINS);
		            }
		            else{
		            	bdata = b.binHex(data[f][j], dataRatio[r][i], BinnedData.BINS);
		            }
		            Triangulation dt = new Triangulation();
		            double[] mt = dt.compute(bdata, false);
		            
		            if (mt == null){
		           	 	k++;
		           	 	continue;
		            }    
		            for (int m = 0; m < 9; m++) {
		                if (Double.isNaN(mt[m]))
		                    mt[m] = 0;
		                //	System.out.println("\t ScagnosticsRatior = "+mt[m]);
		                //OUTLYING BUG
		                if (mt[0]>1)
		                	mt[m] =1;
		            }
		        	scagRatio[f][r][k] = mt;
		            k++;
		        }
		    }
		}
    }
}

