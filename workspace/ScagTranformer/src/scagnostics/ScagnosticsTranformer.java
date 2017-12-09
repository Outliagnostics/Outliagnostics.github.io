package scagnostics;
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

import static main.Main.*;

public class ScagnosticsTranformer{
	// Use this function for drawing
	public static double computeFuncData(int f, double input) {
		if (f == NONE)
			return input;
		if (f == HALF)
			return input/2;
		if (f == SQUARE)
			return Math.pow(input,2);
		if (f == SQRT)
			return Math.pow(input,0.5);
		if (f == LOG){
			double eps = 0.000001;
			double d = input+eps;
			double val = Math.log(d);
			double min = Math.log(eps);
			double max = Math.log(1+eps);
			return (val-min)/(max-min);
		}
		if (f == INVERSE){
			 double eps = 0.01;// summited 1st round
			//double eps = 0.05; 
			double d = input+eps; 
			double val = 1/d;
			double max = 1/eps;
			double min = 1/(1+eps);
			return (val-min)/(max-min);
		}
		if (f == LOGIT){
			double eps = 0.00001;
			double d = input*(1-2*eps)+eps;
			double val = Math.log(d/(1-d))/20+0.5;
			double max = Math.log((1-eps)/(eps))/20+0.5;
			double min = Math.log(eps/(1-eps))/20+0.5;
			
			return (val-min)/(max-min);
		}
		//if (f == LOGIT_INVERSE){
		//	return 1- 1/(  Math.pow(Math.E, 20*input-10) +1  );
		//	return 1/( Math.pow(Math.E, -20*input+10) +1  );
		//}
		if (f == SIGMOID){
			double d = -input*20+10; 
			return 1/(1+Math.exp(d));
		}
		
		System.err.println("could NOT find the function");
		return Double.NaN;
 	}	
	public static void computeFuncData(int f) {
		for (int i = 0; i < nVars; i++) {
    		for (int p = 0; p < nPoints; p++) {
    			data2[f][i][p] = computeFuncData(f,data[i][p]);
    	 	}
    	}
	}
	  
	
	
	// Compute Scagnostics for combination fx-fy
	public static void computeScagnostics(int fx, int fy) {
    	int k = 0;
    	for (int i = 1; i < nVars; i++) {
			for (int j = 0; j < i; j++) {
				Binner b = new Binner();
	            BinnedData bdata = null;
	            bdata = b.binHex(data2[fx][j], data2[fy][i], BinnedData.BINS);
	            
	        	Triangulation dt = new Triangulation();
	            double[] mt = dt.compute(bdata, false);
	            
	            if (mt == null){
	           	 	k++;
	           	 	continue;
	            }    
	            for (int m = 0; m < 9; m++) {
	                if (Double.isNaN(mt[m]))
	                    mt[m] = 0;
	                //OUTLYING BUG
	                if (mt[0]>1)
	                	mt[m] =1;
	            }
	        	scagRatio[fx][fy][k] = mt;
	            k++;
	        }
	    }
    }

	// Compute Scagnostics for Testing in Section 4.1
	public static void computeScagnosticsForTesing41(int fx, int fy) {
    	int k = 0;
    	long tBin = 0;
    	long tCompute = 0;
    	for (int i = 1; i < nVars; i++) {
			for (int j = 0; j < i; j++) {
				if (k==10637) continue;
			//	System.out.println(k);
		    	long t5 = System.currentTimeMillis();
		    	Binner b = new Binner();
	            BinnedData bdata = null;
	            bdata = b.binHex(data2[fx][j], data2[fy][i], BinnedData.BINS);
	            long t6 = System.currentTimeMillis();
	            tBin += (t6-t5);
	            
	            
	            long t7 = System.currentTimeMillis();
		    	Triangulation dt = new Triangulation();
	            double[] mt = dt.compute(bdata, false);
	            long t8 = System.currentTimeMillis();
	            tCompute += (t8-t7);
	            
	            k++;
	        }
	    }
    	main.Main.bin[fx] = tBin;
    	main.Main.computeScag[fx] = tCompute;
    }

	// Compute Scagnostics for Testing in Section 4.2
	public static void computeScagnosticsForTesing42(int fx, int fy) {
    	long t7 = System.currentTimeMillis();
    	t7 = System.currentTimeMillis();
    	t7 = System.currentTimeMillis();
    	for (int i = 1; i < nVars; i++) {
			for (int j = 0; j < i; j++) {
				if (i==99 || j==58) continue;
				//System.out.println(i+" "+j);
		    	Binner b = new Binner();
	            BinnedData bdata = null;
	            bdata = b.binHex(data2[fx][j], data2[fy][i], BinnedData.BINS);
	            
	            Triangulation dt = new Triangulation();
	            dt.compute(bdata, false);
	        }
	    }
    	long t8 = System.currentTimeMillis();
        
    	main.Main.computeScag42[fx][fy] = t8-t7;
    	System.out.println("fx="+fx+" fy="+fy+" time="+main.Main.computeScag42[fx][fy]);
    	
    }
	
	// Compute Scagnostics for MainVideo
	public static void computeScagnosticsMainVideo(int fx, int fy, int pairID) {
    	int k = 0;
    	for (int i = 1; i < nVars; i++) {
			for (int j = 0; j < i; j++) {
				if (k!=pairID){
					k++;
					continue;
				}
				Binner b = new Binner();
	            BinnedData bdata = null;
	            bdata = b.binHex(data2[fx][j], data2[fy][i], BinnedData.BINS);
	            
	        	Triangulation dt = new Triangulation();
	            double[] mt = dt.compute(bdata, false);
	            
	            if (mt == null){
	           	 	k++;
	           	 	continue;
	            }    
	            for (int m = 0; m < 9; m++) {
	                if (Double.isNaN(mt[m]))
	                    mt[m] = 0;
	                //OUTLYING BUG
	                if (mt[0]>1)
	                	mt[m] =1;
	            }
	        	scagRatio[fx][fy][k] = mt;
	            k++;
	        }
	    }
    }
	
    public static void computeMaxRatio() {
    	for (int fx=0; fx<numFunc; fx++){
			for (int fy=0; fy<numFunc; fy++){
				int k=0;
				if (fx==1 && fy==1) {
					k++;
					continue;
				}
				for (int i = 1; i < nVars; i++) {
		    		for (int j = 0; j < i; j++) {
		    			 for (int m = 0; m < 9; m++) {
		 	                    int maxX = maxFx[k][m];
		 	                    int maxY = maxFy[k][m];
		 	                    if (scagRatio[fx][fy][k][m]>scagRatio[maxX][maxY][k][m]){
		 	                    	maxFx[k][m] = fx;
		 	                    	maxFy[k][m] = fy;
		 	                    }	   
		 	             }
		                 k++;
		    		}
		        }	
			}    
		}
	}
}

