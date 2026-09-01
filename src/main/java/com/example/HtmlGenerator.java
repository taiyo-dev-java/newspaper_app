package com.example;

import java.io.FileWriter;
import java.util.List;

public class HtmlGenerator {

    private static final String OUTPUT_FILE =
            "index.html";

    /**
     * 記事一覧からHTMLを生成する
     */
    public void generate(List<Article> articles)
            throws Exception {

        StringBuilder html =
                new StringBuilder();

        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>News</title>");
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

        try (
            FileWriter writer =
                    new FileWriter(OUTPUT_FILE)
        ) {
            writer.write(html.toString());
        }

        System.out.println(
                OUTPUT_FILE + " を作成しました");
    }
}