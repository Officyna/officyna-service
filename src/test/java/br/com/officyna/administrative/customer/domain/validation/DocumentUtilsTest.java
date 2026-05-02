package br.com.officyna.administrative.customer.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DocumentUtilsTest {

    // ─── normalize ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("normalize deve retornar null quando o input é null")
    void normalize_ShouldReturnNull_WhenInputIsNull() {
        assertNull(DocumentUtils.normalize(null));
    }

    @Test
    @DisplayName("normalize deve remover pontos, traços e barras do CPF formatado")
    void normalize_ShouldRemoveFormattingFromCpf() {
        assertEquals("12345678909", DocumentUtils.normalize("123.456.789-09"));
    }

    @Test
    @DisplayName("normalize deve remover pontos, barras e traços do CNPJ numérico formatado")
    void normalize_ShouldRemoveFormattingFromNumericCnpj() {
        assertEquals("11222333000181", DocumentUtils.normalize("11.222.333/0001-81"));
    }

    @Test
    @DisplayName("normalize deve remover formatação e converter para maiúsculas no CNPJ alfanumérico")
    void normalize_ShouldUppercaseAndRemoveFormattingFromAlphanumericCnpj() {
        assertEquals("AB123456000110", DocumentUtils.normalize("AB.123.456/0001-10"));
        assertEquals("AB123456000110", DocumentUtils.normalize("ab.123.456/0001-10"));
    }

    @Test
    @DisplayName("normalize não deve alterar documento já normalizado")
    void normalize_ShouldReturnSameValue_WhenAlreadyNormalized() {
        assertEquals("12345678909", DocumentUtils.normalize("12345678909"));
        assertEquals("AB123456000110", DocumentUtils.normalize("AB123456000110"));
    }

    // ─── isCpfFormat ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("isCpfFormat deve retornar true para exatamente 11 dígitos")
    void isCpfFormat_ShouldReturnTrue_WhenExactly11Digits() {
        assertTrue(DocumentUtils.isCpfFormat("12345678909"));
    }

    @ParameterizedTest
    @DisplayName("isCpfFormat deve retornar false para formatos inválidos")
    @ValueSource(strings = {"1234567890", "123456789012", "1234567890A", "123.456.789-09", ""})
    void isCpfFormat_ShouldReturnFalse_WhenInvalidFormat(String input) {
        assertFalse(DocumentUtils.isCpfFormat(input));
    }

    // ─── isCnpjFormat ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("isCnpjFormat deve retornar true para CNPJ numérico (14 dígitos)")
    void isCnpjFormat_ShouldReturnTrue_ForNumericCnpj() {
        assertTrue(DocumentUtils.isCnpjFormat("11222333000181"));
    }

    @Test
    @DisplayName("isCnpjFormat deve retornar true para CNPJ alfanumérico (12 alfanum + 2 dígitos)")
    void isCnpjFormat_ShouldReturnTrue_ForAlphanumericCnpj() {
        assertTrue(DocumentUtils.isCnpjFormat("AB123456000110"));
    }

    @ParameterizedTest
    @DisplayName("isCnpjFormat deve retornar false para formatos inválidos")
    @ValueSource(strings = {
            "1122233300018",      // 13 chars
            "112223330001810",    // 15 chars
            "ab123456000110",     // letras minúsculas
            "11.222.333/0001-81", // com formatação
            "AB1234560001AB"      // letras nas posições dos dígitos verificadores (devem ser numéricos)
    })
    void isCnpjFormat_ShouldReturnFalse_WhenInvalidFormat(String input) {
        assertFalse(DocumentUtils.isCnpjFormat(input));
    }

    // ─── isValidCpf ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("isValidCpf deve retornar true para CPFs com dígitos verificadores corretos")
    void isValidCpf_ShouldReturnTrue_ForValidCpfs() {
        assertTrue(DocumentUtils.isValidCpf("12345678909"));
        assertTrue(DocumentUtils.isValidCpf("52998224725"));
    }

    @Test
    @DisplayName("isValidCpf deve retornar false para CPF com todos os dígitos iguais")
    void isValidCpf_ShouldReturnFalse_ForAllSameDigits() {
        assertFalse(DocumentUtils.isValidCpf("11111111111"));
        assertFalse(DocumentUtils.isValidCpf("00000000000"));
        assertFalse(DocumentUtils.isValidCpf("99999999999"));
    }

    @Test
    @DisplayName("isValidCpf deve retornar false quando os dígitos verificadores estão errados")
    void isValidCpf_ShouldReturnFalse_ForWrongCheckDigits() {
        assertFalse(DocumentUtils.isValidCpf("12345678900")); // d2 deveria ser 9
        assertFalse(DocumentUtils.isValidCpf("12345678901")); // d2 deveria ser 9
        assertFalse(DocumentUtils.isValidCpf("52998224700")); // dígitos alterados
    }

    @Test
    @DisplayName("isValidCpf deve retornar false para CPF com formato inválido")
    void isValidCpf_ShouldReturnFalse_ForWrongFormat() {
        assertFalse(DocumentUtils.isValidCpf("123456789"));    // 9 dígitos
        assertFalse(DocumentUtils.isValidCpf("123456789091")); // 12 dígitos
    }

    // ─── isValidCnpj ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("isValidCnpj deve retornar true para CNPJs numéricos válidos")
    void isValidCnpj_ShouldReturnTrue_ForValidNumericCnpjs() {
        assertTrue(DocumentUtils.isValidCnpj("11222333000181"));
        assertTrue(DocumentUtils.isValidCnpj("11444777000161"));
    }

    @Test
    @DisplayName("isValidCnpj deve retornar true para CNPJ alfanumérico válido (IN RFB 2.229/2024)")
    void isValidCnpj_ShouldReturnTrue_ForValidAlphanumericCnpj() {
        assertTrue(DocumentUtils.isValidCnpj("AB123456000110"));
    }

    @Test
    @DisplayName("isValidCnpj deve retornar false para CNPJ com todos os caracteres iguais")
    void isValidCnpj_ShouldReturnFalse_ForAllSameChars() {
        assertFalse(DocumentUtils.isValidCnpj("11111111111111"));
        assertFalse(DocumentUtils.isValidCnpj("00000000000000"));
    }

    @Test
    @DisplayName("isValidCnpj deve retornar false quando os dígitos verificadores estão errados")
    void isValidCnpj_ShouldReturnFalse_ForWrongCheckDigits() {
        assertFalse(DocumentUtils.isValidCnpj("11222333000100")); // dígitos verificadores alterados
        assertFalse(DocumentUtils.isValidCnpj("AB123456000199")); // alfanumérico com check errado
    }

    @Test
    @DisplayName("isValidCnpj deve retornar false para CNPJ com formato inválido")
    void isValidCnpj_ShouldReturnFalse_ForWrongFormat() {
        assertFalse(DocumentUtils.isValidCnpj("1122233300018")); // 13 chars
    }
}