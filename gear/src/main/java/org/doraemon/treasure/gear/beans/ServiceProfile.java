package org.doraemon.treasure.gear.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class ServiceProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String              profile;
    private Map<String, Object> properties;
}
