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

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.StringTokenizer;

//*****************************************************
/*
 * Slider for ranked plots to adjust the limit of the rank score
 *for the plots to be displayed
 */

class Slider implements AdjustmentListener {

	private Label t;
	private Scrollbar sb;
	private RankCanvas rc;

	public Slider(Panel p, RankCanvas rcs) {
		rc = rcs;
		int initialValue = 1;
		Label desc = new Label("Plots with rank score larger than: ");
		Font font = new Font("Verdana", Font.BOLD, p.getWidth() / 50);
		sb = new Scrollbar(Scrollbar.HORIZONTAL, initialValue, 1, 1, 11); // value, visible, min, max
		sb.setUnitIncrement(1);
		sb.addAdjustmentListener(this);
		desc.setFont(font);
		p.add(desc);
		p.add(sb);
		this.t = new Label("");
		this.t.setSize(6, 8);
		this.t.setText(String.valueOf(initialValue * 0.1));
		this.t.setFont(font);
		p.add(t);
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		double val = 0.1 * e.getValue();
		this.t.setText(String.valueOf(val));
		rc.setLimit(val);
	}
}

// *****************************************************

// *****************************************************
/*
 * Class to handle the resizing of the frame/window of ranked plots to resize the plots and reposition the slider
 */

class processStateListener implements WindowStateListener {
	Frame f;
	Panel p;
	Panel p2;
	Canvas c;

	public processStateListener(Frame frame, Panel panel, Panel panel2, Canvas canvas) {
		this.f = frame;
		this.p = panel;
		this.p2 = panel2;
		this.c = canvas;
	}

	public void windowStateChanged(WindowEvent we) {
		p.setSize(f.getWidth() - 20, f.getHeight() - 20);
		p2.setSize(f.getWidth(), 10);
		f.show();
	}
}

// *****************************************************

public class Scagnostics extends Frame {

	private static int MAX_ROWS = 100;
	private static int numVars = 0;
	private static int numRows = 0;
	private static int numScagnostics = Triangulation.numScagnostics;
	private static boolean[] isScagnosticOutlier = null;
	private static double[] dataMin, dataMax;
	public static double[][] data = null;
	public static double[][] scagnostics = null;
	public static double[][] sscagnostics = null;
	public static String[] variableLabels = null;
	public static String[] scagnosticsLabels = Triangulation.scagnosticsLabels;
	public static String[] transforms = new String[] { "none", "half", "inverse", "log", "square root", "square",
					"logit" };

	private static void getData(String[] argv) {
		argv = null;
		if (argv != null) {
			if (argv.length > 0) {
				if (getFileData(argv[0]))
					return;
			}
		} else {
			getFileData("baseball.txt");
		}
		/*
		 * try { InputStreamReader isr = new InputStreamReader(System.in); BufferedReader stdin = new BufferedReader(isr);
		 * System.out.print("Enter the name of the data file: "); fname = stdin.readLine(); } catch (IOException ie) {
		 * System.out.println("Error getting file name as input: " + ie); }
		 */
	}

	private static String buildSelect() {
		// String sql = "SELECT speed, volume, occupancy FROM t_readings limit 100";
		// String sql =
		// "SELECT avg_speed, avg_volume, avg_occupancy FROM averages where fieldDeviceID = 'IL-TESTTSC-I_55-S-6157' and type = '2' and dayofweek = '0' and hour = '8'";
		// String sql =
		// "SELECT avg_speed, avg_volume, avg_occupancy FROM averages where type = '2' and dayofweek = '0' and hour = '8'";
		// String sql = "SELECT speed, volume, occupancy FROM t_readings where fieldDeviceID = 'IL-TESTTSC-I_55-S-6157'";
		numVars = 3;
		variableLabels = new String[numVars];
		variableLabels[0] = "speed";
		variableLabels[1] = "volume";
		variableLabels[2] = "occupancy";
		String sql = "SELECT ";
		for (int i = 0; i < numVars - 1; i++) {
			sql += variableLabels[i] + ", ";
		}
		sql += variableLabels[numVars - 1] + " ";
		sql += "FROM t_readings where fieldDeviceID = 'IL-TESTTSC-I_55-S-6157'";
		System.out.println("SQL : " + sql);
		return sql;
	}

	/*
	 * ---------------------------------------------------------------------------------------- Function to read in the data from
	 * the database using the postgresql jdbc driver
	 * ----------------------------------------------------------------------------------------
	 */
	public static boolean getDbData() {
		Connection conn = null;
		// Find the Postgresql driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException ce) {
			System.out.println("driver not found: " + ce);
		}
		// Connect to the database
		try {
			String url = "jdbc:postgresql://highway/highway?user=aanand2&password=highw138&sslfactory=org.postgresql.ssl.NonValidatingFactory";
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			System.out.println("error getting connection: " + e);
		}

		// Execute the query
		numVars = 3;
		initializeMinMax();
		try {
			int i = 0;

			String sql = buildSelect();
			Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println(sql);

			st.setMaxRows(MAX_ROWS);
			ResultSet rs = st.executeQuery(sql);

			String temp = null;
			rs.beforeFirst();
			while (rs.next()) {
				try {
					temp = rs.getString(1);
					if (!rs.wasNull()) {
						data[0][i] = Double.parseDouble(temp);
					}
				} catch (Exception e) {
					System.out.println("E: " + e);
				}
				temp = rs.getString(2);
				if (!rs.wasNull()) {
					data[1][i] = Double.parseDouble(temp);
				}
				temp = rs.getString(3);
				if (!rs.wasNull()) {
					data[2][i] = Double.parseDouble(temp);
				}
				updateMinMax(data[0][i], 0);
				updateMinMax(data[1][i], i);
				updateMinMax(data[2][i], 2);
				i++;
			}

			// Numer of records selected
			rs.last();
			numRows = rs.getRow();
			System.out.println("Number of rows, cols " + numRows + " " + numVars);

			// Close statements
			rs.close();
			st.close();

			return true;
		} catch (Exception e) {
			System.out.println("error querying db: " + e);
			return false;
		}
	}

	/*
	 * ---------------------------------------------------------------------------------------- Reads in the data from a file.
	 * NOTE: Need to know the type of data in the file and the format of the file.
	 * ----------------------------------------------------------------------------------------
	 */
	private static boolean getFileData(String fname) {

		String delimiter = " "; // set column delimiter

		java.io.BufferedReader fin;
		try {
			fin = new java.io.BufferedReader(new java.io.FileReader(fname));
		} catch (java.io.FileNotFoundException fe) {
			javax.swing.JOptionPane.showMessageDialog(null, "File not found!", "Alert",
							javax.swing.JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try {
			String record = null; // To get the line of text from the file
			record = fin.readLine();
			// Get the scagnosticsLabels
			record = replaceSeparatorsWithBlanks(record);
			StringTokenizer st = new StringTokenizer(record, delimiter); // default delimiter = space
			int col = 0;
			numVars = st.countTokens();
			variableLabels = new String[numVars];

			while (st.hasMoreTokens()) {
				variableLabels[col] = st.nextToken();
				col++;
			}
			// Count the number of rows
			numRows = 0;
			record = fin.readLine();
			while (record != null) {
				record = fin.readLine();
				numRows++;
			}
			fin.close();
			System.out.println("Number of rows, cols " + numRows + " " + numVars);

			// Read in the data
			fin = new java.io.BufferedReader(new java.io.FileReader(fname));
			data = new double[numVars][numRows];
			initializeMinMax();
			record = fin.readLine(); // ignore line with scagnosticsLabels
			record = fin.readLine();
			int j = 0;
			while (record != null) {
				record = replaceSeparatorsWithBlanks(record);
				st = new StringTokenizer(record, delimiter);
				for (int i = 0; i < numVars; i++) {
					try {
						String tmp = st.nextToken();
						data[i][j] = Double.parseDouble(tmp);
						updateMinMax(data[i][j], i);
					} catch (Exception ie) {
						javax.swing.JOptionPane.showMessageDialog(null, "Error reading from the file", "Alert",
										javax.swing.JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
				record = fin.readLine();
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

	private static String replaceSeparatorsWithBlanks(String record) {
		record = replaceAll(record, ",,", "," + "0" + ",");
		record = replaceAll(record, "\t\t", "\t" + "0" + "\t");
		record = replaceAll(record, ",", " ");
		record = replaceAll(record, "\t", " ");
		return record;
	}

	private static String replaceAll(String source, String toReplace, String replacement) {
		int idx = source.lastIndexOf(toReplace);
		if (idx != -1) {
			StringBuffer sb = new StringBuffer(source);
			sb.replace(idx, idx + toReplace.length(), replacement);
			while ((idx = source.lastIndexOf(toReplace, idx - 1)) != -1) {
				sb.replace(idx, idx + toReplace.length(), replacement);
			}
			source = sb.toString();
		}

		return source;
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

	private static boolean generateData(int n, Random rand) {
		numRows = n;
		numVars = 24;
		variableLabels = new String[numVars];
		data = new double[numVars][numRows];
		initializeMinMax();
		for (int j = 0; j < numRows; j++) {
			generateUniform(0, j, rand);
			generateSpherical(2, j, rand);
			generateBivariateNormal(4, j, rand);
			generateTriangular(6, j, rand);
			generateMonotonic(8, j, rand);
			generateQuadratic(10, j, rand);
			generateClustered(12, j, rand);
			generateDoughnut(14, j, rand);
			generateOutlying(16, j, rand);
			generateSeries(18, j, n, rand);
			generateStriated(20, j, rand);
			generateSparse(22, j, rand);
		}

		return true;
	}

	private static void generateLoMedHi(Random rand) {
		int n = 250;
		int nSamp = 100;
		numVars = 6;
		numRows = n;
		variableLabels = new String[numVars];
		data = new double[numVars][numRows];
		double lo, med, hi;
		final String[] labels = { "Outlying", "Skewed", "Clumpy", "Sparse", "Striated", "Convex", "Skinny", "Stringy",
						"Monotonic" };
		for (int k = 0; k < 9; k++) {
			for (int i = 0; i < nSamp; i++) {
				initializeMinMax();
				for (int j = 0; j < n; j++) {
					if (k == 0) { // outlying
						generateUniform(0, j, rand);
						generateSpherical(2, j, rand);
						generateOutlying(4, j, rand);
					}
					if (k == 1) { // skewed
						generateNegativeSkewed(0, j, rand);
						generateUniform(2, j, rand);
						generateStriated(4, j, rand);
					}
					if (k == 2) { // clumpy
						generateUniform(0, j, rand);
						generateDoughnut(2, j, rand);
						generateClustered(4, j, rand);
					}
					if (k == 3) { // sparse
						generateSeries(0, j, n, rand);
						generateOutlying(2, j, rand);
						generateSparse(4, j, rand);
					}
					if (k == 4) { // striated
						generateUniform(0, j, rand);
						generateSeries(2, j, n, rand);
						generateStriated(4, j, rand);
					}
					if (k == 5) { // convex
						generateSeries(0, j, n, rand);
						generateQuadratic(2, j, rand);
						generateUniform(4, j, rand);
					}
					if (k == 6) { // skinny
						generateSpherical(0, j, rand);
						generateUniform(2, j, rand);
						generateSeries(4, j, n, rand);
					}
					if (k == 7) { // stringy
						generateMonotonic(0, j, rand);
						generateSeries(2, j, n, rand);
						generateCircular(4, j, rand);
					}
					if (k == 8) { // monotonic
						generateUniform(0, j, rand);
						generateTriangular(2, j, rand);
						generateMonotonic(4, j, rand);
					}
				}
				normalizeData();
				lo = computeOneScagnostic(data[0], data[1], k);
				if (k == 1) {
					while (lo > .5) {
						initializeMinMax();
						for (int j = 0; j < n; j++) {
							generateNegativeSkewed(0, j, rand);
							generateUniform(2, j, rand);
							generateStriated(4, j, rand);
						}
						normalizeData();
						lo = computeOneScagnostic(data[0], data[1], k);
					}
				}
				med = computeOneScagnostic(data[2], data[3], k);
				hi = computeOneScagnostic(data[4], data[5], k);
				System.out.println((i + 1) + " " + labels[k] + " " + lo + " " + med + " " + hi);
			}
		}
	}

	private static void generateUniform(Random rand) {
		int n = 250;
		int nSamp = 100;
		numVars = 4;
		numRows = n;
		variableLabels = new String[numVars];
		data = new double[numVars][numRows];
		final String[] labels = { "Outlying", "Skewed", "Clumpy", "Sparse", "Striated", "Convex", "Skinny", "Stringy",
						"Monotonic" };
		for (int i = 0; i < nSamp; i++) {
			initializeMinMax();
			for (int j = 0; j < n; j++) {
				generateUniform(0, j, rand);
				generateSpherical(2, j, rand);
			}
			normalizeData();
			double[] u = computeAllScagnostics(data[0], data[1]);
			double[] z = computeAllScagnostics(data[2], data[3]);
			for (int k = 0; k < 9; k++)
				System.out.println((i + 1) + " " + labels[k] + " " + u[k] + " " + z[k]);
		}
	}

	private static double computeOneScagnostic(double[] x, double[] y, int m) {
		Binner b = new Binner();
		BinnedData bdata = b.binHex(x, y, BinnedData.BINS);
		Triangulation dt = new Triangulation();
		double[] mt = dt.compute(bdata, false);
		if (mt == null)
			return Double.NaN;
		return mt[m];
	}

	private static double[] computeAllScagnostics(double[] x, double[] y) {
		Binner b = new Binner();
		BinnedData bdata = b.binHex(x, y, BinnedData.BINS);
		Triangulation dt = new Triangulation();
		double[] mt = dt.compute(bdata, false);
		return mt;
	}

	private static void generateUniform(int i, int j, Random rand) {
		setLabels("u", i);
		data[i][j] = rand.nextDouble();
		data[i + 1][j] = rand.nextDouble();
		setMinMax(j, i);
	}

	private static void generateSpherical(int i, int j, Random rand) {
		setLabels("z", i);
		data[i][j] = rand.nextGaussian();
		data[i + 1][j] = rand.nextGaussian();
		setMinMax(j, i);
	}

	private static void generateBivariateNormal(int i, int j, Random rand) {
		setLabels("r", i);
		double z1 = rand.nextGaussian();
		double z2 = rand.nextGaussian();
		data[i][j] = z1 + .5 * z2;
		data[i + 1][j] = .5 * z1 + z2;
		setMinMax(j, i);
	}

	private static void generateTriangular(int i, int j, Random rand) {
		setLabels("t", i);
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i][j] = z1;
		data[i + 1][j] = z1 + .4 * z1 * z2;
		setMinMax(j, i);
	}

	private static void generateMonotonic(int i, int j, Random rand) {
		setLabels("m", i);
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i][j] = z1;
		data[i + 1][j] = Math.pow(z1, 3) + .05 * z2;
		setMinMax(j, i);
	}

	private static void generateQuadratic(int i, int j, Random rand) {
		setLabels("q", i);
		double z1 = rand.nextDouble();
		double z2 = rand.nextGaussian();
		data[i][j] = 2 * z1 - 1;
		data[i + 1][j] = 1 - Math.pow(data[i][j], 2) + .1 * z2;
		setMinMax(j, i);
	}

	private static void generateClustered(int i, int j, Random rand) {
		setLabels("c", i);
		double p = rand.nextDouble();
		double z1 = .25 * rand.nextGaussian();
		double z2 = .25 * rand.nextGaussian();
		if (p < .33333) {
			data[i][j] = z1 - 1;
			data[i + 1][j] = z2 - 1;
		} else if (p < .66666) {
			data[i][j] = z1;
			data[i + 1][j] = z2 + 1;
		} else {
			data[i][j] = z1 + 1;
			data[i + 1][j] = z2 - 1;
		}
		setMinMax(j, i);
	}

	private static void generateDoughnut(int i, int j, Random rand) {
		setLabels("d", i);
		double z1 = rand.nextDouble();
		double z2 = .5 * rand.nextDouble();
		double theta = 6.28 * z1;
		double rho = z2;
		if (rand.nextDouble() > .3)
			rho = z2 + 1;
		data[i][j] = Math.cos(theta) * rho;
		data[i + 1][j] = Math.sin(theta) * rho;
		setMinMax(j, i);
	}

	private static void generateOutlying(int i, int j, Random rand) {
		setLabels("o", i);
		data[i][j] = rand.nextGaussian();
		data[i + 1][j] = rand.nextGaussian();
		data[i][j] = Math.pow(data[i][j], 5);
		data[i + 1][j] = Math.pow(data[i + 1][j], 5);
		setMinMax(j, i);
	}

	private static void generateSeries(int i, int j, int n, Random rand) {
		setLabels("f", i);
		data[i][j] = j;
		data[i + 1][j] = Math.sin((double) (15 * j) / (double) n) + rand.nextGaussian() / 10;
		setMinMax(j, i);
	}

	private static void generateStriated(int i, int j, Random rand) {
		setLabels("s", i);
		data[i][j] = Math.floor(4 * rand.nextDouble());
		data[i + 1][j] = rand.nextDouble();
		setMinMax(j, i);
	}

	private static void generateSparse(int i, int j, Random rand) {
		setLabels("s", i);
		if (rand.nextDouble() < .02) {
			data[i][j] = 3 * rand.nextDouble();
			data[i + 1][j] = 3 * rand.nextDouble();
		} else {
			data[i][j] = Math.floor(3 * rand.nextDouble());
			data[i + 1][j] = Math.floor(3 * rand.nextDouble());
		}
		setMinMax(j, i);
	}

	private static void generateNegativeSkewed(int i, int j, Random rand) {
		setLabels("s", i);
		data[i][j] = Math.floor(3 * rand.nextDouble()) + .01 * rand.nextDouble();
		data[i + 1][j] = Math.floor(3 * rand.nextDouble() + .01 * rand.nextDouble());
		setMinMax(j, i);
	}

	private static void generateCircular(int i, int j, Random rand) {
		setLabels("s", i);
		double z1 = rand.nextDouble();
		double z2 = rand.nextDouble();
		double theta = 6.28 * z1;
		double rho = 1;
		if (z2 > .5)
			rho = .97;
		data[i][j] = Math.cos(theta) * rho;
		data[i + 1][j] = Math.sin(theta) * rho;
		setMinMax(j, i);
	}

	private static void setMinMax(int j, int loc) {
		updateMinMax(data[loc][j], loc);
		updateMinMax(data[loc + 1][j], loc + 1);
	}

	private static void setLabels(String s, int loc) {
		variableLabels[loc] = s + (1) + "";
		variableLabels[loc + 1] = s + (2) + "";
	}

	private static BufferedWriter openOutputFileWithHeaderRecord(boolean isRandom) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("abalone_measures.txt"));
			if (isRandom)
				out.write(" sample n distribution ");
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

	private static boolean writeRandomData() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("data.txt"));
			for (int i = 0; i < variableLabels.length; i++) {
				out.write(variableLabels[i] + " ");
			}
			out.newLine();
			for (int j = 0; j < data[0].length; j++) {
				for (int i = 0; i < data.length; i++) {
					out.write(data[i][j] + " ");
				}
				out.newLine();
			}
			out.close();
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
		long startTime;
		startTime = System.currentTimeMillis();
		int maximizedScagnostic = Triangulation.CLUMPY;
		int k = 0;
		for (int i = 1; i < numVars; i++) {
			for (int j = 0; j < i; j++) {
				for (int ti = 0; ti < transforms.length; ti++) {
					double[] di = new double[data[i].length];
					for (int mi = 0; mi < di.length; mi++)
						di[mi] = transform(data[i][mi], transforms[ti]);
					for (int tj = 0; tj < transforms.length; tj++) {
						double[] dj = new double[data[j].length];
						for (int mj = 0; mj < dj.length; mj++)
							dj[mj] = transform(data[j][mj], transforms[ti]);
						Binner b = new Binner();
						BinnedData bdata = b.binHex(dj, di, BinnedData.BINS);
						Triangulation dt = new Triangulation();
						double[] mt = dt.compute(bdata, false);
						if (mt == null)
							continue;
						for (int m = 0; m < numScagnostics; m++) {
							if (Double.isNaN(mt[m]))
								mt[m] = 0;
							if ((ti == 0 && tj == 0) || mt[maximizedScagnostic] > scagnostics[maximizedScagnostic][k]) {
								scagnostics[m][k] = mt[m];
								if (m == 0)
									System.out.println(i + " " + j + " " + transforms[ti] + " " + transforms[tj] + " "
													+ mt[maximizedScagnostic]);
							}
						}
					}
				}
				k++;
			}
		}
		float triangulationTime = (System.currentTimeMillis() - startTime) / 1000F;
		System.out.println("triangulation time: " + triangulationTime);
	}

	private static double transform(double x, String transform) {
		if (transform.equals("none"))
			return x;
		else if (transform.equals("half"))
			x = .5 * x;
		else if (transform.equals("inverse"))
			x = 1 / x;
		else if (transform.equals("log"))
			x = Math.log(x);
		else if (transform.equals("square root"))
			x = Math.sqrt(x);
		else if (transform.equals("square"))
			x = x * x;
		else if (transform.equals("logit"))
			x = Math.log(x / (1 - x));

		if (Double.isInfinite(x))
			return Double.NaN;
		else
			return x;
	}

	private static void computeScagnosticsOnRandomData() {
		int numCells = numVars / 2;
		scagnostics = new double[numScagnostics][numCells];
		int k = 0;
		for (int i = 1; i < numVars; i += 2) {
			int j = i - 1;
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

	private static void computeScagnosticsOutliers() {
		isScagnosticOutlier = computeMSTOutliers(scagnostics);
	}

	private static final boolean[] computeMSTOutliers(double[][] pts) {

		// Prim's algorithm on simple integer arrays

		int nVar = pts.length; // 9 Scagnostic dimensions
		int nPts = pts[0].length; // p*(p-1)/2 points representing scatterplots
		if (nPts < 2)
			return null;
		int[][] edges = new int[nPts - 1][2];
		int[] list = new int[nPts];
		int[] degrees = new int[nPts];
		double[] cost = new double[nPts];
		double[] lengths = new double[nPts - 1];

		list[0] = 0;
		cost[0] = Double.POSITIVE_INFINITY;
		int cheapest = 0;

		for (int i = 1; i < nPts; i++) {
			for (int j = 0; j < nVar; j++) {
				double d = pts[j][i] - pts[j][0];
				cost[i] += d * d;
			}
			if (cost[i] < cost[cheapest])
				cheapest = i;
		}
		for (int j = 1; j < nPts; j++) {
			int end = list[cheapest];
			int jp = j - 1;
			edges[jp][0] = cheapest;
			edges[jp][1] = end;
			lengths[jp] = cost[cheapest];
			degrees[cheapest]++;
			degrees[end]++;
			cost[cheapest] = Double.POSITIVE_INFINITY;
			end = cheapest;

			for (int i = 1; i < nPts; i++) {
				if (cost[i] != Double.POSITIVE_INFINITY) {
					double dist = 0.;
					for (int k = 0; k < nVar; k++) {
						double d = (pts[k][i] - pts[k][end]);
						dist += d * d;
					}
					if (dist < cost[i]) {
						list[i] = end;
						cost[i] = dist;
					}
					if (cost[i] < cost[cheapest])
						cheapest = i;
				}
			}
		}

		// find cutoff value for identifying extremely long edges

		int[] index = Sort.indexedDoubleArraySort(lengths, 0, 0);
		int n50 = lengths.length / 2;
		int n25 = n50 / 2;
		int n75 = n50 + n50 / 2;
		double cutoff = lengths[index[n75]] + 1.5 * (lengths[index[n75]] - lengths[index[n25]]);

		boolean[] outliers = new boolean[nPts];
		for (int i = 0; i < nPts; i++)
			outliers[i] = true;
		for (int i = 0; i < nPts - 1; i++) {
			if (lengths[i] < cutoff) {
				for (int k = 0; k < 2; k++) {
					int node = edges[i][k];
					outliers[node] = false;
				}
			}
		}
		return outliers;
	}

	private static void drawRawDataSplom() {
		Frame f = new ChildFrame("Data SPLOM", 700, 700);
		SplomCanvas gc = new SplomCanvas(f, data, variableLabels, null, null, false);
		f.add(gc);
		gc.repaint();
		f.show();
	}

	private static void drawSortedDataSplom() {
		int[] sortOrder = rankVariables(scagnostics);
		Frame f = new ChildFrame("Sorted Data SPLOM", 700, 700);
		SplomCanvas gc = new SplomCanvas(f, data, variableLabels, sortOrder, isScagnosticOutlier, false);
		f.add(gc);
		gc.repaint();
		f.show();
	}

	private static void drawRankPlots() {
		int[][] sortOrder = rankPlots(scagnostics);
		Frame f = new ChildFrame("Ranked Plots", 700, 700);
		// *****************************************************
		// ---add a border layout to position the slider
		f.setLayout(new BorderLayout());
		Panel rp = new Panel();
		rp.setSize(f.getWidth() - 20, f.getHeight() - 20);
		f.add(rp, BorderLayout.CENTER);
		// ---add the rank canvas
		RankCanvas gc = new RankCanvas(rp, data, scagnosticsLabels, sortOrder, scagnostics);
		rp.add(gc);
		gc.repaint();
		rp.show();
		f.add(rp);
		// ---add the slider to vary the limit
		Panel rp2 = new Panel();
		rp2.setSize(f.getWidth(), 10);
		Slider scroll = new Slider(rp2, gc);
		f.add(rp2, BorderLayout.SOUTH);
		rp2.show();

		// ---add state listener so if frame is maximized panel size is changed appropriately
		f.addWindowStateListener(new processStateListener(f, rp, rp2, gc));
		// *****************************************************
		f.show();
	}

	private static void drawScagnosticsSplom() {

		Frame f = new GraphFrame("Scagnostics SPLOM", 700);
		SplomCanvas gc = new SplomCanvas(f, scagnostics, scagnosticsLabels, null, null, true);
		f.add(gc);
		gc.repaint();
		f.show();
	}

	private static int[][] rankPlots(double[][] x) {
		int nCols = x.length;
		int[][] sortOrder = new int[nCols][];
		for (int j = 0; j < nCols; j++) {
			sortOrder[j] = Sort.indexedDoubleArraySort(x[j], 0, 0);
		}
		return sortOrder;
	}

	private static int[] rankVariables(double[][] x) {
		int nRows = x[0].length;
		int nCols = x.length;
		double[][] s = computeSampleCovarianceMatrix(x);
		double[][] eigenvectors = new double[nCols][nCols];
		double[] eigenvalues = new double[nCols];
		Eigen.eigenSymmetric(s, eigenvectors, eigenvalues);
		double[] coord = new double[nRows];
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				coord[i] += x[j][i] * eigenvectors[j][0];
			}
		}
		double[] c = new double[numVars];
		int k = 0;
		for (int i = 1; i < numVars; i++) {
			for (int j = 0; j < i; j++) {
				c[i] += coord[k];
				c[j] += coord[k];
				k++;
			}
		}
		return Sort.indexedDoubleArraySort(c, 0, 0);
	}

	private static double[][] computeSampleCovarianceMatrix(double[][] x) {
		int nCols = x.length;
		int nRows = x[0].length;
		double[][] cov = new double[nCols][nCols];
		double[] xbar = new double[nCols];

		for (int j = 0; j < nCols; j++) {
			for (int i = 0; i < nRows; i++) {
				xbar[j] += x[j][i];
			}
			xbar[j] /= nRows;
		}

		for (int j = 0; j < nCols; j++) {
			for (int k = 0; k < nCols; k++) {
				for (int i = 0; i < nRows; i++) {
					cov[j][k] += (x[j][i] - xbar[j]) * (x[k][i] - xbar[k]);
				}
				cov[j][k] /= nRows;
			}
		}
		return cov;
	}

	private static void computeOnFileData(String[] argv) {
		getData(argv);
		normalizeData();
		drawRawDataSplom();
		computeScagnosticsOnFileData();
		computeScagnosticsOutliers();
		drawSortedDataSplom();
		drawScagnosticsSplom();
		drawRankPlots();
		BufferedWriter outFile = openOutputFileWithHeaderRecord(false);
		writeMeasures(outFile, 0, 0, false);
		try {
			outFile.close();
		} catch (IOException e) {
			System.exit(1);
		}
	}

	private static void computeOnRandomData() {
		boolean plot = true;
		int numSamples = 100;
		BufferedWriter outFile;
		if (plot)
			outFile = null;
		else
			outFile = openOutputFileWithHeaderRecord(true);
		Random rand = new Random(13579);
		for (int numPoints = 1000; numPoints <= 1000; numPoints += 100) {
			for (int sample = 0; sample < numSamples; sample++) {
				generateData(numPoints, rand);
				if (plot) {
					plotRandomData();
					return;
				} else {
					normalizeData();
					computeScagnosticsOnRandomData();
					writeMeasures(outFile, sample, numPoints, true);
				}
			}
		}
		try {
			outFile.close();
		} catch (IOException e) {
			System.exit(1);
		}
	}

	private static void plotRandomData() {
		writeRandomData();
		String[] s = new String[1];
		s[0] = "data.txt";
		getData(s);
		normalizeData();
		drawRawDataSplom();
	}

	public static void main(String argv[]) {
		computeOnFileData(argv);
		//		computeOnRandomData();
		// generateLoMedHi(new Random(13579));
		// generateUniform(new Random(13579));
	}
}
