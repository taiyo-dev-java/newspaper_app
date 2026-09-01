package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    /**
     * HTTP GETでレスポンスを取得する
     */
    public String get(String urlString) throws Exception {

        URL url = new URL(urlString);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        try (
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()))
        ) {

            StringBuilder response =
                    new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } finally {
            connection.disconnect();
        }
    }
}