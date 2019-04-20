package co.igorski.client;

import co.igorski.exceptions.SnitcherException;

import java.io.IOException;
import java.util.Map;

public interface WebClient {

    int login(String url, Map<String, String> form) throws IOException, SnitcherException;

    String post(String url, String body) throws IOException, SnitcherException;
}
