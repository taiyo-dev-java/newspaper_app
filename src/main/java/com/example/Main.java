package com.example;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            NewsService newsService = new NewsService();
            ArticleRepository repository = new ArticleRepository();
            HtmlGenerator htmlGenerator = new HtmlGenerator();

            // ニュースを取得
            List<Article> articles = newsService.fetchAll();

            // DBに保存
            repository.saveAll(articles);

            // HTMLを生成
            htmlGenerator.generate(articles);

            System.out.println("処理が完了しました");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}