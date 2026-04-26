package br.com.officyna.administrative.customer.domain.validation;

import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.domain.CustomerType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentValidator implements ConstraintValidator<ValidDocument, CustomerRequest> {

    @Override
    public boolean isValid(CustomerRequest request, ConstraintValidatorContext context) {
        if (request == null || request.document() == null || request.type() == null) {
            return true; // @NotBlank and @NotNull handle null/blank cases
        }

        String normalized = DocumentUtils.normalize(request.document());
        String errorMessage;

        if (request.type() == CustomerType.INDIVIDUAL) {
            if (!DocumentUtils.isCpfFormat(normalized)) {
                errorMessage = "CPF must contain exactly 11 numeric digits";
            } else if (!DocumentUtils.isValidCpf(normalized)) {
                errorMessage = "Invalid CPF";
            } else {
                return true;
            }
        } else {
            if (!DocumentUtils.isCnpjFormat(normalized)) {
                errorMessage = "CNPJ must contain 12 alphanumeric characters followed by 2 numeric check digits";
            } else if (!DocumentUtils.isValidCnpj(normalized)) {
                errorMessage = "Invalid CNPJ";
            } else {
                return true;
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addPropertyNode("document")
                .addConstraintViolation();
        return false;
    }
}