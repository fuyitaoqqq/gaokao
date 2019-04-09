package cn.animalcity.gaokao.net;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: fuyitao
 * @date: 2019/04/09
 */

@Slf4j
public class NetApplication {

    public static void main(String[] args) {

        long start = System.nanoTime();
        System.out.println("Start time: " + start);

        List<String> universityNameList = readFile();
        //Stream.iterate(1, i -> i+1).limit(universityNameList.size())
        universityNameList.forEach(item -> {
            int i = universityNameList.indexOf(item);
            String realUrl = null;
            try {
                realUrl = getRealUrlFromBaiDu(item);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            writeFile(item + "\t" + realUrl);
            log.info(i + " " + item + " " + realUrl);
        });

        long finish = System.nanoTime();
        System.out.println("Finish time:" + finish);
        System.out.println("Count:" + (finish-start));

    }

    /*public static void main(String[] args) {
        String name = "北京大学";
        try {
            getRealUrlFromGoogle(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 读取university文件，并将文件名放到arrayList
     *
     * @return arrayList
     */
    private static List<String> readFile() {
        final String filename = "gaokao-net/university.txt";
        List<String> name = new ArrayList<>(400);

        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                name.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 追加将大学名字和官网url写入文件
     *
     * @param content 格式为“name + '\t' + url”
     */
    private static void writeFile(String content) {
        File newFile = new File("gaokao-net/university-url.txt");
        try {
            FileWriter writer = new FileWriter(newFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(content + "\r\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRealUrlFromGoogle(String university) throws IOException {

        String encode = URLEncoder.encode(university, "utf-8");


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://www.google.com/q=" + encode);
        HttpHost proxy = new HttpHost("192.168.0.131", 1080);

        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        /*RequestConfig requestConfig = RequestConfig.custom()
                //.setProxy(proxy)

                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();*/

        httpGet.setHeader("User-Agent", GaokaoHttpEnum.USER_AGENT.getValue());

        httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();

        CloseableHttpResponse response = httpClient.execute(httpGet);

        String s = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println(s);

        return  null;
    }

    /**
     * 根据大学名字，利用百度搜索引擎，获取大学官网URL
     *
     * @param university 大学名字
     * @return 官网URL
     */
    private static String getRealUrlFromBaiDu(String university) throws UnsupportedEncodingException {
        String request = university + GaokaoHttpEnum.OUT_TIEBA.getValue() + GaokaoHttpEnum.OUT_ZHIDAO.getValue();
        String encodeUrl = GaokaoHttpEnum.BAIDU_URL.getValue() + URLEncoder.encode(request, "utf-8");

        String realUrl = "";

        /*
         * 解析百度搜索html，JSoup获取每个university的百度加密链接href
         * 排除百度贴吧和百度知道的搜索结果
         */
        String html = HttpUtil.get(encodeUrl);
        Document document = Jsoup.parse(html);
        //匹配百度加密链接
        String href = document.select("div#1").select("h3").select("a[href]").attr("href");
        if (href != null) {
            Connection connection = Jsoup.connect(href);

            /*
             * 构造header
             */
            Map<String, String> header = new HashMap<>(3);
            header.put("User-Agent", GaokaoHttpEnum.USER_AGENT.getValue());
            header.put("Accept", GaokaoHttpEnum.ACCEPT.getValue());
            header.put("Accept-Language", GaokaoHttpEnum.ACCEPT_LANGUAGE.getValue());
            connection.headers(header);

            /*
             * 百度真实url放在header的Location中，所以将followRedirects设置为false
             */
            try {
                Connection.Response response = connection.timeout(2000).method(Connection.Method.GET).followRedirects(false).execute();
                realUrl = response.header("Location");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return realUrl;
        }
        return null;
    }

}
