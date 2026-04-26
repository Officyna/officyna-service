package br.com.officyna.administrative.customer.domain.validation;

public final class DocumentUtils {

    private DocumentUtils() {}

    /**
     * Removes formatting characters (dots, dashes, slashes, spaces) and uppercases.
     * Works for both CPF and CNPJ (formatted or raw input).
     */
    public static String normalize(String document) {
        if (document == null) return null;
        return document.replaceAll("[.\\-/\\s]", "").toUpperCase();
    }

    public static boolean isCpfFormat(String normalized) {
        return normalized != null && normalized.matches("[0-9]{11}");
    }

    /**
     * CNPJ format: 12 alphanumeric chars (0-9, A-Z) + 2 numeric check digits.
     * Supports both legacy numeric CNPJs and the new alphanumeric format (IN RFB 2.229/2024,
     * effective July 6, 2026).
     */
    public static boolean isCnpjFormat(String normalized) {
        return normalized != null && normalized.matches("[0-9A-Z]{12}[0-9]{2}");
    }

    public static boolean isValidCpf(String normalized) {
        if (!isCpfFormat(normalized)) return false;
        if (normalized.chars().distinct().count() == 1) return false;

        int[] d = normalized.chars().map(c -> c - '0').toArray();

        int[] w1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += d[i] * w1[i];
        int rem = sum % 11;
        if (d[9] != (rem < 2 ? 0 : 11 - rem)) return false;

        int[] w2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 10; i++) sum += d[i] * w2[i];
        rem = sum % 11;
        return d[10] == (rem < 2 ? 0 : 11 - rem);
    }

    /**
     * Validates CNPJ using Modulo 11 with ASCII-based char values (char - 48).
     * Digits: '0'=0 ... '9'=9. Letters: 'A'=17 ... 'Z'=42.
     * Supports both numeric and alphanumeric CNPJs.
     */
    public static boolean isValidCnpj(String normalized) {
        if (!isCnpjFormat(normalized)) return false;
        if (normalized.chars().distinct().count() == 1) return false;

        int[] w1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (normalized.charAt(i) - 48) * w1[i];
        int rem = sum % 11;
        if ((normalized.charAt(12) - '0') != (rem < 2 ? 0 : 11 - rem)) return false;

        int[] w2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) sum += (normalized.charAt(i) - 48) * w2[i];
        rem = sum % 11;
        return (normalized.charAt(13) - '0') == (rem < 2 ? 0 : 11 - rem);
    }
}