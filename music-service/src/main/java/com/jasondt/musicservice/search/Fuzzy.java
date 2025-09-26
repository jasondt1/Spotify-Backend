package com.jasondt.musicservice.search;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import java.text.Normalizer;

public final class Fuzzy {
    private static final JaroWinklerSimilarity JW = new JaroWinklerSimilarity();

    public static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return n.toLowerCase().trim();
    }

    public static double score(String query, String target) {
        String q = norm(query), t = norm(target);
        if (t.isEmpty()) return 0;
        if (t.equals(q)) return 2.0;
        if (t.startsWith(q)) return 1.5;
        if (t.contains(q)) return 1.2;
        double jw = JW.apply(q, t);
        double tokensBonus = 0;
        for (String tok : q.split("\\s+")) {
            if (!tok.isBlank() && t.contains(tok)) tokensBonus += 0.05;
        }
        return jw + tokensBonus;
    }
}
