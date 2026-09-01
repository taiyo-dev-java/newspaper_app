package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class ArticleRepository {

    private static final String INSERT_SQL =
            "INSERT INTO articles(source, title, url, score) "
            + "VALUES (?, ?, ?, ?) "
            + "ON CONFLICT (url) DO NOTHING";

    /**
     * 記事をDBへ保存する
     */
    public void saveAll(List<Article> articles) throws Exception {

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // GitHub Actionsなどで環境変数がない場合
        // ローカル設定を使用
        if (url == null || user == null || password == null) {
            url = LocalConfig.DB_URL;
            user = LocalConfig.DB_USER;
            password = LocalConfig.DB_PASSWORD;
        }

        try (
            Connection connection =
                    DriverManager.getConnection(
                            url,
                            user,
                            password);

            PreparedStatement statement =
                    connection.prepareStatement(INSERT_SQL)
        ) {

            for (Article article : articles) {

                statement.setString(
                        1,
                        article.source);

                statement.setString(
                        2,
                        article.title);

                statement.setString(
                        3,
                        article.url);

                statement.setInt(
                        4,
                        article.score);

                int count =
                        statement.executeUpdate();

                System.out.println(
                        "登録: " + article.title
                        + " / 件数: " + count);
            }
        }
    }
}