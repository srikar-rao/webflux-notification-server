package com.dev.org.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filter to automatically inject the current Trace ID into all HTTP response headers.
 */
@Component
@RequiredArgsConstructor
public class TraceIdResponseFilter implements WebFilter {

    private final ObjectProvider<Tracer> tracerProvider;

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    // Uncomment the next line if you also specifically want to support legacy B3
    // private static final String B3_TRACE_ID_HEADER = "X-B3-TraceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Tracer tracer = tracerProvider.getIfAvailable();
        if (tracer != null) {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                String traceId = currentSpan.context().traceId();

                // Modern custom trace header
                exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, traceId);

                // Legacy B3 header
                // exchange.getResponse().getHeaders().set(B3_TRACE_ID_HEADER, traceId);
            }
        }

        return chain.filter(exchange);
    }
}
