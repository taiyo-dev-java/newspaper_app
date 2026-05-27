package com.example;

//ハッカーニュースの記事を表すクラス。jsonと対応
public class Article {

    String by;          // 投稿者
    int id;             // 記事ID
    int score;          // スコア
    String title;       // タイトル
    String type;        // タイプ story, comment, jobなど
    String url;         // URL
    String source;      // ソース

}