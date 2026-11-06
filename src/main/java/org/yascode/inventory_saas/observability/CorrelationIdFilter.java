package org.yascode.inventory_saas.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Pattern VALID_ID = Pattern.compile("[a-zA-Z0-9._-]{1,64}");


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId = resolveCorrelationId(request);

        try {
            MDC.put(
                    CorrelationIdConstants.MDC_KEY,
                    correlationId
            );

            response.setHeader(
                    CorrelationIdConstants.HEADER_NAME,
                    correlationId
            );

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(CorrelationIdConstants.MDC_KEY);
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {

        String incomingId = request.getHeader(
                CorrelationIdConstants.HEADER_NAME
        );

        if (incomingId != null
                && VALID_ID.matcher(incomingId).matches()) {
            return incomingId;
        }

        return UUID.randomUUID().toString();
    }
}
