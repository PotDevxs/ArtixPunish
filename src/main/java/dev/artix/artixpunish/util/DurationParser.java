package dev.artix.artixpunish.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte texto tipo {@code 7d}, {@code 12h}, {@code 30m}, {@code 2w} em milissegundos.
 */
public final class DurationParser {

    private static final Pattern TOKEN = Pattern.compile("(\\d+)\\s*([dhwms])", Pattern.CASE_INSENSITIVE);

    private DurationParser() {
    }

    /**
     * @return milissegundos ou -1 se inválido
     */
    public static long parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return -1;
        }
        String s = input.trim().toLowerCase();
        long total = 0;
        Matcher m = TOKEN.matcher(s);
        int matches = 0;
        while (m.find()) {
            matches++;
            long n = Long.parseLong(m.group(1));
            char unit = m.group(2).charAt(0);
            switch (unit) {
                case 'w':
                    total += n * 7L * 24L * 60L * 60L * 1000L;
                    break;
                case 'd':
                    total += n * 24L * 60L * 60L * 1000L;
                    break;
                case 'h':
                    total += n * 60L * 60L * 1000L;
                    break;
                case 'm':
                    total += n * 60L * 1000L;
                    break;
                case 's':
                    total += n * 1000L;
                    break;
                default:
                    return -1;
            }
        }
        if (matches == 0 || total <= 0) {
            return -1;
        }
        // Garante que a string inteira foi consumida (apenas tokens válidos)
        String compact = s.replaceAll("\\s+", "");
        int expectedLen = 0;
        Matcher m2 = TOKEN.matcher(compact);
        while (m2.find()) {
            expectedLen += m2.group(0).length();
        }
        if (expectedLen != compact.length()) {
            return -1;
        }
        return total;
    }

    public static String formatMillis(long millis) {
        if (millis < 0) {
            return "permanente";
        }
        long s = millis / 1000;
        long m = s / 60;
        long h = m / 60;
        long d = h / 24;
        long w = d / 7;
        if (w > 0) {
            return w + " semana(s)";
        }
        if (d > 0) {
            return d + " dia(s)";
        }
        if (h > 0) {
            return h + " hora(s)";
        }
        if (m > 0) {
            return m + " minuto(s)";
        }
        return Math.max(1, s) + " segundo(s)";
    }
}
