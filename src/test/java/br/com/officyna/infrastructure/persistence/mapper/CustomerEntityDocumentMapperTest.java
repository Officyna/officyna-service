package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.infrastructure.persistence.mongodb.model.AddressDocument;
import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerEntityDocumentMapperTest {

    private CustomerEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null Customer entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a Customer entity to CustomerDocument")
    void toDocument_validEntity_returnsDocument() {
        Address address = Address.builder()
                .street("Street A")
                .number("123")
                .complement("Apt 1")
                .neighborhood("Neighborhood B")
                .city("City C")
                .state("State D")
                .zipCode("12345-678")
                .country("Country E")
                .build();

        Customer entity = Customer.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(CustomerType.INDIVIDUAL)
                .email("test@example.com")
                .phone("987654321")
                .areaCode("11")
                .countryCode("55")
                .address(address)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getName(), document.getName());
        assertEquals(entity.getDocument(), document.getDocument());
        assertEquals(entity.getType().name(), document.getType());
        assertEquals(entity.getEmail(), document.getEmail());
        assertEquals(entity.getPhone(), document.getPhone());
        assertEquals(entity.getAreaCode(), document.getAreaCode());
        assertEquals(entity.getCountryCode(), document.getCountryCode());
        assertNotNull(document.getAddress());
        assertEquals(entity.getAddress().getStreet(), document.getAddress().getStreet());
        assertEquals(entity.getActive(), document.getActive());
        assertEquals(entity.getCreatedAt(), document.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a Customer entity with null Address to CustomerDocument")
    void toDocument_entityWithNullAddress_returnsDocumentWithNullAddress() {
        Customer entity = Customer.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(CustomerType.INDIVIDUAL)
                .email("test@example.com")
                .phone("987654321")
                .areaCode("11")
                .countryCode("55")
                .address(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertNull(document.getAddress());
    }

    @Test
    @DisplayName("Should correctly map a Customer entity with null CustomerType to CustomerDocument")
    void toDocument_entityWithNullType_returnsDocumentWithNullType() {
        Customer entity = Customer.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(null)
                .email("test@example.com")
                .phone("987654321")
                .areaCode("11")
                .countryCode("55")
                .address(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertNull(document.getType());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null CustomerDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a CustomerDocument to Customer entity")
    void toEntity_validDocument_returnsEntity() {
        AddressDocument addressDocument = AddressDocument.builder()
                .street("Street A")
                .number("123")
                .complement("Apt 1")
                .neighborhood("Neighborhood B")
                .city("City C")
                .state("State D")
                .zipCode("12345-678")
                .country("Country E")
                .build();

        CustomerDocument document = CustomerDocument.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(CustomerType.INDIVIDUAL.name())
                .email("test@example.com")
                .phone("987654321")
                .areaCode("11")
                .countryCode("55")
                .address(addressDocument)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getName(), entity.getName());
        assertEquals(document.getDocument(), entity.getDocument());
        assertEquals(CustomerType.valueOf(document.getType()), entity.getType());
        assertEquals(document.getEmail(), entity.getEmail());
        assertEquals(document.getPhone(), entity.getPhone());
        assertEquals(document.getAreaCode(), entity.getAreaCode());
        assertEquals(document.getCountryCode(), entity.getCountryCode());
        assertNotNull(entity.getAddress());
        assertEquals(document.getAddress().getStreet(), entity.getAddress().getStreet());
        assertEquals(document.getActive(), entity.getActive());
        assertEquals(document.getCreatedAt(), entity.getCreatedAt());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a CustomerDocument with null AddressDocument to Customer entity")
    void toEntity_documentWithNullAddress_returnsEntityWithNullAddress() {
        CustomerDocument document = CustomerDocument.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(CustomerType.INDIVIDUAL.name())
                .email("test@example.com")
                .phone("987654321")
                .areaCode("11")
                .countryCode("55")
                .address(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertNull(entity.getAddress());
    }

    @Test
    @DisplayName("Should correctly map a CustomerDocument with null type string to Customer entity")
    void toEntity_documentWithNullTypeString_returnsEntityWithNullType() {
        CustomerDocument document = CustomerDocument.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(null)
                .email("test@example.com")
                .phone("987654321")
                .areaCode("11")
                .countryCode("55")
                .address(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertNull(entity.getType());
    }

    // --- toAddressDocument tests ---

    @Test
    @DisplayName("Should return null when toAddressDocument receives a null Address entity")
    void toAddressDocument_nullAddress_returnsNull() {
        assertNull(mapper.toAddressDocument(null));
    }

    @Test
    @DisplayName("Should correctly map an Address entity to AddressDocument")
    void toAddressDocument_validAddress_returnsDocument() {
        Address address = Address.builder()
                .street("Street A")
                .number("123")
                .complement("Apt 1")
                .neighborhood("Neighborhood B")
                .city("City C")
                .state("State D")
                .zipCode("12345-678")
                .country("Country E")
                .build();

        AddressDocument document = mapper.toAddressDocument(address);

        assertNotNull(document);
        assertEquals(address.getStreet(), document.getStreet());
        assertEquals(address.getNumber(), document.getNumber());
        assertEquals(address.getComplement(), document.getComplement());
        assertEquals(address.getNeighborhood(), document.getNeighborhood());
        assertEquals(address.getCity(), document.getCity());
        assertEquals(address.getState(), document.getState());
        assertEquals(address.getZipCode(), document.getZipCode());
        assertEquals(address.getCountry(), document.getCountry());
    }

    // --- toAddressEntity tests ---

    @Test
    @DisplayName("Should return null when toAddressEntity receives a null AddressDocument")
    void toAddressEntity_nullAddressDocument_returnsNull() {
        assertNull(mapper.toAddressEntity(null));
    }

    @Test
    @DisplayName("Should correctly map an AddressDocument to Address entity")
    void toAddressEntity_validAddressDocument_returnsEntity() {
        AddressDocument addressDocument = AddressDocument.builder()
                .street("Street A")
                .number("123")
                .complement("Apt 1")
                .neighborhood("Neighborhood B")
                .city("City C")
                .state("State D")
                .zipCode("12345-678")
                .country("Country E")
                .build();

        Address address = mapper.toAddressEntity(addressDocument);

        assertNotNull(address);
        assertEquals(addressDocument.getStreet(), address.getStreet());
        assertEquals(addressDocument.getNumber(), address.getNumber());
        assertEquals(addressDocument.getComplement(), address.getComplement());
        assertEquals(addressDocument.getNeighborhood(), address.getNeighborhood());
        assertEquals(addressDocument.getCity(), address.getCity());
        assertEquals(addressDocument.getState(), address.getState());
        assertEquals(addressDocument.getZipCode(), address.getZipCode());
        assertEquals(addressDocument.getCountry(), address.getCountry());
    }
}