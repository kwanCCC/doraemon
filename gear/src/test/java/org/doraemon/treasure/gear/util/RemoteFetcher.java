package org.doraemon.treasure.gear.util;

import com.blueocn.gear.client.exceptions.GearClientException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.http.HttpStatus.SC_OK;

public class RemoteFetcher {

    private final int timeoutInSeconds;

    public RemoteFetcher() {
        this(10);
    }

    public RemoteFetcher(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public String getResponse(String url) throws InterruptedException {
        try (CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
            client.start();
            HttpGet request = new HttpGet(url);
            Future<HttpResponse> future = client.execute(request, null);
            HttpResponse resp = future.get(timeoutInSeconds, TimeUnit.SECONDS);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() != SC_OK) {
                    throw new GearClientException(
                            "query configuration response error, status code is " +
                            resp.getStatusLine().getStatusCode());
                }

                return IOUtils.toString(resp.getEntity().getContent());
            }
            throw new GearClientException("Received null response from gear server");
        } catch (IOException | ExecutionException | TimeoutException e) {
            throw new GearClientException(e);
        }
    }
}