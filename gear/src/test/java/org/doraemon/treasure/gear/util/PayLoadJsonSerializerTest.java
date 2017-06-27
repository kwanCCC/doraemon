package org.doraemon.treasure.gear.util;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.junit.Before;
import org.junit.Test;

import com.blueocn.gear.client.beans.ServiceProfile;

import junit.framework.Assert;

public class PayLoadJsonSerializerTest {

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testDeserializeByteArray() throws Exception {
        PayLoadJsonSerializer s = new PayLoadJsonSerializer();
        String json =
            "{\n"
            + "  \"name\": \"gear\",\n"
            + "  \"id\": \"bcaea663-a3e7-4c0f-9803-b24287fbfb69\",\n"
            + "  \"address\": \"localhost\",\n"
            + "  \"port\": 9000,\n"
            + "  \"payload\": {\n"
            + "    \"profile\": \"aaaa\"\n"
            + "  },\n"
            + "  \"registrationTimeUTC\": 1456208141463,\n"
            + "  \"serviceType\": \"DYNAMIC\"\n"
            + "}\n";

        ServiceInstance<ServiceProfile> serviceInstance = s.deserialize(json.getBytes("UTF-8"));

        Assert.assertEquals(serviceInstance.getName(), "gear");
        Assert.assertEquals(serviceInstance.getId(), "bcaea663-a3e7-4c0f-9803-b24287fbfb69");
        Assert.assertEquals(serviceInstance.getAddress(), "localhost");
        Assert.assertEquals(serviceInstance.getPort(), new Integer(9000));
        Assert.assertEquals(serviceInstance.getRegistrationTimeUTC(), 1456208141463l);
        Assert.assertEquals(serviceInstance.getServiceType(), ServiceType.DYNAMIC);
        Assert.assertEquals(serviceInstance.getSslPort(), new Integer(0));
        Assert.assertEquals(serviceInstance.getUriSpec(), null);
        Assert.assertEquals(serviceInstance.getPayload(), new ServiceProfile("aaaa"));
    }

}
