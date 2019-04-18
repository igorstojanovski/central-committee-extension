package co.igorski.client;

import co.igorski.exceptions.SnitcherException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ApacheHttpClient implements WebClient {
    private final BasicCookieStore cookieStore = new BasicCookieStore();
    private final CloseableHttpClient httpClient;

    public ApacheHttpClient() {
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    @Override
    public int login(String url, Map<String, String> form) throws IOException, SnitcherException {
        int statusCode;
        try {
            HttpUriRequest login = RequestBuilder.post()
                    .setUri(new URI(url))
                    .addParameter("username", form.get("username"))
                    .addParameter("password", form.get("password"))
                    .build();

            try (CloseableHttpResponse loginResponse = httpClient.execute(login)) {
                HttpEntity entity = loginResponse.getEntity();
                statusCode = loginResponse.getStatusLine().getStatusCode();
                EntityUtils.consume(entity);
                cookieStore.getCookies();
            }
        } catch (URISyntaxException e) {
            throw new SnitcherException("Bad url when logging in.", e);
        }

        return statusCode;
    }

    @Override
    public String post(String url, String body) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(body);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(httpPost);

        return EntityUtils.toString(response.getEntity());
    }
}
