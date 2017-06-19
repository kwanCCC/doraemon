package org.doraemon.treasure.gear;

import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.curator.x.discovery.strategies.StickyStrategy;

import java.util.Optional;

public class SelectStrategyFactory {
    private SelectStrategyFactory() {}

    public static <T> Optional<ProviderStrategy<T>> getStrategy(ServiceStrategy strategy) {
        switch (strategy) {
            case RANDOM:
                return Optional.of(new RandomStrategy<>());
            case ROUND_ROBIN:
                return Optional.of(new RoundRobinStrategy<>());
            case STICKY_RANDOM:
                return Optional.of(new StickyStrategy<>(new RandomStrategy<>()));
            case STICKY_ROUND_ROBIN:
                return Optional.of(new StickyStrategy<>(new RoundRobinStrategy<>()));
            default:
                return Optional.empty();
        }
    }
}
