package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;

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
            StringBuilder html = new StringBuilder();

            html.append("<html>");
            html.append("<head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<title>Hacker News Top 30</title>");
            html.append("</head>");
            html.append("<body>");

            html.append("<h1>Hacker News Top 30</h1>");

            for (Article article : articles) {

                html.append("<hr>");

                html.append("<h2>")
                        .append(article.title)
                        .append("</h2>");

                html.append("<p>Score : ")
                        .append(article.score)
                        .append("</p>");

                if (article.url != null) {

                    html.append("<a href='")
                            .append(article.url)
                            .append("'>")
                            .append(article.url)
                            .append("</a>");
                }
            }

            html.append("</body>");
            html.append("</html>");

            FileWriter writer = new FileWriter("news.html");

            writer.write(html.toString());

            writer.close();

            System.out.println("news.html を作成しました");


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