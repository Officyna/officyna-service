package br.com.officyna.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private static final String HEADER_NAME = "X-Correlation-ID";

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    @DisplayName("Deve reaproveitar o correlation id recebido no header, propagar no MDC e devolver no response")
    void doFilterInternal_shouldReuseExistingCorrelationId() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HEADER_NAME)).thenReturn("existing-correlation-id");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/serviceorder");

        doAnswer(invocation -> {
            assertEquals("existing-correlation-id", MDC.get("correlationId"));
            assertEquals("GET", MDC.get("httpMethod"));
            assertEquals("/api/serviceorder", MDC.get("requestUri"));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(HEADER_NAME, "existing-correlation-id");
        verify(chain).doFilter(request, response);
        assertNull(MDC.get("correlationId"));
        assertNull(MDC.get("httpMethod"));
        assertNull(MDC.get("requestUri"));
    }

    @Test
    @DisplayName("Deve gerar um novo correlation id quando o header estiver ausente ou em branco")
    void doFilterInternal_shouldGenerateCorrelationId_whenHeaderBlank() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HEADER_NAME)).thenReturn("  ");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(eq(HEADER_NAME), argThat(id -> id != null && !id.isBlank()));
        verify(chain).doFilter(request, response);
        assertNull(MDC.get("correlationId"));
    }

    @Test
    @DisplayName("Deve limpar o MDC mesmo quando a cadeia de filtros lança exceção")
    void doFilterInternal_shouldClearMdc_whenChainThrows() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(HEADER_NAME)).thenReturn("id-1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/serviceorder");
        doThrow(new IOException("boom")).when(chain).doFilter(request, response);

        assertThrows(IOException.class, () -> filter.doFilterInternal(request, response, chain));

        assertNull(MDC.get("correlationId"));
        assertNull(MDC.get("httpMethod"));
        assertNull(MDC.get("requestUri"));
    }
}
