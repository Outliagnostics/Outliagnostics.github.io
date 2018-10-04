package scagOriginal;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class Renderers {
    public static void drawMST(Triangulation t, Graphics g, Color color, int xoff,
                               int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        g.setColor(color);
        for (int i = 0; i < t.mstEdges.size(); i++) {
            Edge e = (Edge) t.mstEdges.get(i);
            drawEdge(e, g, xoff, yoff, ht, wt, MIN_NORM, MAX_NORM);
        }
    }

    public static void drawShape(Triangulation t, Graphics g, Color color,
                                 int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        g.setColor(color);
        Iterator i = t.edges.iterator();
        while (i.hasNext()) {
            Edge e = (Edge) i.next();
            if (e.onShape) {
                drawEdge(e, g, xoff, yoff, ht, wt, MIN_NORM, MAX_NORM);
            }
        }
    }

    public static void drawComplex(List nodes, List triangles, Graphics g, Color color,
                                   int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        if (nodes.size() < 3)
            return;
        g.setColor(color);
        int[] xpoly = new int[3];
        int[] ypoly = new int[3];
        Iterator tri = triangles.iterator();
        while (tri.hasNext()) {
            Triangle triangle = (Triangle) tri.next();
            if (triangle.onComplex) {
                Edge e1 = triangle.anEdge;
                Edge e2 = triangle.anEdge.nextE;
                Edge e3 = triangle.anEdge.nextE.nextE;
                xpoly[0] = xoff + (int) (((double) (e1.p1.x - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
                xpoly[1] = xoff + (int) (((double) (e2.p1.x - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
                xpoly[2] = xoff + (int) (((double) (e3.p1.x - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
                ypoly[0] = yoff + ht - (int) (((double) (e1.p1.y - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
                ypoly[1] = yoff + ht - (int) (((double) (e2.p1.y - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
                ypoly[2] = yoff + ht - (int) (((double) (e3.p1.y - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
                g.fillPolygon(xpoly, ypoly, 3);
            }
        }
    }

    public static void drawTriangles(Triangulation t, Graphics g, Color color,
                                     int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        g.setColor(color);
        Iterator tri = t.triangles.iterator();
        while (tri.hasNext()) {
            Triangle triangle = (Triangle) tri.next();
            Edge e1 = triangle.anEdge;
            Edge e2 = triangle.anEdge.nextE;
            Edge e3 = triangle.anEdge.nextE.nextE;
            drawEdge(e1, g, xoff, yoff, ht, wt, MIN_NORM, MAX_NORM);
            drawEdge(e2, g, xoff, yoff, ht, wt, MIN_NORM, MAX_NORM);
            drawEdge(e3, g, xoff, yoff, ht, wt, MIN_NORM, MAX_NORM);
        }
    }

    public static void drawHull(Triangulation t, Graphics g, Color color,
                                int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        g.setColor(color);
        Edge e = t.hullStart;
        do {
            drawEdge(e, g, xoff, yoff, ht, wt, MIN_NORM, MAX_NORM);
            e = e.nextH;
        } while (!e.isEqual(t.hullStart));
    }

    public static void drawPoints(Triangulation t, Graphics g, Color color,
                                  int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        for (int i = 0; i < t.px.length; i++) {
            g.setColor(color);
            if (t.isOutlier[i])
                g.setColor(Color.red);
            int dx1 = xoff + (int) (((double) t.px[i] - MIN_NORM) / (MAX_NORM - MIN_NORM) * wt);
            int dy1 = yoff + ht - (int) (((double) t.py[i] - MIN_NORM) / (MAX_NORM - MIN_NORM) * ht);
//            int size = (int) (500 * (double) counts[i] / (double) totalCount);
            int size = 4;
            g.fillRect(dx1, dy1, size, size);
        }
        g.setColor(color);
        g.drawRect(2, 2, ht + yoff * 2, wt + xoff * 2);
    }

    public static void drawEdge(Edge e, Graphics g,
                                int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
        int dx1 = xoff + (int) (((double) (e.p1.x - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);// + offset;
        int dx2 = xoff + (int) (((double) (e.p2.x - MIN_NORM) / (MAX_NORM - MIN_NORM)) * wt);
        int dy1 = yoff + ht - (int) (((double) (e.p1.y - MIN_NORM) / (MAX_NORM - MIN_NORM)) * ht);
        int dy2 = yoff + ht - (int) (((double) (e.p2.y - MIN_NORM) / (MAX_NORM - MIN_NORM)) * ht);
/*
        Graphics2D g2 = (Graphics2D) g;
        Map m = new Hashtable();
        m.put(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(m);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(dx1, dy1, dx2, dy2);
*/
        g.drawLine(dx1, dy1, dx2, dy2);
    }

	//*****************************************************  
	/*
	*Function to draw histograms
	*/	
    public static void drawHistogram(Graphics g, int index,
                                  int xoff, int yoff, int ht, int wt, int MIN_NORM, int MAX_NORM) {
		int size=0, padd = 12;
		int framewt = wt;
		wt= ht;		//square histogram plot.
		Font font = new Font("Verdana", Font.BOLD, ht/14);		//chose 14 based on what fit and looked legible 
        g.setFont(font);
        for (int i = 0; i < Scagnostics.scagnosticsLabels.length; i++) {
			//---print label
            g.setColor(Color.BLACK);
	            g.drawString(Scagnostics.scagnosticsLabels[i],  xoff +padd, (int)((i+1)*(padd+ht/14)) );
			//---draw bars
			double bsize =0, rsize = 0;
			for (int j=0; j < Scagnostics.scagnostics[0].length; j++){
	            if (j==index)
					rsize = Scagnostics.scagnostics[i][j];
				else
					bsize += Scagnostics.scagnostics[i][j];
			}
			g.setColor(Color.RED);
 		    size = (int) ((double) rsize* wt);
			g.fillRect((xoff + xoff/2 + padd), (int)((i+1)*(ht/15 +padd ) -padd), size, ht/25);	
			g.setColor(Color.BLACK);
			bsize = bsize/(Scagnostics.scagnostics[0].length-1);		//average
 		    size = (int) ((double) bsize * wt);
   	        g.fillRect((xoff + xoff/2 + padd), (int)((i+1)*(ht/15 + padd)), size, ht/25);
			//---draw separating line
			if (i < (Scagnostics.scagnosticsLabels.length-1)){		//except for the last bars
				int y = (int)((i+1)*(ht/15 + padd)) + ht/25 + 5;
				g.drawLine( xoff, y, (xoff + xoff/2 + wt+2*padd), y);
			}
        }

		//---canvas border
	    g.drawRect(xoff, 2, xoff/2, ht + 2*yoff);
	    g.drawRect((xoff + xoff/2), 2, wt+2*padd, ht+ 2*yoff);
	}
	//*****************************************************  
}
