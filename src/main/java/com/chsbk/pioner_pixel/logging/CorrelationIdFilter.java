package com.chsbk.pioner_pixel.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String HDR = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String cid = Optional.ofNullable(req.getHeader(HDR))
                .filter(StringUtils::hasText)
                .orElse(UUID.randomUUID().toString());

        MDC.put("correlationId", cid);
        res.setHeader(HDR, cid);

        try {
            chain.doFilter(req, res);
        }
        finally {
            MDC.clear();
        }
    }
}
