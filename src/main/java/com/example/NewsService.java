package com.example;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NewsService {

    private static final int ARTICLE_LIMIT = 30;

    private static final String HACKER_NEWS_TOP_STORIES_URL =
            "https://hacker-news.firebaseio.com/v0/topstories.json";

    private static final String HACKER_NEWS_ITEM_URL =
            "https://hacker-news.firebaseio.com/v0/item/";

    private static final String HATENA_URL =
            "https://b.hatena.ne.jp/hotentry/it.rss";

    private final Gson gson = new Gson();
    private final HttpClient httpClient = new HttpClient();

    /**
     * Hacker Newsとはてなからニュースを取得する
     */
    public List<Article> fetchAll() throws Exception {

        List<Article> articles = new ArrayList<>();

        articles.addAll(fetchHackerNews());
        articles.addAll(fetchHatena());

        return articles;
    }

    /**
     * Hacker Newsから記事を取得する
     */
    private List<Article> fetchHackerNews() throws Exception {

        List<Article> articles = new ArrayList<>();

        String response =
                httpClient.get(HACKER_NEWS_TOP_STORIES_URL);

        String cleaned =
                response.replace("[", "")
                        .replace("]", "");

        String[] ids = cleaned.split(",");

        int limit = Math.min(ARTICLE_LIMIT, ids.length);

        for (int i = 0; i < limit; i++) {

            String id = ids[i].trim();

            String itemUrl =
                    HACKER_NEWS_ITEM_URL + id + ".json";

            String itemResponse =
                    httpClient.get(itemUrl);

            Article article =
                    gson.fromJson(itemResponse, Article.class);

            article.source = "Hacker News";

            articles.add(article);
        }

        return articles;
    }

    /**
     * はてなから記事を取得する
     */
    private List<Article> fetchHatena() throws Exception {

        List<Article> articles = new ArrayList<>();

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder =
                factory.newDocumentBuilder();

        Document document =
                builder.parse(HATENA_URL);

        NodeList items =
                document.getElementsByTagName("item");

        int limit = Math.min(ARTICLE_LIMIT, items.getLength());

        for (int i = 0; i < limit; i++) {

            Element item =
                    (Element) items.item(i);

            String title =
                    item.getElementsByTagName("title")
                            .item(0)
                            .getTextContent();

            String link =
                    item.getElementsByTagName("link")
                            .item(0)
                            .getTextContent();

            String count =
                    item.getElementsByTagName(
                            "hatena:bookmarkcount")
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
}