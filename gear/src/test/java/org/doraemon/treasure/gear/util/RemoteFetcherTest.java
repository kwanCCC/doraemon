package org.doraemon.treasure.gear.util;

import com.blueocn.gear.client.exceptions.GearClientException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.doraemon.treasure.gear.util.AssertUtils.expectFailing;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class RemoteFetcherTest {
    private static final String resource = "/blah/";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private final String response = "whatever this is";
    private final RemoteFetcher fetcher = new RemoteFetcher(1);

    private String baseUri;

    @Before
    public void setUp() {
        baseUri = String.format("http://localhost:%d/", wireMockRule.port());
    }

    @Test
    public void fetchesResponseFromRemoteSuccessfully() throws InterruptedException {
        stubFor(get(urlEqualTo(resource))
                    .willReturn(
                        aResponse()
                            .withStatus(SC_OK)
                            .withBody(response)));

        String actual = fetcher.getResponse(baseUri + resource);

        assertThat(actual, is(response));
    }

    @Test
    public void blowsUpOnResponseIsNotOk() throws InterruptedException {
        try {
            fetcher.getResponse(baseUri + resource);
            expectFailing(GearClientException.class);
        } catch (GearClientException e) {
            assertThat(e.getMessage(), is("query configuration response error, status code is 404"));
        }
    }

    @Test
    public void blowsUpIfHostIsUnknown() throws InterruptedException {
        try {
            fetcher.getResponse("http://hostname:port/" + resource);
            expectFailing(GearClientException.class);
        } catch (GearClientException e) {
            assertThat(e.getCause(), instanceOf(ExecutionException.class));
        }
    }

    @Test
    public void blowsUpIfRemoteIsTooSlow() throws InterruptedException {
        stubFor(get(urlEqualTo(resource))
                    .willReturn(
                        aResponse()
                            .withStatus(SC_OK)
                            .withFixedDelay(1500)
                            .withBody(response)));

        try {
            fetcher.getResponse(baseUri + resource);
            expectFailing(GearClientException.class);
        } catch (GearClientException e) {
            assertThat(e.getCause(), instanceOf(TimeoutException.class));
        }
    }
}