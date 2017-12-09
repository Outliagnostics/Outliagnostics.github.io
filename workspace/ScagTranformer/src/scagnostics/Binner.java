package scagnostics;
/*
 * Binner
 *
 * Leland Wilkinson (SPSS, Inc.)
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

public class Binner {

	public Binner() {
	}

	public final BinnedData binHex(double[] x, double[] y, int nBins) {

		int n = x.length;

		// scaling constants

		double con1 = .25;
		double con2 = 1. / 3.;
		double c1 = (double) (nBins - 1);
		double c2 = c1 / Math.sqrt(3.);
		int jinc = nBins;
		int iinc = 2 * nBins;
		int nBin = (nBins + 20) * (nBins + 20);

		int[] count = new int[nBin];
		double[] xbin = new double[nBin];
		double[] ybin = new double[nBin];

		// fill bins
		for (int i = 0; i < n; i++) {
			// Tuan modified for missing input
			if ( Double.isNaN(x[i]) || Double.isNaN(y[i])){
				//System.out.println("	Binner.java:	x[i]="+x[i]+" y[i]="+y[i]);
				continue;
			}
				
			double sx = c1 * x[i];
			double sy = c2 * y[i];
			int i1 = (int) (sy + .5);
			int j1 = (int) (sx + .5);
			double dy = sy - ((double) i1);
			double dx = sx - ((double) j1);
			double dist1 = dx * dx + 3. * dy * dy;
			int m = 0;
			if (dist1 < con1) {
				m = i1 * iinc + j1;
			} else if (dist1 > con2) {
				m = ((int) sy) * iinc + ((int) sx) + jinc;
			} else {
				int i2 = (int) sy;
				int j2 = (int) sx;
				dy = sy - ((double) i2) - .5;
				dx = sx - ((double) j2) - .5;
				double dist2 = dx * dx + 3. * dy * dy;
				if (dist1 <= dist2) {
					m = i1 * iinc + j1;
				} else {
					m = i2 * iinc + j2 + jinc;
				}
			}
			
			if (m >= 0 && m < count.length) {
				count[m]++;
				xbin[m] += (x[i] - xbin[m]) / count[m];
				ybin[m] += (y[i] - ybin[m]) / count[m];
			}
		}

		nBin = deleteEmptyBins(count, xbin, ybin);
		if (nBin > 250) {
			nBins = nBins / 2;
			BinnedData b = binHex(x, y, nBins);
			return b;
		}

		int[] tcount = new int[nBin];
		double[] xtbin = new double[nBin];
		double[] ytbin = new double[nBin];

		System.arraycopy(count, 0, tcount, 0, nBin);
		System.arraycopy(xbin, 0, xtbin, 0, nBin);
		System.arraycopy(ybin, 0, ytbin, 0, nBin);

		
		BinnedData bdata = new BinnedData(xtbin, ytbin, tcount);
		bdata.binCounts = nBin;
		return bdata;

	}

	private int deleteEmptyBins(int[] count, double[] xbin, double[] ybin) {

		int k = 0;
		for (int i = 0; i < count.length; i++) {
			if (count[i] > 0) {
				count[k] = count[i];
				xbin[k] = xbin[i];
				ybin[k] = ybin[i];
				k++;
			}
		}
		return k;
	}
}

class BinnedData {
	private int x[] = null;
	private int y[] = null;
	private int counts[] = null;
	public int binCounts = 0;

	public static final double RESOLUTION = 1000;
	public static final int BINS = 100;

	BinnedData(double[] x, double[] y, int[] counts) {
		this.x = integerizeData(x);
		this.y = integerizeData(y);
		this.counts = counts;
	}

	private int[] integerizeData(double[] x) {
		int n = x.length;
		int[] xd = new int[n];
		for (int i = 0; i < n; i++) {
			xd[i] = (int) (RESOLUTION * x[i]);
		}
		return xd;
	}

	public int[] getXData() {
		return x;
	}

	public int[] getYData() {
		return y;
	}

	public int[] getCounts() {
		return counts;
	}
}