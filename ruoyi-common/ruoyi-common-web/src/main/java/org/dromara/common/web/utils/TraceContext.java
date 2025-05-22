package org.dromara.common.web.utils;

import org.slf4j.MDC;

/**
 * @author lee
 * @description
 */
public class TraceContext {
    private static final String TRACE_ID_KEY = "traceId";

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static void clear() {
        MDC.clear();
    }
}
