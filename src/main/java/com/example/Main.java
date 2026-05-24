package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            Gson gson = new Gson();
            List<Article> articles = new ArrayList<>();

            // トップ記事ID一覧取得
            String topStoriesUrl =
                    "https://hacker-news.firebaseio.com/v0/topstories.json";

            String topStoriesResponse = getResponse(topStoriesUrl);

            System.out.println("Top Stories IDs:");
            System.out.println(topStoriesResponse);

            // JSON配列を雑に分割
            String cleaned =
                    topStoriesResponse.replace("[", "")
                            .replace("]", "");

            String[] ids = cleaned.split(",");

            System.out.println("\n===== TOP 30 ARTICLES =====");

            for (int i = 0; i < 30; i++) {

                String id = ids[i].trim();

                String itemUrl =
                    "https://hacker-news.firebaseio.com/v0/item/"
                    + id
                    + ".json";

                // APIレスポンス取得
                String itemResponse = getResponse(itemUrl);

                // JSON → Article変換
                Article article =
                gson.fromJson(itemResponse, Article.class);

                // Listへ追加
                articles.add(article);
            }

            //LIstの中身をすべて出力
            System.out.println("\n===== ARTICLE LIST =====");

            for (Article article : articles) {

                System.out.println("--------------------");

                System.out.println("Title : " + article.title);

                System.out.println("URL   : " + article.url);

                System.out.println("Score : " + article.score);
            }   


        //エラー時の処理
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // HTTP GET 共通処理
    public static String getResponse(String urlString) throws Exception {

        URL url = new URL(urlString);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }
}