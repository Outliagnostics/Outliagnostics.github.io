package main;

import static main.Main.*;
import static processing.core.PConstants.PI;

import java.awt.Color;

import processing.core.PApplet;

public class Plot{
	public int pair =-1;
	public PApplet parent;
	public int[] index;
	public int id;
	public float yP;
	public float hP;
	public int order;
	public static float[] x =  new float[nM];
	public static float[] y =  new float[MAX_PAIR];
	
	//Data for selected Points
	public float[] r3 =  new float[nM];
	public float[][] x3 =  new float[nM][numS];
	public float[][] y3 =  new float[nM][numS];
	
	//Search
	public static Color[][] colors =  new Color[3][nM];
	public static float[] xxx1 =  new float[nM];
	public static float[] www1 =  new float[nM];
	public static float[] yyy1=  new float[MAX_PAIR];
	
	
	// selected Plot index for drawing variable names
	public static int sIndex =  -1;
	public static float[] x4 =  new float[3];
	public static float[] y4 =  new float[3];
	public static String[] tx =  {"","",""};
	public static String[] ty =  {"","",""};
		
	
	public Plot(int pair_, PApplet parent_){
		parent = parent_;
		pair = pair_;
		id= pair;
		index = PlotSPLOM.pairToIndex(pair);
		bIndex[0] =-1;
		bIndex[1] =-1;
	}
	
	// Draw Scatterplot
	public void drawScatterplot(int order, float brushingIndex, boolean found1Temp, CheckBoxImposed c4) {
		int y2 = (int) (stepX2*l*order)+1;
		int h2 = plotH2;
		float maxW = stepX2*l;
		
		for (int m=0; m<nM;m++){
			float val = iX2[m].value;
			float pY =  (float) (h2*scagVals[m][sS][pair]);
			float j = yP+ hP-pY;
			colors[order][m] = getColor(j,1);
			parent.fill(colors[order][m].getRGB());
			
			// No colors for plots in Bubble plot visualization
			if (c4.s>=0){
				//float v4 = 60+scagVals[m][sS][pair]*195;
				parent.fill(200,200,200);
			}	
			float yShift = (maxW-(val-1));
			float yy = y2+yShift;
			float xRect = x[m]-val/2;
			float yRect = y2+yShift;
			float sRect = val-1;
			
			// Check brushing point
			if (xRect<=parent.mouseX && parent.mouseX<=xRect+sRect+1 && yRect<=parent.mouseY && parent.mouseY<=yRect+sRect+1) {
				bO1 = order;
				bP1 = pair;
				bM1 =m ;
				found1 = true;
				
				if (mClicked){
					if (found2 && m==bM2 && pair==bP2 && order== bO2){
						bO2 = -1;
						bP2 = -1;
						bM2 = -1 ;
						found2 = false;
					}
					else{		
						bO2 = order;
						bP2 = pair;
						bM2 =m ;
						found2 = true;
					}
				}
			}
			
			if (found2 && m==bM2 && pair==bP2 && order== bO2){
				if (found1Temp){
					parent.stroke(220,220,220);
					parent.strokeWeight(2.1f);
				}
				else {
					parent.stroke(255,255,0);
					parent.strokeWeight(2.5f);
				}
				sRect++;
				xRect--;
				yRect--;		
			}
			else if (found1 && m==bM1 && pair==bP1 && order== bO1){
				parent.stroke(255,255,0);
				parent.strokeWeight(2.5f);
				sRect++;
				xRect--;
				yRect--;
			} 
				
			parent.rect(xRect,yRect,sRect,sRect);
			xxx1[m] = xRect;
			www1[m] = val;
			parent.strokeWeight(1.f);
			parent.noStroke();
			
			float topY = y2+yShift;
			parent.fill(Color.BLACK.getRGB());
			if (val>8){
				r3[m] = 0.1f+3*val/maxW;
				float margin = 0.09f*val;
				for (int s = 0; s < numS; s++) {
					x3[m][s] = x[m]-val/2 + 0.79f*val*data[m][index[0]][s]+margin;
					y3[m][s] = topY+0.77f*val*(1-data[m][index[1]][s])+margin;
					parent.ellipse(x3[m][s], y3[m][s], r3[m], r3[m]);
					// Check brushing point
					if (dist(parent.mouseX, parent.mouseY, x3[m][s], y3[m][s]) < r3[m]) {
						bO = order;
						bD =m ;
						bS =s ;
						found = true;
					}
				}	
				
			}
			if (pY<h2-sliderY2){
				Color co = new Color(0,0,0,200);
				parent.fill(co.getRGB());
				parent.rect(x[m]-val/2-0.4f,yy-0.4f,val-1+0.8f,val-1+0.8f);
			}
			else if (pY>h2-sliderY1){
				Color co = new Color(0,0,0,250);
				parent.fill(co.getRGB());
				parent.rect(x[m]-val/2-0.4f,yy-0.4f,val-1+0.8f,val-1+0.8f);
			}	
		}
		
		
		// Variable names for scaterplots
		int v1 = index[1];
		int v2 = index[0];
		
		if (!found2)
			sIndex = (int) (brushingIndex -maxW*0.77f);
		if (sIndex>=0 && sIndex<nM){
			x4[order]= x[sIndex];
			y4[order] =y2+maxW-2;
			tx[order] =varlist[v2];
			ty[order] =varlist[v1];
			
			double al = PI/16.;
			parent.translate(x4[order],y4[order]);
			parent.rotate((float) (-al));
			parent.fill(Color.WHITE.getRGB());
			parent.text(varlist[v1], 0, 0);
			parent.rotate((float) (al));
			parent.translate(-(x4[order]),-(y4[order]));
			parent.fill(Color.WHITE.getRGB());
			parent.text(varlist[v2], x4[order], y4[order]+10);
		}
	}
	
	
	// Draw Selected Point
	public void drawSelectedPoint(int order) {
		//Draw connect lines for brushing points
		for (int d=0; d<nM;d++){
			float val = iX2[d].value;
			for (int s = 0; s < numS; s++) {
				if (found && val>8) {
					if (s==bS){
						parent.fill(Color.YELLOW.getRGB());
						parent.ellipse(x3[d][s], y3[d][s], r3[d]*2, r3[d]*2);	
					}
				}
			}	
		}
		parent.textSize(14);
		for (int d=0; d<nM;d++){
			float val = iX2[d].value;
			for (int s = 0; s < numS; s++) {
				if (found && val>8) {
					if (s==bS){
						if (d==bD && order==bO){
							parent.noStroke();
							parent.fill(new Color(0,0,0,220).getRGB());
							float wText = parent.textWidth(statelist[s]);
							parent.rect(parent.mouseX+5,parent.mouseY-6, wText+10, 18);		
							parent.fill(Color.YELLOW.getRGB());
							parent.text(statelist[s],parent.mouseX+10,parent.mouseY+6);
						}	
					}
				}
			}	
		}
	}
	
	// Draw Scatterplot SuperImposed
	public void drawScatterplotSuperimposed(int order) {
		for (int m=0; m<nM;m++){
			float pY =  (float) (hP*scagVals[m][sS][pair]);
			float val = iX2[m].value;
			float sss = val-1;
			float j = yP+ hP-pY;
			float yy = -10+yP+hP-pY*0.85f-sss/2;
			Color c = getColor(j,2);
			parent.fill(c.getRGB());
			parent.rect(x[m]-sss/2,yy,sss,sss);
			float maxW = stepX2*l;
			float topY = yy;
			parent.fill(Color.BLACK.getRGB());
			if (val>8){
				r3[m] = 0.1f+3*val/maxW;
				float margin = 0.09f*val;
				for (int s = 0; s < numS; s++) {
					x3[m][s] = x[m]-val/2 + 0.79f*val*(data[m][index[0]][s])+margin;
					y3[m][s] = topY+0.77f*val*(1-data[m][index[1]][s])+margin;
					parent.ellipse(x3[m][s], y3[m][s], r3[m], r3[m]);
					// Check brushing point
					if (dist(parent.mouseX, parent.mouseY, x3[m][s], y3[m][s]) < r3[m]) {
						bO = order;
						bD = m ;
						bS = s ;
						found = true;
					}
				}	
			}
		}
	}
	
	// Draw Selected Point for Superimposed
	public void drawSelectedPointSuperimposed(int order) {
		parent.noFill();
		parent.stroke(255,255,0);
		parent.beginShape();
		int count=0;
		float x4=0;
		float y4=0;
		for (int d=0; d<nM;d++){
			float val = iX2[d].value;
			for (int h = 0; h < numS; h++) {
				if (found && val>8) {
					if (h==bS){
						parent.curveVertex(x3[d][h],  y3[d][h]);
						if (count==0)
							parent.curveVertex(x3[d][h],  y3[d][h]);
						else{
							x4 = x3[d][h];
							y4 = y3[d][h];
						}
						count++;
					}
				}
			}	
		}
		parent.curveVertex(x4,  y4);
		parent.endShape();
		
		//Draw connect lines for brushing points
		for (int d=0; d<nM;d++){
			float val = iX2[d].value;
			for (int s = 0; s < numS; s++) {
				if (found && val>8) {
					if (s==bS){
						parent.fill(Color.YELLOW.getRGB());
						parent.ellipse(x3[d][s], y3[d][s], r3[d]*2, r3[d]*2);	
					}
				}
			}	
		}
		parent.textSize(14);
		for (int d=0; d<nM;d++){
			float val = iX2[d].value;
			for (int s = 0; s < numS; s++) {
				if (found && val>8) {
					if (s==bS){
						if (d==bD && order==bO){
							parent.noStroke();
							parent.fill(new Color(0,0,0,220).getRGB());
							float wText = parent.textWidth(statelist[s]);
							parent.rect(parent.mouseX+5,parent.mouseY-6, wText+10, 18);		
							parent.fill(Color.YELLOW.getRGB());
							parent.text(statelist[s],parent.mouseX+10,parent.mouseY+6);
						}	
					}
				}
			}	
		}
	}
		
	// Draw Selected Scagnostic in Mainscreen
		public void drawTime11(int order_) {
			float x2 = plotX2;
			plotY2 =150;
			plotH2 =90;
			yP = plotY2+(plotH2+gap2)*order;
			yyy1[order] = yP+plotH2;
			
			int w2 = plotW2;
			hP = plotH2;
			order = order_;
			int v1 = index[1];
			int v2 = index[0];
			parent.noStroke();
			parent.fill(Color.GRAY.getRGB());
			parent.rect(x2,yP,w2,hP);
			parent.fill(Color.WHITE.getRGB());
			double al = PI/8.;
			parent.translate(x2+3,yP+65);
			parent.rotate((float) (-al));
			parent.text(varlist[v1], 0, 0);
			parent.rotate((float) (al));
			parent.translate(-(x2+3),-(yP+65));
			parent.fill(Color.WHITE.getRGB());
			parent.text(varlist[v2], x2+3, yP+75);
			
			parent.beginShape();
			float yy = yP+hP;
			parent.curveVertex(x2+w2+gapS, yy);
			parent.curveVertex(x2+w2+gapS, yy);
			for (int d=0; d<nM;d++){
				float pY =  (float) (hP*scagVals[d][sS][pair]);
				parent.curveVertex(x[d],yy-pY);
			}
			parent.curveVertex(x[nM-1]+10, yy);
			parent.curveVertex(x[nM-1]+10, yy);
			
			parent.noStroke();
			parent.fill(Color.WHITE.getRGB());
			parent.endShape();
			convertColor(x2+w2+gapS,parent.width,1,order_);
		}
		
		// Draw SuperImposed in Mainscreen
		public void drawTime12(int order_) {
			float x2 = plotX2;
			plotH2 += (plotY2-30)/3;
			plotY2=30;
			yP = plotY2+(plotH2+gap2)*order;
			int w2 = plotW2;
			hP = plotH2;
			order = order_;
			int v1 = index[1];
			int v2 = index[0];
			parent.noStroke();
			parent.fill(Color.GRAY.getRGB());
			parent.rect(x2,yP,w2,hP);
			parent.fill(Color.WHITE.getRGB());
			double al = PI/8.;
			parent.translate(x2+3,yP+65);
			parent.rotate((float) (-al));
			parent.text(varlist[v1], 0, 0);
			parent.rotate((float) (al));
			parent.translate(-(x2+3),-(yP+65));
			parent.fill(Color.WHITE.getRGB());
			parent.text(varlist[v2], x2+3, yP+75);
			
			float xx1 = x2+w2+gapS;
			float yy1 = yP+hP-10;
			parent.stroke(Color.WHITE.getRGB());
			for (int d=0; d<nM;d++){
				float pY =  (float) (hP*scagVals[d][sS][pair]);
				float xx2 = x[d];
				float yy2 = -10+(yP+hP-pY*0.85f);
				
				parent.line(xx1, yy1, xx2, yy2);
				xx1=xx2;
				yy1=yy2;
			}
			convertColor(x2+w2+gapS,parent.width,1,order_);
		}
		
		
		public void drawTime21(int order_) {
			float x2 = plotX2;
			yP = plotY2+(plotH2+gap2)*3 +(plotH3+gap2)*(order_-3);
			yyy1[order] = yP+plotH3;
			int w2 = plotW2;
			hP = plotH3;
			order = order_;
			int v1 = index[1];
			int v2 = index[0];
			parent.noStroke();
			parent.fill(Color.GRAY.getRGB());
			parent.rect(x2,yP,w2,hP);
			parent.fill(new Color(240,240,240).getRGB());
			parent.text(varlist[v1], x2+3, yP+17);
			parent.text(varlist[v2], x2+3, yP+31);
			
			parent.beginShape();
			float yy = yP+hP;
			parent.curveVertex(x2+w2+gapS, yy);
			parent.curveVertex(x2+w2+gapS, yy);
			
			for (int d=0; d<nM;d++){
				float pY =  (float) ((hP-2)*scagVals[d][sS][pair]);
				parent.curveVertex(x[d],yy-pY);
			}
			parent.curveVertex(x[nM-1]+1, yy);
			parent.curveVertex(x[nM-1]+1, yy);

			//parent.stroke(new Color(60, 60, 60, 200).getRGB());
			parent.noStroke();
			parent.fill(Color.WHITE.getRGB());
			parent.endShape();
			convertColor(x2+w2+gapS,parent.width,2,order_);
		}
		
		public void drawTime22(int order_) {
			float x2 = plotX2;
			yP = plotY2+(plotH2+gap2)*3 +(plotH3+gap2)*(order_-3);
			int w2 = plotW2;
			hP = plotH3;
			order = order_;
			int v1 = index[1];
			int v2 = index[0];
			parent.noStroke();
			parent.fill(Color.GRAY.getRGB());
			parent.rect(x2,yP,w2,hP);
			parent.fill(new Color(240,240,240).getRGB());
			parent.text(varlist[v1], x2+3, yP+17);
			parent.text(varlist[v2], x2+3, yP+31);
			
			float xx1 = x2+w2+gapS;
			float yy1 = yP+hP;
			parent.stroke(Color.WHITE.getRGB());
			for (int d=0; d<nM;d++){
				float pY =  (float) (hP*scagVals[d][sS][pair]);
				float xx2 = x[d];
				float yy2 = (yP+hP-pY*0.85f);
				
				parent.line(xx1, yy1, xx2, yy2);
				xx1=xx2;
				yy1=yy2;
			}
			convertColor(x2+w2+gapS,parent.width,2, order_);
		}
		
		
		public void convertColor(float xx, float xx2,int mode, int order) {
			for (int i=(int) xx; i<=xx2;i++){
				for (int j= (int) yP; j<yP+hP+1;j++){
					int k = parent.get(i,j);
					if (k==Color.WHITE.getRGB()){
						Color c = getColor(j,mode);
						parent.set(i,j,c.getRGB());
					}
				}
			}
			// Filtered by sliders
			if (c1V<0 && order<3){
				Color co = new Color(0,0,0,250);
				parent.fill(co.getRGB());
				parent.rect(xx, yP, parent.width-xx, sliderY1);
				co = new Color(0,0,0,200);
				parent.fill(co.getRGB());
				parent.rect(xx, yP+sliderY2, parent.width-xx, hP-sliderY2);
			}
			else if (c1V<0 && order>=3){
				Color co = new Color(0,0,0,250);
				parent.fill(co.getRGB());
				parent.rect(xx, yP, parent.width-xx, sliderY1);
				co = new Color(0,0,0,200);
				parent.fill(co.getRGB());
				float rate = (float) plotH3/plotH2;
				
				parent.rect(xx, yP+sliderY2*rate, parent.width-xx, hP-sliderY2*rate);
			}
		}
			
		public Color getColor(float j, int mode) {
			if (order==0){
				if (j>=yP+hP/2){
					float mul =4f;
					if (mode==2) mul=3f;
					int al = (int) (255-(j-(yP+hP/2))*mul);
					if (al>255) al=255;
					else if (al<1) al=1;
					return new Color(al,0,0,255);
				}
				else{
					float mul =4f;
					if (mode==2) mul=3;
					int al = (int) (((yP+hP/2)-j)*mul);
					if (al>255) al=255;
					else if (al<1) al=1;
					return new Color(255,al,al,255);
				}
			}
			else if (order==1){
				if (j>=yP+hP/2){
					float mul =4f;
					if (mode==2) mul=3f;
					int al = (int) (255-(j-(yP+hP/2))*mul);
					if (al>255) al=255;
					else if (al<1) al=1;
					return new Color(0,al,0,255);
				}
				else{
					float mul =4f;
					if (mode==2) mul=3;
					int al = (int) (((yP+hP/2)-j)*mul);
					if (al>255) al=255;
					else if (al<1) al=1;
					return new Color(al,255,al,255);
				}
			}
			else if (order==2){
				if (j>=yP+hP/2){
					float mul =3.0f;
					if (mode==2) mul=3;
					int al = (int) (255-(j-(yP+hP/2))*mul);
					if (al>255) al=255;
					else if (al<1) al=1;
					return new Color(0,0,al,255);
				}
				else{
					float mul =4.f;
					if (mode==2) mul=3.f;
					int al = (int) (((yP+hP/2)-j)*mul);
					if (al>255) al=255;
					else if (al<1) al=1;
					return new Color(al,al,255,255);
				}
			}
			else{
				float mul =3f;
				if (mode==2) mul=3;
				int al = (int) (150-(j-yP)*mul);
				if (al>255) al=255;
				else if (al<1) al=1;
				return new Color(al,al,al,255);
			}
				
		}	
	
}