package scag;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;

public class ScagnosticsWBD2017 extends PApplet {
	// UnemploymentRate: 5. in 1976: Lesotho, Swazila, Comoros Dominican Republ Egypt Arab R are good inliers
		//nC = 241; 
	
	// LifeExpectancy:  9. GOOD example: Cambodia,Swaziland, Iran, Iraq.  deaths related to wars 
	// FirstMarriage: 3. missing many data points
	// PrevalenceOfHIV: 5.5 . It has many outliers and inliers in one plot
	// PrevalenceObesity: 7. USA is an outlier from 2000 to 2014
		
	// PrevalenceOfOverweight: ok 6
	// PrimaryCompletion: good 8 -> Maldives in year 2000 as outlier and Nicaragua 1986 as inlier
	// ProgressionSecondary: 3. Not many points
	// RatioUnemployments: 7. Ethiopia in 2005 and Quatar 2011 are good example of outliers
		// out.json may have problem of quotation	
	// SchoolEnrollment: 5
	// PrevalenceSmoking: 6. good example of outliers of different types
	
	// Account: 6 (only 2 years data) Good example of 2D outlier. Shoudl be in the paper
	// FertilityDeath: Good example of 2D outlier. SHoudl be in the paper: Figure 1, year 1960
		//https://www.google.com/publicdata/explore?ds=d5bncppjof8f9_&ctype=b&strail=false&nselm=s&met_x=sp_dyn_le00_in&scale_x=lin&ind_x=false&met_y=sp_dyn_tfrt_in&scale_y=lin&ind_y=false&met_s=sp_pop_totl&scale_s=lin&ind_s=false&dimp_c=country:region&ifdim=country&iconSize=0.5&uniSize=0.035#!ctype=b&strail=false&bcs=d&nselm=s&met_x=sp_dyn_cdrt_in&scale_x=lin&ind_x=false&met_y=sp_dyn_tfrt_in&scale_y=lin&ind_y=false&met_s=sp_pop_totl&scale_s=lin&ind_s=false&dimp_c=country:region&ifdim=country&pit=1447866000000&hl=en_US&dl=en_US&ind=false
	
	public static String filename = "data/LifeExpectancy.txt";
	public static int nC = 217; // Unemployment rate
	public static int selectYear = 1960;
	public static String remove1 = "Gabon";
	public static String remove2 = "Niger";	
	public static String remove3 = "Mali";
	public static String remove4 = "Argentina";
	public static String remove5 = "United Arab Emirates";
		
	
	
	
		
	public static int startYear = 1960;
	
	public static int nY = 56; // No data for 2016 and 2017
	public static int nV = 2;
	public static String[] countries = new String[nC];
	public static String[] varirables = new String[nV];
	
	public static double[][][] data = new double[nV][nY][nC];
	public static double[][][] dataS = new double[nV][nY][nC];
	private static int numScagnostics = Triangulation.numScagnostics;
	public static double[][][] scagnostics = new double[nV][nY][numScagnostics];
	public static double[][][][] scagnosticsLeave1out = new double[nV][nY][nC][numScagnostics];
	public static String[] scagnosticsLabels = Triangulation.scagnosticsLabels;
	public static int sx, sy;
	public static String line1 =""; 
	
	// For Drawing
	public static List MST1;
	public static List MST2;
	public static List MST3;
	public static List MST4;
	public static List MST5;
	public static List MST6;
	public static double areaAlpha1 = 0;
	public static double areaAlpha2 = 0;
	public static double areaAlpha3 = 0;
	public static double areaAlpha4 = 0;
	public static double areaAlpha5 = 0;
	public static double areaAlpha6 = 0;
	public static double periAlpha1 = 0;
	public static double periAlpha2 = 0;
	public static double periAlpha3 = 0;
	public static double periAlpha4 = 0;
	public static double periAlpha5 = 0;
	public static double periAlpha6 = 0;
	public static Triangulation DT1;
	public static Triangulation DT2;
	public static Triangulation DT3;
	public static Triangulation DT4;
	public static Triangulation DT5;
	public static Triangulation DT6;
	public static boolean[] isOutliers;
	public static boolean[] isOutliers2;
	public static boolean[] isOutliers3;
	public static boolean[] isOutliers4;
	public static boolean[] isOutliers5;
	public static boolean[] isOutliers6;
	public static double[] scagnostics1 = new double[numScagnostics];
	public static double[] scagnostics2 = new double[numScagnostics];
	public static double[] scagnostics3 = new double[numScagnostics];
	public static double[] scagnostics4 = new double[numScagnostics];
	public static double[] scagnostics5 = new double[numScagnostics];
	public static double[] scagnostics6 = new double[numScagnostics];
	
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	float x = 40;
	float y = 30;
	float size = 185;
	
	public static double[] data11;
	public static double[] data12;
	public static double[] data21;
	public static double[] data22;
	public static double[] data31;
	public static double[] data32;
	public static double[] data41;
	public static double[] data42;
	public static double[] data51;
	public static double[] data52;
	public static double[] data61;
	public static double[] data62;

	public static void main(String args[]){
	  PApplet.main(new String[] { ScagnosticsWBD2017.class.getName() });
    }
	
	public void setup() {
		size(1280, 850);
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		smooth();
		
		getData();
		normalizeMinMax();
		//writeStandardizedData();
		
		// Write Scagnostics
		computeScagnosticsOnFileData();
		writeOut();
		
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
	}
	
	public void draw() {
		this.background(255,255,255);
		
		boolean showMST =true;
		boolean alphaShape =true;
		boolean convexHull =true;
		float xoff = x;
		
		drawDT("Original",showMST,alphaShape,convexHull,
				data11, data12, MST1, areaAlpha1,periAlpha1, DT1, isOutliers, scagnostics1,xoff,1);
		
		int xMargin =22;
		xoff = x +xMargin/5 +(size+xMargin);
		drawDT("Leave "+remove1+" out",showMST,alphaShape,convexHull,
				data21, data22, MST2, areaAlpha2,periAlpha2, DT2, isOutliers2, scagnostics2, xoff,1);
		xoff = x +xMargin/5+(size+xMargin)*2;
		drawDT("Leave "+remove2+" out",showMST,alphaShape,convexHull,
				data31, data32, MST3, areaAlpha3,periAlpha3, DT3, isOutliers3, scagnostics3, xoff,1);
		xoff = x +xMargin/5+(size+xMargin)*3;
		drawDT("Leave "+remove3+" out",showMST,alphaShape,convexHull,
				data41, data42, MST4, areaAlpha4,periAlpha4, DT4, isOutliers5, scagnostics4, xoff,1);
		xoff = x +xMargin/5+(size+xMargin)*4;
		drawDT("Leave "+remove4+" out",showMST,alphaShape,convexHull,
				data51, data52, MST5, areaAlpha5,periAlpha5, DT5, isOutliers5, scagnostics5, xoff,1);
		xoff = x +xMargin/5+(size+xMargin)*5;
		drawDT("Leave "+remove5+" out",showMST,alphaShape,convexHull,
				data61, data62, MST6, areaAlpha6,periAlpha6, DT6, isOutliers6, scagnostics6, xoff,1);
	}

	
	static final String ZEROES = "000000000000";
	static final String BLANKS = "            ";
	static String format( double val, int n, int w) 
	{
		if (Double.isNaN(val))
			return "\"NaN\"";
		else if (val<0.005)
			return "0";
							
	//	rounding			
		double incr = 0.5;
		for( int j=n; j>0; j--) incr /= 10; 
		val += incr;
		
		String s = Double.toString(val);
		int n1 = s.indexOf('.');
		int n2 = s.length() - n1 - 1;
		
		if (n>n2)      s = s+ZEROES.substring(0, n-n2);
		else if (n2>n) s = s.substring(0,n1+n+1);

		if( w>0 & w>s.length() ) s = BLANKS.substring(0,w-s.length()) + s;
		else if ( w<0 & (-w)>s.length() ) {
			w=-w;
			s = s + BLANKS.substring(0,w-s.length()) ;
		}
		return s;
	}	
	
	static String formatCountry( String country){
		return country.replace("\"", "").replace(",", "");
	}	

	private static void getData() {
		ScagnosticsWBD2017 w = new ScagnosticsWBD2017();
		String[] lines = w.loadStrings(filename);
		//String[] lines = w.loadStrings("data/PrevalenceOfHIV.txt");
		line1 = lines[0];
		//nC=(int) (line1.length()-1)/2;
		for (int k = 1; k < lines.length; k++) {
			String[] p = lines[k].split("\t");
			//System.out.println("line "+k+" = "+lines[k]);
			
			int variable = (int) (k-1)/nC;
			int country = (k-1)%nC;
			
			if (variable==0)  // get the country list
				countries[country] = p[2];
			else{
				if (!countries[country].equals(p[2]))
					System.err.println("Country order does not match in the INPUT file");
			}
			if (country==0){
				varirables[variable] = p[0];
			}
			else{
				if (!varirables[variable].equals(p[0]))
					System.err.println("VARIABLE does not match in the INPUT file: "+p[0]+" "+varirables[variable]);	
			}
				
			for (int k2 = 4; k2<p.length ; k2++) {
				int year = k2-4; 
				if (year>=nY)  // No data for 2016 and 2017 
					break;
				String str = p[k2].trim();
				if (str.equals("..")) {
					data[variable][year][country] = Double.NaN;
				}
				else{
					data[variable][year][country] = Double.parseDouble(str);
				}			
			}
		}
	}
	
	private static void normalizeMinMax() {
		double[][] dataMin = new double[nV][nY];
		double[][] dataMax = new double[nV][nY];
		for (int v = 0; v < nV; v++) {
			for (int y = 0; y < nY; y++) {
				dataMin[v][y] = Double.POSITIVE_INFINITY;
				dataMax[v][y] = Double.NEGATIVE_INFINITY;
				for (int c = 0; c < nC; c++) {
					if (Double.isNaN(data[v][y][c]))
						continue;
					if (data[v][y][c] < dataMin[v][y])
						dataMin[v][y] = data[v][y][c];
					if (data[v][y][c] > dataMax[v][y])
						dataMax[v][y] = data[v][y][c];
				}
			}
		}
		// Normalize
		for (int v = 0; v < nV; v++) {
			for (int y = 0; y < nY; y++) {
				for (int c = 0; c < nC; c++) {			
					if (Double.isNaN(data[v][y][c])){
						dataS[v][y][c] = Double.NaN;
						continue;
					}
					dataS[v][y][c] = (data[v][y][c] - dataMin[v][y])
							/ (dataMax[v][y] - dataMin[v][y]);			
				}
			}
		}
	}
	
	public static double outlierCutoff =-1;
	// Make sure leave 1 out plots are computed based on the original MST length
	public static double totalMSTLength =-1;
	
	private static void computeScagnosticsOnFileData() {
		for (int v = 0; v < nV; v=v+2) {
			int variable = (int) v/2;   // get pairs of variable
			for (int y = 0; y < nY; y++) {
				System.out.print("year = "+y);
				
				// outlierCutoff make sure that the original plot and leave 1 out plot has the same cut off value
				outlierCutoff =-1;
				totalMSTLength = -1;
				Binner b1 = new Binner();
				BinnedData bdata1 = b1.binHex(dataS[v][y], dataS[v+1][y],
						BinnedData.BINS);
				Triangulation dt1 = new Triangulation();
				double[] mt1 = dt1.compute(bdata1, false);
				if (mt1 == null)
					continue;
				for (int scag = 0; scag < numScagnostics; scag++) {
					if (Double.isNaN(mt1[scag]))
						mt1[scag] = 0;
					scagnostics[variable][y][scag] = mt1[scag];
					System.out.print("\t"+format(scagnostics[variable][y][scag],2,0));
					
				}
				
				if (y==selectYear-startYear){
					data11= dataS[v][y];
					data12= dataS[v+1][y];
					MST1 = dt1.mstEdges;
		        	areaAlpha1 = dt1.alphaArea;
		        	periAlpha1 = dt1.alphaPerimeter;
		        	DT1 = dt1;
		        	scagnostics1 = scagnostics[variable][y];
		        	isOutliers = dt1.isOutlier;
		        }
				System.out.println();	
				
				// Compute leave one out outliers **************
				for (int c = 0; c < nC; c++) {			
					//System.out.print(c+" year = "+y);
					Binner b = new Binner();
					BinnedData bdata = b.binHexLeave1out(dataS[v][y], dataS[v+1][y], c,
							BinnedData.BINS);
					Triangulation dt = new Triangulation();
					double[] mt = dt.compute(bdata, false);
					if (mt == null)
						continue;
					for (int scag = 0; scag < numScagnostics; scag++) {
						if (Double.isNaN(mt[scag]))
							mt[scag] = 0;
						scagnosticsLeave1out[variable][y][c][scag] = mt[scag];
					}
					
					if (y==selectYear-startYear && countries[c].equals(remove1)){
						data21= dataS[v][y];
						data22= dataS[v+1][y];
						MST2 = dt.mstEdges;
			        	areaAlpha2 = dt.alphaArea;
			        	periAlpha2 = dt.alphaPerimeter;
			        	DT2 = dt;
			        	isOutliers2 = dt.isOutlier;
			        	scagnostics2 = scagnosticsLeave1out[variable][y][c];
					}
					else if (y==selectYear-startYear && countries[c].equals(remove2)){
						data31= dataS[v][y];
						data32= dataS[v+1][y];
						MST3 = dt.mstEdges;
			        	areaAlpha3 = dt.alphaArea;
			        	periAlpha3 = dt.alphaPerimeter;
			        	DT3 = dt;
			        	isOutliers3 = dt.isOutlier;
			        	scagnostics3 = scagnosticsLeave1out[variable][y][c];				
					}
					else if (y==selectYear-startYear && countries[c].equals(remove3)){
						data41= dataS[v][y];
						data42= dataS[v+1][y];
						MST4 = dt.mstEdges;
			        	areaAlpha4 = dt.alphaArea;
			        	periAlpha4 = dt.alphaPerimeter;
			        	DT4 = dt;
			        	isOutliers4 = dt.isOutlier;	
			        	scagnostics4 = scagnosticsLeave1out[variable][y][c];
					}
					else if (y==selectYear-startYear && countries[c].equals(remove4)){
						data51= dataS[v][y];
						data52= dataS[v+1][y];
						MST5 = dt.mstEdges;
			        	areaAlpha5 = dt.alphaArea;
			        	periAlpha5 = dt.alphaPerimeter;
			        	DT5 = dt;
			        	isOutliers5 = dt.isOutlier;      	
			        	scagnostics5 = scagnosticsLeave1out[variable][y][c];
			        }
					else if (y==selectYear-startYear && countries[c].equals(remove5)){
						data61= dataS[v][y];
						data62= dataS[v+1][y];
						MST6 = dt.mstEdges;
			        	areaAlpha6 = dt.alphaArea;
			        	periAlpha6 = dt.alphaPerimeter;
			        	DT6 = dt;
			        	isOutliers6 = dt.isOutlier;      	
			        	scagnostics6 = scagnosticsLeave1out[variable][y][c];
			        }
					//System.out.println();	
				}
			}
		}
	}

	
	private static void writeOut() {
        try {
        	System.out.println("************* writeOut *************");
            BufferedWriter out = new BufferedWriter(new FileWriter("data/out.json"));
            out.write("{");
            
            out.newLine();
        	out.write("\t\"Scagnostics\": [");
        	for (int s = 0; s < numScagnostics; s++) {  
        		if (s==0)
        			out.write("\""+scagnosticsLabels[s]+"\"");
        		else
        			out.write(", \""+scagnosticsLabels[s]+"\"");
    		}
        	out.write("],");
        	
        	out.newLine();
        	out.write("\t\"Variables\": [");
        	for (int v = 0; v < nV; v++) {  
        		if (v==0)
        			out.write(varirables[v]);
        		else
        			out.write(", "+varirables[v]);
    		}
        	out.write("],");
        	
        	out.newLine();
        	out.write("\t\"Countries\": [");
        	for (int c = 0; c < nC; c++) {  
        		if (c==0)
        			out.write("\""+formatCountry(countries[c])+"\"");
        		else
        			out.write(", \""+formatCountry(countries[c])+"\"");
    		}
        	out.write("],");
        	
        	// By year, it has redundant data for scatterplots
        	out.newLine();
        	out.write("\t\"YearsData\": [");         
        	for (int y = 0; y < nY; y++) {
	        	out.newLine();
            	out.write("\t\t{");         
            	//out.newLine();
            	for (int v = 0; v < nV; v++) {  
            		if (v==0)
            			out.write("\"s"+v+"\": [");    
            		else
            			out.write("\t\t"+" \"s"+v+"\": [");   
	            	for (int c = 0; c < nC; c++) {	
                		if (c<nC-1)
            				out.write(format(dataS[v][y][c],2,0) +", ");
            			else 
            				out.write(format(dataS[v][y][c],2,0) +"],");  	
            		}
	            	//if (v==0)
	            		out.newLine();
	            	
	            	// Write scagnostics
	            	if (v%2==1){
        				int pair = (int) v/2; 
        				out.write("\t\t\"Scagnostics"+pair+"\": [");   
	            		for (int scag = 0; scag < numScagnostics; scag++) {
							if (scag<numScagnostics-1)
		        				out.write(format(scagnostics[pair][y][scag],2,0) +", ");
		        			else if (y<nY-1)
		        				out.write(format(scagnostics[pair][y][scag],2,0) +"]},");  
		        			else	
		        				out.write(format(scagnostics[pair][y][scag],2,0) +"]}");  	
		            	}
		            }
        		}        	
	        }   
            out.newLine();
        	out.write("\t],");    
        	
        	// By countries
            out.newLine();
        	out.write("\t\"CountriesData\": {");         
  
            for (int c = 0; c < nC; c++) {	
            	out.newLine();
            	out.write("\t\t\""+formatCountry(countries[c])+"\": [");         
	        	for (int y = 0; y < nY; y++) {
	        		out.newLine();
	        		out.write("\t\t\t{");
	        		for (int v = 0; v < nV; v++) {  
	        			// Original data
	        			out.write("\"v"+v+"\": "+format(data[v][y][c],2,0) +", ");
	        			// Standardized data
	        			out.write("\"s"+v+"\": "+format(dataS[v][y][c],2,0) +", ");
	        			// Write scagnostics for that country
	        			if (v%2==1){
	        				int pair = (int) v/2; 
		        			for (int scag = 0; scag < numScagnostics; scag++) {
								out.write("\""+scagnosticsLabels[scag]+"\": "+format(scagnosticsLeave1out[pair][y][c][scag],2,0) +", ");
		        			}
	        				out.write("\"year\": "+ y);
	        			}
	        		}
	        		if (y<nY-1)
	        			out.write("},");
	        		else if (c<nC-1)
	        			out.write("}],");
	        		else 
	        			out.write("}]");
                }
	        	//out.newLine();
	        	//out.write("\t\t],");
            }   
            out.newLine();
        	out.write("\t}");         
        	
        	out.newLine();
            out.write("}");
            out.close();
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error writing OUPUT file", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
	
	
	
	public void drawDT(String label,boolean showMST, boolean alphaShape, boolean convexHull,
			double[] d1, double[] d2, List MST, double area, double peri, Triangulation DT, boolean[] isOutliers, double[] scagnostics,
			float xoff, double ratio_) {
		
		this.noStroke();
		float margin = 10;
		float gap = 22;
		double ratio = PApplet.sqrt((float) ratio_);
		float pSize1 = 6;
		float pSize = 4;
		float sat =220;

		float yoff =y;		
		
		// Draw Input data **********************************************
		this.textFont(metaBold, 14);
		fill(0,0,0);
		this.textAlign(PApplet.CENTER);
		this.text(label,(float) (xoff+size/(2*ratio)),y-15);
		/*
		this.strokeWeight(1);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		this.noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		noStroke();
		this.fill(Color.BLACK.getRGB());
		for (int i = 0; i < d1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize1, pSize1);
		}	*/
		// Draw Text Alpha Shape
		float cX;
		float cY;
		float al;
		/*if (label.equals("Original")){		
			fill(0,0,0);
			 cX = xoff-24;
			 cY = yoff + size/2;
			 al = -PApplet.PI/2;
			this.translate(cX,cY);
			this.rotate((float) (al));
			if (ratio==1)
				this.text("Input Data",0,0);
			this.rotate((float) (-al));
			this.translate(-(cX),-(cY));
		}*/
		
		
		// Trianglation **********************************************	
		if (DT==null)
			return;
		
		//yoff = yoff+(size+gap);
		this.strokeWeight(1);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		this.noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		if (alphaShape){
		  // DRAW triangle forming
			this.strokeWeight(1);
			this.stroke(255,0,0);
			this.fill(sat,sat,sat);
			Iterator tri = DT.triangles.iterator();
		    this.curveTightness(1); 
			int count=0;
		    while (tri.hasNext()) {
				Triangle triangle = (Triangle) tri.next();
				if (triangle.onComplex) {
			        Edge e1 = triangle.anEdge;
	                Edge e2 = triangle.anEdge.nextE;
	                Edge e3 = triangle.anEdge.nextE.nextE;
	                float x1 = (float) (xoff + (e1.p1.x)*size/(1000*ratio));
	                float x2 = (float) (xoff + (e2.p1.x)*size/(1000*ratio));
	                float x3 = (float) (xoff + (e3.p1.x)*size/(1000*ratio));
	                float y1 = (float) (yoff + (e1.p1.y)*size/(1000*ratio));
	                float y2 = (float) (yoff + (e2.p1.y)*size/(1000*ratio));
	                float y3 = (float) (yoff + (e3.p1.y)*size/(1000*ratio));
	                
	                this.beginShape();
	                this.curveVertex(x1, y1);
	                this.curveVertex(x1, y1);
	                this.curveVertex(x2, y2);
	                this.curveVertex(x3, y3);
	                this.curveVertex(x1, y1); 
	                this.curveVertex(x1, y1); 
	                this.endShape();
	                count++;
				 }
	        }
			
		 //   System.out.println("count: "+count);
		}
		// Draw Text Alpha Shape
		if (label.equals("Original")){
			fill(255,0,0);
			cX = xoff-24; 
			cY = yoff + size/2;
			al = -PApplet.PI/2;
			this.translate(cX,cY);
			this.rotate((float) (al));
			if (ratio==1)
				this.text("Triangulation",0,0);
			this.rotate((float) (-al));
			this.translate(-(cX),-(cY));
		}
		
		noStroke();
		this.fill(Color.BLACK.getRGB());
		for (int i = 0; i < d1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize, pSize);
		}
		// Print scagnostics **********************************************		
		fill(0,0,0);
		this.textAlign(PApplet.CENTER);
		DecimalFormat df = new DecimalFormat("#.##");
		this.text("Outlying="+df.format(scagnostics[0]),(float) (xoff+size/(2*ratio)),yoff+size*4+100);
		
		DecimalFormat df2 = new DecimalFormat("#.");
		this.text("OutLength="+df2.format(DT.totalMSTOutlierLengths),(float) (xoff+size/(2*ratio)),yoff+size*4+115);
		this.text("MSTLength="+df2.format(DT.totalOriginalMSTLengths),(float) (xoff+size/(2*ratio)),yoff+size*4+130);
		
		this.text("totalCount="+DT.totalCount,(float) (xoff+size/(2*ratio)),yoff+size*4+148);
		this.text("totalPeeledCount="+DT.totalPeeledCount,(float) (xoff+size/(2*ratio)),yoff+size*4+163);
		
		
		// Alpha Shape **********************************************		
		yoff = yoff+(size+gap);
	    this.strokeWeight(1);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		this.noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		
		if (alphaShape){
		  // DRAW triangle forming ALPHA SHAPE
			this.noStroke();
			this.fill(255,200,0);
			Iterator tri = DT.triangles.iterator();
		    this.curveTightness(1); 
			int count=0;
		    while (tri.hasNext()) {
				Triangle triangle = (Triangle) tri.next();
				if (triangle.onComplex) {
			        Edge e1 = triangle.anEdge;
	                Edge e2 = triangle.anEdge.nextE;
	                Edge e3 = triangle.anEdge.nextE.nextE;
	                float x1 = (float) (xoff + (e1.p1.x)*size/(1000*ratio));
	                float x2 = (float) (xoff + (e2.p1.x)*size/(1000*ratio));
	                float x3 = (float) (xoff + (e3.p1.x)*size/(1000*ratio));
	                float y1 = (float) (yoff + (e1.p1.y)*size/(1000*ratio));
	                float y2 = (float) (yoff + (e2.p1.y)*size/(1000*ratio));
	                float y3 = (float) (yoff + (e3.p1.y)*size/(1000*ratio));
	                
	                this.beginShape();
	                this.curveVertex(x1, y1);
	                this.curveVertex(x1, y1);
	                this.curveVertex(x2, y2);
	                this.curveVertex(x3, y3);
	                this.curveVertex(x1, y1); 
	                this.curveVertex(x1, y1); 
	                this.endShape();
	                count++;
				 }
	        }
		 //   System.out.println("count: "+count);
		}
		// Draw Text Alpha Shape
		if (label.equals("Original")){
			fill(240,160,0);
			 cX = xoff-24;
			 cY = yoff + size/2;
			 al = -PApplet.PI/2;
			this.translate(cX,cY);
			this.rotate((float) (al));
			if (ratio==1)
				this.text("Alpha Shape",0,0);
			this.rotate((float) (-al));
			this.translate(-(cX),-(cY));
		}
			
	      //DRAW ALPHA SHAPE
			this.stroke(240,160,0);
			Iterator it = DT.edges.iterator();
		    while (it.hasNext()) {
	            Edge e = (Edge) it.next();
	            if (e.onShape) {
	            	float x1 = (float) (xoff + (e.p1.x)*size/(1000*ratio));
	    			float x2 = (float) (xoff + (e.p2.x)*size/(1000*ratio));
	    			float y1 = (float) (yoff + (e.p1.y)*size/(1000*ratio));
	    			float y2 = (float) (yoff + (e.p2.y)*size/(1000*ratio));
	    			this.line(x1, y1, x2, y2);
	            }
		      }
		      
		    //p.fill(Color.CYAN.getRGB());
		    //p.text((int)areaAlpha[fileID]+"/"+(int)periAlpha[fileID],xoff,yoff+16);
			noStroke();
			this.fill(Color.BLACK.getRGB());
			for (int i = 0; i < d1.length; i++) {
				float xx = (float) (xoff + d1[i]*size/ratio);
				float yy = (float) (yoff + d2[i]*size/ratio);
				this.ellipse(xx, yy, pSize, pSize);
			}
				
		// DRAW HULL ********************************************************************
		yoff = yoff+(size+gap);
		this.strokeWeight(1);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		this.noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		if (convexHull){
			this.stroke(Color.BLUE.getRGB());
			this.strokeWeight(1);
			this.fill(0,0,255,70);
			Edge e = DT.hullStart;
			if (e !=null){
   			 this.beginShape();
   			 int count =0;
   			 do {	float x1 = (float) (xoff + (e.p1.x)*size/(1000*ratio));
	    			float x2 = (float) (xoff + (e.p2.x)*size/(1000*ratio));
	    			float y1 = (float) (yoff + (e.p1.y)*size/(1000*ratio));
	    			float y2 = (float) (yoff + (e.p2.y)*size/(1000*ratio));
	    			this.line(x1, y1, x2, y2);
	    			if (count==0)
	    				 this.curveVertex(x1, y1);
			        this.curveVertex(x1, y1);
		            this.curveVertex(x2, y2);
		            e = e.nextH;
		            count++;
		        } while (!e.isEqual(DT.hullStart));
			}
	           this.endShape();
			     
		} 
		noStroke();
		this.fill(Color.BLACK.getRGB());
		for (int i = 0; i < d1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize, pSize);
		}
		
		
		
		// Draw Text Hull
		if (label.equals("Original")){
			fill(0,0,255);
			cX = xoff-24;
			cY = yoff + size/2;
			al = -PApplet.PI/2;
			this.translate(cX,cY);
			this.rotate((float) (al));
			if (ratio==1)
				this.text("Convex Hull",0,0);
			this.rotate((float) (-al));
			this.translate(-(cX),-(cY));
		}	
		// MST ********************************************************************
		yoff +=(size+gap);
		this.strokeWeight(1);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		
		this.strokeWeight(2);
		if (showMST){
			this.stroke(0,200,0);
			for (int i = 0; i < MST.size(); i++) {
			    Edge e = (Edge) MST.get(i);
			    float x1 = (float) (xoff + e.p1.x*size/(1000*ratio));
				float x2 = (float) (xoff + e.p2.x*size/(1000*ratio));
				float y1 = (float) (yoff + e.p1.y*size/(1000*ratio));
				float y2 = (float) (yoff + e.p2.y*size/(1000*ratio));
				this.line(x1, y1, x2, y2);
			}
		}
		noStroke();
		this.fill(Color.BLACK.getRGB());
		for (int i = 0; i < d1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize, pSize);
		}
		
		// Leave 1 out data point ******
		this.noFill();
		this.stroke(Color.RED.getRGB());
		for (int i = 0; i < d1.length; i++) {
			if (label.contains(countries[i])){
				float xx = (float) (xoff + d1[i]*size/ratio);
				float yy = (float) (yoff + d2[i]*size/ratio);
				this.ellipse(xx, yy, pSize*2, pSize*2);
			}
		}
		
		// Outliers ******
		/*
		noStroke();
		this.fill(Color.RED.getRGB());
		for (int i = 0; i < d1.length; i++) {
			if (isOutliers[i]){
				float xx = (float) (xoff + d1[i]*size/ratio);
				float yy = (float) (yoff + d2[i]*size/ratio);
				this.ellipse(xx, yy, pSize, pSize);
			}
		}*/
		
		
		if (label.equals("Original")){
			this.textAlign(PApplet.CENTER);
			// Draw Text MST
			fill(0,200,0);
			cX = xoff-24;
			cY = yoff + size/2;
			al = -PApplet.PI/2;
			this.translate(cX,cY);
			this.rotate((float) (al));
			this.text("MST",0,0);
			this.rotate((float) (-al));
			this.translate(-(cX),-(cY));
		}
	}	
	
	public void mouseWheel(MouseWheelEvent e) {
		int delta = e.getWheelRotation();
		if (this.keyPressed){
			x -=delta;
		}
		else 
			y -=delta;
	}	
}