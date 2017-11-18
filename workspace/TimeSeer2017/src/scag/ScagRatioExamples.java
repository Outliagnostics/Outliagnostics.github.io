package scag;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;

public class ScagRatioExamples extends PApplet {
	public static int count = 0;
	public static double[] scagnostics = new double[9];
	//public static double[] data1 = {0,0.91,0.82,0.81,0.71,0.65,0.7,0.2,0.51,0.7,
	//	0.74,0.21,0.86,0.65,0.33,0.61,0.54,0.4,0.1,0.6,0.3,0.6,0.9,0.6,0.5,0.4,0.7,0.92};
	//public static double[] data2 = {1,0.01,0.32,0.13,0.22,0.87,0.5,0.9,0.72,0.5,
	//	0.72,0.72,0.57,0.41,0.87,0.12,0.39,0.9,0.8,0.9,1.0,0.6,0.2,0.2,0.0,0.1,0.3,0.41};
	public static double[] data1;
	public static double[] data2;
	public static double[] data3 ;
	public static double[] data4;
	public static double[][] data;
		
	@SuppressWarnings("rawtypes")
	public static List MST1;
	public static List MST2;
	public static double areaAlpha1 = 0;
	public static double areaAlpha2 = 0;
	public static double periAlpha1 = 0;
	public static double periAlpha2 = 0;
	public static Triangulation DT1;
	public static Triangulation DT2;
	public PFont metaBold = loadFont("Arial-BoldMT-18.vlw");
	
	float x = 10;
	float y = 40;
	double ratio = 0.5f;
	
	public static void main(String args[]){
	  PApplet.main(new String[] { ScagRatioExamples.class.getName() });
    }
	
	public static void getData(){
		int numSamp =  90;
		data = new double[2][numSamp];
		Random rand = new Random(1122);
		double maxX =  Double.NEGATIVE_INFINITY;
		double maxY =  Double.NEGATIVE_INFINITY;
		double minX =  Double.POSITIVE_INFINITY;
		double minY =  Double.POSITIVE_INFINITY;
		
		for (int i=0; i<numSamp;i++){
			double z1 = .49 * rand.nextGaussian();
			double z2 = .48 * rand.nextGaussian();
			double p = rand.nextDouble();
			if (p < .33333) {
				data[0][i] = z1 - 2.4;
				data[1][i] = z2 - 0.5;
			} else if (p < .66666) {
				data[0][i] = z1;
				data[1][i] = z2;
			} else {
				data[0][i] = z1 + 2.8;
				data[1][i] = z2/1.2 - 0.2;
			}
			if (data[0][i]>maxX)
				maxX=data[0][i];
			if (data[0][i]<minX)
				minX=data[0][i];
			if (data[1][i]>maxY)
				maxY=data[1][i];
			if (data[1][i]<minY)
				minY=data[1][i];
		}	
		System.out.println("	"+minX+" "+maxX);
		System.out.println("	"+minY+" "+maxY);
		
		for (int i=0; i<numSamp;i++){
			data[0][i] = (data[0][i]-minX)/(maxX-minX);
		}
		for (int i=0; i<numSamp;i++){
			data[1][i] = (data[1][i]-minY)/(maxY-minY);
		}
		
		data1 = data[0];
		data2 = data[1];
		data3 = data[0];
		data4 = new double[numSamp];
		for (int i=0; i<numSamp;i++){
			data4[i] =data[1][i]/2;
		}
	}
	
	public void setup() {
		size(1280, 750);
		stroke(255);
		frameRate(12);
		curveTightness(1.f); 
		smooth();
		getData();
		computeScagnosticsOnFileData(data1, data2,0,1);
		computeScagnosticsOnFileData(data3, data4,1,ratio);
		//System.out.println("data1"+"data2");
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent evt) {
				mouseWheel(evt);
			}
		});
	}
	
	private static void computeScagnosticsOnFileData(double[] d1, double[] d2, int decision, double ratio) {
       Binner b = new Binner();
		BinnedData bdata = b.binHex(d1, d2, BinnedData.BINS);
		Triangulation dt = new Triangulation();
		double[] mt = dt.compute(bdata, false);
		 
        for (int m = 0; m < 9; m++) {
            if (Double.isNaN(mt[m]))
                mt[m] = 0;
            scagnostics[m] = mt[m];
        }
        if (decision==0){
        	MST1 = dt.mstEdges;
        	areaAlpha1 = dt.alphaArea;
        	periAlpha1 = dt.alphaPerimeter;
        	DT1 = dt;
        }
        else  if (decision==1){
        	MST2 = dt.mstEdges;
        	areaAlpha2 = dt.alphaArea;
        	periAlpha2 = dt.alphaPerimeter;
        	DT2 = dt;
        }
     }
	
	
	
	
	public void draw() {
		this.background(255,255,255);
		
		boolean showMST =true;
		boolean alphaShape =true;
		boolean convexHull =true;
		float xoff = x+100;
		float yoff = y;
		
		drawDT(showMST,alphaShape,convexHull,
				data1, data2, MST1, areaAlpha1,periAlpha1, DT1,
				xoff,yoff,1);
		xoff = x +430;
		drawDT(showMST,alphaShape,convexHull,
				data3, data4, MST2, areaAlpha2,periAlpha2, DT2,
				xoff,yoff,ratio);
	}

	public void drawDT(boolean showMST, boolean alphaShape, boolean convexHull,
			double[] d1, double[] d2, List MST, double area, double peri, Triangulation DT,
			float xoff, float yoff_, double ratio_) {
		this.noStroke();
		float size = 275;
		float margin = 15;
		float gap = 60;
		double ratio = PApplet.sqrt((float) ratio_);
		float pSize1 = 9;
		float pSize = 5;
		float sat =220;
		
		
		float yoff =y+ yoff_;
		if (ratio<1){
			yoff += 50;
		}
		
// Draw Input data **********************************************
		this.textFont(metaBold, 35);
		fill(0,0,0);
		this.textAlign(PApplet.CENTER);
		String text = "Aspect ratio of 1:1";
		if (ratio<1)
			text = "Aspect ratio of 2:1";
		this.text(text,(float) (xoff+size/(2*ratio)),y+10);
		
		
		
		this.strokeWeight(2);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		this.noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		noStroke();
		this.fill(Color.BLACK.getRGB());
		for (int i = 0; i < data1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize1, pSize1);
		}	
		// Draw Text Alpha Shape
		fill(0,0,0);
		float cX = xoff-24;
		float cY = yoff + size/2;
		float al = -PApplet.PI/2;
		this.translate(cX,cY);
		this.rotate((float) (al));
		if (ratio==1)
			this.text("Input Data",0,0);
		this.rotate((float) (-al));
		this.translate(-(cX),-(cY));
	
		
		
// Trianglation **********************************************		
		yoff = yoff+(size+gap);
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
		
		/*
		noStroke();
		this.fill(Color.BLACK.getRGB());
		for (int i = 0; i < data1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, 9, 9);
		}*/
					
		
// Alpha Shape **********************************************		
		yoff = yoff+(size+gap);
	    this.strokeWeight(2);
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
			for (int i = 0; i < data1.length; i++) {
				float xx = (float) (xoff + d1[i]*size/ratio);
				float yy = (float) (yoff + d2[i]*size/ratio);
				this.ellipse(xx, yy, pSize, pSize);
			}
				
	   
		
// DRAW HULL ********************************************************************
		yoff = yoff+(size+gap);
		this.strokeWeight(2);
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
		for (int i = 0; i < data1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize, pSize);
		}
		
		
		
		// Draw Text Hull
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
		
		
		
		
// MST ********************************************************************
		yoff +=(size+gap);
		this.strokeWeight(2);
		fill(sat,sat,sat);
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		noFill();
		stroke(0,0,0);
		rect(xoff-margin,yoff-margin, (float) ((size/ratio+margin*2)), (float) ((size*ratio+margin*2)));
		
		this.strokeWeight(4);
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
		for (int i = 0; i < data1.length; i++) {
			float xx = (float) (xoff + d1[i]*size/ratio);
			float yy = (float) (yoff + d2[i]*size/ratio);
			this.ellipse(xx, yy, pSize, pSize);
		}
		
		this.textAlign(PApplet.CENTER);
		// Draw Text MST
		fill(0,200,0);
		cX = xoff-24;
		cY = yoff + size/2;
		al = -PApplet.PI/2;
		this.translate(cX,cY);
		this.rotate((float) (al));
		if (ratio==1)
			this.text("MST",0,0);
		this.rotate((float) (-al));
		this.translate(-(cX),-(cY));
				
		
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
