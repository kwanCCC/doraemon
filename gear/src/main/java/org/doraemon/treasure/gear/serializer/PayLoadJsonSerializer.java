package org.doraemon.treasure.gear.serializer;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.doraemon.treasure.gear.beans.ServiceProfile;

import java.io.IOException;
import java.util.Map;

public class PayLoadJsonSerializer extends JsonInstanceSerializer<ServiceProfile> {

    public PayLoadJsonSerializer() {
        super(ServiceProfile.class);
    }

    @Override
    public ServiceInstance<ServiceProfile> deserialize(byte[] bytes) throws Exception {
        ServiceInstance<Object> rawServiceInstance = this.deserialize0(bytes);
        ServiceInstanceBuilder<ServiceProfile> builder =
                ServiceInstance.<ServiceProfile>builder().address(rawServiceInstance.getAddress())
                                                         .id(rawServiceInstance.getId())
                                                         .name(rawServiceInstance.getName())
                                                         .port(rawServiceInstance.getPort())
                                                         .registrationTimeUTC(rawServiceInstance.getRegistrationTimeUTC())
                                                         .serviceType(rawServiceInstance.getServiceType())
                                                         .sslPort(rawServiceInstance.getSslPort() == null
                                                                  ? 0
                                                                  : rawServiceInstance.getSslPort())
                                                         .uriSpec(rawServiceInstance.getUriSpec());
        if (rawServiceInstance.getPayload() instanceof Map) {
            builder.payload(new ServiceProfile(
                    StringUtils.EMPTY,
                    (Map<String, Object>) rawServiceInstance.getPayload()
            ));
        } else if (rawServiceInstance.getPayload() instanceof ServiceProfile) {
            builder.payload((ServiceProfile) rawServiceInstance.getPayload());
        }
        return builder.build();
    }

    private ServiceInstance<Object> deserialize0(byte[] bytes) throws JsonParseException, JsonMappingException,
                                                                      IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(bytes, mapper.getTypeFactory().constructType(ServiceInstance.class));
    }
}
