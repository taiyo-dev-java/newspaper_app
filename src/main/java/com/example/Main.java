package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Main {

    public static void main(String[] args) {

    try {

        List<Article> articles = new ArrayList<>();

        articles.addAll(fetchHackerNews());

        articles.addAll(fetchHatena());

        saveArticles(articles);

        generateHtml(articles);

    } catch (Exception e) {
        e.printStackTrace();
    }

    }


    private static List<Article> fetchHackerNews() throws Exception {

        List<Article> articles = new ArrayList<>();

        Gson gson = new Gson();

        // ハッカーニューストップ記事ID一覧取得
        String topStoriesUrl =
            "https://hacker-news.firebaseio.com/v0/topstories.json";

        String topStoriesResponse = getResponse(topStoriesUrl);

        System.out.println("Top Stories IDs:");
        System.out.println(topStoriesResponse);

        // JSON配列を雑に分割
        String cleaned =
            topStoriesResponse.replace("[", "").replace("]", "");

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
                article.source = "Hacker News";

            // Listへ追加
            articles.add(article);
        }

        // ここにハッカーニュースから記事を取得するコードを実装
        return articles;
    }

    private static List<Article>fetchHatena() throws Exception {

        List<Article> articles = new ArrayList<>();

        String hatenaUrl =
                    "https://b.hatena.ne.jp/hotentry/it.rss";

        DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

        DocumentBuilder builder =
                    factory.newDocumentBuilder();

        Document document =
                    builder.parse(hatenaUrl);

        NodeList items =
                    document.getElementsByTagName("item");

        for (int i = 0; i < 30; i++) {

        Element item =  (Element) items.item(i);

        String title =  item.getElementsByTagName("title")
                        .item(0)
                        .getTextContent();

        String link =   item.getElementsByTagName("link")
                        .item(0)
                        .getTextContent();

        String count =   item.getElementsByTagName("hatena:bookmarkcount")
                        .item(0)
                        .getTextContent();

        Article article = new Article();

        article.source = "HatenaBlog";

        article.title = title;

        article.url = link;

        article.score = Integer.parseInt(count);

        articles.add(article);
        }    
        return articles;
    }

    private static void saveArticles(List<Article> articles) throws Exception {
        
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            url = LocalConfig.DB_URL;
            user = LocalConfig.DB_USER;
            password = LocalConfig.DB_PASSWORD;
        }

        //DBへ保存
        Connection conn = DriverManager.getConnection(url, user, password);

        String sql =
                    "INSERT INTO articles(source, title, url, score) "
                    + "VALUES (?, ?, ?, ?) "
                    + "ON CONFLICT (url) DO NOTHING";

        PreparedStatement ps = conn.prepareStatement(sql);

        for (Article article : articles) {
            System.out.println("登録試行: " +article.title);

            ps.setString(1, article.source);
            ps.setString(2, article.title);
            ps.setString(3, article.url);
            ps.setInt(4, article.score);
                    
            int count = ps.executeUpdate();


            System.out.println("登録件数: " + count);
                            
        }

        ps.close();
        conn.close();
        
    }

    private static void generateHtml(List<Article> articles) throws Exception {
    //LIstの中身をすべて出力
        StringBuilder html = new StringBuilder();

        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Hacker News Top 30</title>");
        html.append("</head>");
        html.append("<body>");

        String currentSource = "";

        for (Article article : articles) {

            if (!article.source.equals(currentSource)) {

                currentSource = article.source;

                html.append("<h1>")
                    .append(currentSource)
                    .append("</h1>");
            }

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

        FileWriter writer = new FileWriter("index.html");

        writer.write(html.toString());

        writer.close();

        System.out.println("index.html を作成しました");
        
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