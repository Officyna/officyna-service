package br.com.officyna.administrative.customer.domain.validation;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.domain.CustomerType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DocumentValidatorTest {

    private static Validator validator;
    private final DocumentValidator documentValidator = new DocumentValidator();

    @Mock
    private ConstraintValidatorContext context;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private CustomerRequest buildRequest(String document, CustomerType type) {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        return new CustomerRequest("João Silva", document, type, "joao@email.com", "99999-9999", "11", "+55", address);
    }

    /** Retorna apenas as violações no campo "document" geradas pelo @ValidDocument. */
    private Set<ConstraintViolation<CustomerRequest>> documentViolations(String document, CustomerType type) {
        return validator.validate(buildRequest(document, type))
                .stream()
                .filter(v -> v.getPropertyPath().toString().equals("document"))
                .collect(Collectors.toSet());
    }

    // ─── casos válidos ────────────────────────────────────────────────────────

    @Test
    @DisplayName("CPF válido sem formatação com tipo INDIVIDUAL deve passar")
    void shouldPass_WhenValidCpfNormalized() {
        assertTrue(documentViolations("12345678909", CustomerType.INDIVIDUAL).isEmpty());
    }

    @Test
    @DisplayName("CPF válido com formatação (000.000.000-00) com tipo INDIVIDUAL deve passar")
    void shouldPass_WhenValidCpfFormatted() {
        assertTrue(documentViolations("123.456.789-09", CustomerType.INDIVIDUAL).isEmpty());
    }

    @Test
    @DisplayName("CNPJ numérico válido sem formatação com tipo COMPANY deve passar")
    void shouldPass_WhenValidNumericCnpjNormalized() {
        assertTrue(documentViolations("11222333000181", CustomerType.COMPANY).isEmpty());
    }

    @Test
    @DisplayName("CNPJ numérico válido com formatação (00.000.000/0000-00) com tipo COMPANY deve passar")
    void shouldPass_WhenValidNumericCnpjFormatted() {
        assertTrue(documentViolations("11.222.333/0001-81", CustomerType.COMPANY).isEmpty());
    }

    @Test
    @DisplayName("CNPJ alfanumérico válido sem formatação com tipo COMPANY deve passar (IN RFB 2.229/2024)")
    void shouldPass_WhenValidAlphanumericCnpjNormalized() {
        assertTrue(documentViolations("AB123456000110", CustomerType.COMPANY).isEmpty());
    }

    @Test
    @DisplayName("CNPJ alfanumérico válido com formatação com tipo COMPANY deve passar")
    void shouldPass_WhenValidAlphanumericCnpjFormatted() {
        assertTrue(documentViolations("AB.123.456/0001-10", CustomerType.COMPANY).isEmpty());
    }

    // ─── CPF inválido ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("CPF com dígito verificador errado deve gerar violação com mensagem 'Invalid CPF'")
    void shouldFail_WhenCpfHasWrongCheckDigit() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("12345678900", CustomerType.INDIVIDUAL);
        assertFalse(violations.isEmpty());
        assertEquals("Invalid CPF", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("CPF com todos os dígitos iguais deve gerar violação")
    void shouldFail_WhenCpfHasAllSameDigits() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("11111111111", CustomerType.INDIVIDUAL);
        assertFalse(violations.isEmpty());
        assertEquals("Invalid CPF", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Documento com 14 chars enviado como INDIVIDUAL deve gerar violação de formato")
    void shouldFail_WhenCnpjLengthSentAsIndividual() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("11222333000181", CustomerType.INDIVIDUAL);
        assertFalse(violations.isEmpty());
        assertEquals("CPF must contain exactly 11 numeric digits", violations.iterator().next().getMessage());
    }

    // ─── CNPJ inválido ────────────────────────────────────────────────────────

    @Test
    @DisplayName("CNPJ numérico com dígito verificador errado deve gerar violação com mensagem 'Invalid CNPJ'")
    void shouldFail_WhenCnpjHasWrongCheckDigit() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("11222333000100", CustomerType.COMPANY);
        assertFalse(violations.isEmpty());
        assertEquals("Invalid CNPJ", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("CNPJ alfanumérico com dígito verificador errado deve gerar violação")
    void shouldFail_WhenAlphanumericCnpjHasWrongCheckDigit() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("AB123456000199", CustomerType.COMPANY);
        assertFalse(violations.isEmpty());
        assertEquals("Invalid CNPJ", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Documento com 11 chars enviado como COMPANY deve gerar violação de formato")
    void shouldFail_WhenCpfLengthSentAsCompany() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("12345678909", CustomerType.COMPANY);
        assertFalse(violations.isEmpty());
        assertEquals(
                "CNPJ must contain 12 alphanumeric characters followed by 2 numeric check digits",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    @DisplayName("CNPJ com todos os caracteres iguais deve gerar violação")
    void shouldFail_WhenCnpjHasAllSameChars() {
        Set<ConstraintViolation<CustomerRequest>> violations = documentViolations("11111111111111", CustomerType.COMPANY);
        assertFalse(violations.isEmpty());
        assertEquals("Invalid CNPJ", violations.iterator().next().getMessage());
    }

    // ─── casos com null (delegados a @NotBlank / @NotNull) ───────────────────

    @Test
    @DisplayName("Deve retornar true quando document é null (responsabilidade do @NotBlank)")
    void isValid_ShouldReturnTrue_WhenDocumentIsNull() {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        CustomerRequest request = new CustomerRequest("João Silva", null, CustomerType.INDIVIDUAL, "joao@email.com", "99999-9999", "11", "+55", address);
        assertTrue(documentValidator.isValid(request, context));
    }

    @Test
    @DisplayName("Deve retornar true quando type é null (responsabilidade do @NotNull)")
    void isValid_ShouldReturnTrue_WhenTypeIsNull() {
        AddressDTO address = new AddressDTO("Rua das Flores", "100", null, "Centro", "São Paulo", "SP", "01310-100", "Brasil");
        CustomerRequest request = new CustomerRequest("João Silva", "12345678909", null, "joao@email.com", "99999-9999", "11", "+55", address);
        assertTrue(documentValidator.isValid(request, context));
    }

    @Test
    @DisplayName("Deve retornar true quando request é null")
    void isValid_ShouldReturnTrue_WhenRequestIsNull() {
        assertTrue(documentValidator.isValid(null, context));
    }
}