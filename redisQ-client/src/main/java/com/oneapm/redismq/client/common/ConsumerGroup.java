package com.oneapm.redismq.client.common;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.Set;

@AllArgsConstructor
public final class ConsumerGroup {
    @Getter
    private final Set<String> consumerGroup;

    public ConsumerGroup add(String consumerGroup) {
        Preconditions.checkNotNull(consumerGroup != null, "ConsumerGroup not init");
        this.consumerGroup.add(consumerGroup);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConsumerGroup other = (ConsumerGroup) obj;
        if (CollectionUtils.isNotEmpty(consumerGroup)
            && CollectionUtils.isNotEmpty(other.consumerGroup)
            && consumerGroup.size() == other.consumerGroup.size()) {
            Iterator<String> iterator = consumerGroup.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (!other.consumerGroup.contains(next)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        consumerGroup.forEach(item -> {
            stringBuilder.append(item);
            stringBuilder.append("/n");
        });
        return stringBuilder.toString();
    }

    public void merge(ConsumerGroup other) {
        synchronized (consumerGroup) {
            consumerGroup.addAll(other.consumerGroup);
        }
    }
}
