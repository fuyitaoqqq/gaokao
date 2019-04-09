package cn.animalcity.gaokao.net;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: fuyitao
 * @date: 2019/04/09
 */

@Getter
@AllArgsConstructor
public enum GaokaoHttpEnum {

    /**
     * 定义百度搜索接口
     */
    BAIDU_URL("https://www.baidu.com/s?wd="),

    /**
     * header: User-Agent
     */
    USER_AGENT("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36"),

    /**
     * header: Accept
     */
    ACCEPT("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),

    /**
     * header: Accept-Language
     */
    ACCEPT_LANGUAGE("zh-cn,zh;q=0.5"),

    /**
     * 排除百度知道的搜索结果
     */
    OUT_ZHIDAO(" -site:zhidao.baidu.com"),

    /**
     * 排除百度贴吧的搜索结果
     */
    OUT_TIEBA(" -site:tieba.baidu.com");

    private String value;

}
