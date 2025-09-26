package com.jasondt.musicservice.search;

import java.util.Locale;

public final class DamerauLevenshtein {
    private DamerauLevenshtein() {}

    public static int distance(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        int n = a.length();
        int m = b.length();
        if (n == 0) return m;
        if (m == 0) return n;
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++) {
            char ai = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                char bj = b.charAt(j - 1);
                int cost = ai == bj ? 0 : 1;
                int del = d[i - 1][j] + 1;
                int ins = d[i][j - 1] + 1;
                int sub = d[i - 1][j - 1] + cost;
                int val = Math.min(Math.min(del, ins), sub);
                if (i > 1 && j > 1 && ai == b.charAt(j - 2) && a.charAt(i - 2) == bj) {
                    val = Math.min(val, d[i - 2][j - 2] + 1);
                }
                d[i][j] = val;
            }
        }
        return d[n][m];
    }

    public static double similarity(String a, String b) {
        a = a == null ? "" : a.trim().toLowerCase(Locale.ROOT);
        b = b == null ? "" : b.trim().toLowerCase(Locale.ROOT);
        int max = Math.max(a.length(), b.length());
        if (max == 0) return 1.0;
        int dist = distance(a, b);
        return 1.0 - ((double) dist / (double) max);
    }
}
