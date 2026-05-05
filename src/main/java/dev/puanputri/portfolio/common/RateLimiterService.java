package dev.puanputri.portfolio.common;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-process per-IP rate limiter using a sliding window.
 * <p>
 * Each IP is tracked by a {@link Deque} of request timestamps (epoch milliseconds).
 * When {@link #isAllowed} is called the deque is pruned to remove entries older
 * than {@code windowSeconds}, then the remaining size is compared to {@code limit}.
 * </p>
 * <p>
 * NOTE: this works correctly for a single JVM instance.  For multi-instance
 * deployments back the counter with Redis/Infinispan.
 * </p>
 */
@ApplicationScoped
public class RateLimiterService {

    private final ConcurrentHashMap<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    /**
     * Check whether the given IP is within its rate-limit budget.
     *
     * @param ip            the client IP address (used as the bucket key)
     * @param limit         maximum number of requests allowed in the window
     * @param windowSeconds length of the sliding window in seconds
     * @return {@code true} if the request is allowed, {@code false} if rate-limited
     */
    public boolean isAllowed(String ip, int limit, long windowSeconds) {
        long now = System.currentTimeMillis();
        long windowStart = now - (windowSeconds * 1000L);

        Deque<Long> timestamps = requestLog.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            // Remove entries outside the current window
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= limit) {
                return false;
            }

            timestamps.addLast(now);
            return true;
        }
    }

    /**
     * Convenience overload using default limits: 5 requests per 60 seconds.
     */
    public boolean isAllowed(String ip) {
        return isAllowed(ip, 5, 60);
    }

    /**
     * Remove all entries for an IP (useful for testing).
     */
    public void reset(String ip) {
        requestLog.remove(ip);
    }

    /**
     * Purge the entire in-memory map (useful for testing).
     */
    public void resetAll() {
        requestLog.clear();
    }
}
