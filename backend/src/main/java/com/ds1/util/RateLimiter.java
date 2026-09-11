package com.ds1.util;

import com.ds1.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 极简的进程内滑动窗口限流器，用于给登录/注册这类「可以被无限次尝试」的入口兜底。
 *
 * <p>刻意不引第三方依赖（bucket4j 之类）：这个项目的规模用不着，一个带时间戳的队列就够。
 *
 * <p>已知局限，按需再升级：
 * <ul>
 *   <li>状态在单个 JVM 内存里 —— 多实例部署时每个实例各算各的，届时需要换成 Redis。</li>
 *   <li>「检查」与「记录」是两步，并发下可能略微超发；作为暴力破解的减速带足够。</li>
 *   <li>重启即清零，这是可接受的取舍（不会误伤用户）。</li>
 * </ul>
 */
@Component
public class RateLimiter {

    /** 键数量超过这个值就顺手清理一次，避免长期运行后 Map 无界增长。 */
    private static final int SWEEP_THRESHOLD = 10_000;

    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    /**
     * 窗口内记录数达到上限时抛出 429，否则放行（本身不记录）。
     *
     * @param key          限流维度，如 {@code login:<ip>:<username>}
     * @param maxRequests  窗口内允许的最大次数
     * @param windowMillis 窗口长度（毫秒）
     * @param message      触发限流时返回给前端的提示
     */
    public void checkAllowed(String key, int maxRequests, long windowMillis, String message) {
        Deque<Long> timestamps = hits.get(key);
        if (timestamps == null) {
            return;
        }

        long now = System.currentTimeMillis();
        synchronized (timestamps) {
            evictExpired(timestamps, now, windowMillis);
            if (timestamps.size() >= maxRequests) {
                throw new BusinessException(HttpStatus.TOO_MANY_REQUESTS.value(), message);
            }
        }
    }

    /**
     * 记录一次命中，参与后续的窗口计数。
     */
    public void record(String key, long windowMillis) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = hits.computeIfAbsent(key, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            evictExpired(timestamps, now, windowMillis);
            timestamps.addLast(now);
        }

        if (hits.size() > SWEEP_THRESHOLD) {
            sweep(now, windowMillis);
        }
    }

    /**
     * 清空某个 key 的计数（例如登录成功后，不该再背着之前的失败次数）。
     */
    public void reset(String key) {
        hits.remove(key);
    }

    private void evictExpired(Deque<Long> timestamps, long now, long windowMillis) {
        long cutoff = now - windowMillis;
        while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
            timestamps.pollFirst();
        }
    }

    private void sweep(long now, long windowMillis) {
        hits.entrySet().removeIf(entry -> {
            Deque<Long> timestamps = entry.getValue();
            synchronized (timestamps) {
                evictExpired(timestamps, now, windowMillis);
                return timestamps.isEmpty();
            }
        });
    }
}
