package cn.animalcity.gaokao.net;

import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author: fuyitao
 * @date: 2019/04/09
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class GetOfficialUrl {

    @Test
    public void getUri() {

        final String urlString = "https://www.baidu.com/s?wd=";
        String body = "北京大学";

        String html = HttpUtil.get(urlString+body);

        Document document = Jsoup.parse(html);
        String attr = document.select("div#1").select("h3").select("a[href]").attr("href");
        System.out.println(attr);

//        String s = HttpUtil.get(attr);
//        System.out.println(s);
        try {
            Connection.Response response = Jsoup.connect(attr).method(Connection.Method.GET).followRedirects(false).execute();
            String realUrl = response.header("Location");
            System.out.println(realUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
