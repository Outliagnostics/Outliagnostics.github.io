package scagnostics;

/*
 * Sort
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

import java.util.Comparator;
import java.util.Arrays;

public class Sort {


    public static final void doubleArraySort(double[] x, int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            fromIndex = 0;
            toIndex = x.length;
        }
        Arrays.sort(x, fromIndex, toIndex);
    }

    public static final int[] indexedDoubleArraySort(final double[] x, int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            fromIndex = 0;
            toIndex = x.length;
        }
        Integer[] sortOrder = new Integer[toIndex - fromIndex];
        for (int i = 0; i < sortOrder.length; i++) {
            sortOrder[i] = new Integer(i);
        }
        Arrays.sort(sortOrder, fromIndex, toIndex, new Comparator() {
            public int compare(Object object1, Object object2) {
                int firstIndex = ((Integer) object1).intValue();
                int secondIndex = ((Integer) object2).intValue();
                if (x[firstIndex] > x[secondIndex]) {
                    return 1;
                } else if (x[firstIndex] < x[secondIndex]) {
                    return -1;
                } else if (Double.isNaN(x[firstIndex]) && Double.isNaN(x[secondIndex])) {
                    return 0;
                } else if (Double.isNaN(x[firstIndex])) {
                    return 1;
                } else if (Double.isNaN(x[secondIndex])) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        int[] result = new int[sortOrder.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = sortOrder[i].intValue();
        }
        return result;
    }

    public static final double[] rank(double[] x, double[] wt) {

        int k, k1, k2, kind, kms, l, lind, n;
        double ak, am, freq;
        boolean insert;

        n = x.length;

        double[] ranks = new double[n];

        int[] index = indexedDoubleArraySort(x, 0, n);

        lind = index[0];
        am = x[lind];
        k1 = 0;
        k2 = 0;
        ak = 1.0;
/* kms allows for missing data */
        kms = 1;
        for (k = 1; k < n; k++) {
            kind = index[k];
            insert = true;
            if (!Double.isNaN(am)) {
                freq = 1.0;
/*
                if (wt != null)
                    freq = Math.floor(wt[kind]);
*/
                kms += (int) freq;
                if (freq > 1.0) {
                    ak += (0.5 * (freq - 1.0));
                    k1 = k;
                    k2 = k;
                } else if (x[kind] == am) {
                    k2 = k;
                    ak += 0.5;
                    if (k < n - 1)
                        insert = false;
                }
                if (insert) {
                    for (l = k1; l <= k2; l++) {
                        lind = index[l];
                        ranks[lind] = ak;
                    }
                    if (k2 != n - 1 && k == n - 1)
                        ranks[kind] = kms;
                }
            }
            if (insert) {
                k1 = k;
                k2 = k;
                ak = kms;
                am = x[kind];
            }
        }
        return ranks;
    }
}
