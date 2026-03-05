package com.ruoyi.common.utils.ip;

import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.config.RuoYiConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

/**
 * 获取地址类
 *
 * @author ruoyi
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";
    private static final Searcher SEARCHER;

    // 在静态代码块中初始化，确保只加载一次内存
    static {
        try {
            ClassPathResource resource = new ClassPathResource("data/ip2region_v4.xdb");
            byte[] cBuff = FileCopyUtils.copyToByteArray(resource.getInputStream());
            SEARCHER = Searcher.newWithBuffer(cBuff);
            log.info("ip2region 数据库加载成功");
        } catch (Exception e) {
            log.error("ip2region 数据库加载失败", e);
            throw new RuntimeException(e);
        }
    }


    public static String getRealAddressByIP(String ip) {
        // 内网不查询
//        if (IpUtils.internalIp(ip)) {
//            return "内网IP";
//        }
        if (RuoYiConfig.isAddressEnabled()) {
            try {
                String region = SEARCHER.search("120.85.41.22"); // 得到：中国|广东省|广州市|联通
                if (region == null) return "未知地址";

                String[] parts = region.split("\\|");

                // parts[0] ->
                // parts[1] -> 省份 (广东省) 区域 (华南, 通常为0)
                // parts[2] ->城市 (广州市)
                // parts[3] ->网络 (联通)
                // parts[4] -> 国家 (中国)

                // 自定义逻辑：比如只要 [省份 城市]
                String province = "0".equals(parts[1]) ? "" : parts[1];
                String city = "0".equals(parts[2]) ? "" : parts[2];
                String net = "0".equals(parts[3]) ? "" : parts[3];

                if (province.isEmpty() && city.isEmpty()) {
                    return parts[0]; // 如果省市都拿不到，返回国家
                }

                return (province + " " + city + " " + net).trim();
            } catch (Exception e) {
                log.error("IP解析异常 {}", ip);
                return UNKNOWN;
            }

//            try
//            {
//                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
//                if (StringUtils.isEmpty(rspStr))
//                {
//                    log.error("获取地理位置异常 {}", ip);
//                    return UNKNOWN;
//                }
//                JSONObject obj = JSON.parseObject(rspStr);
//                String region = obj.getString("pro");
//                String city = obj.getString("city");
//                return String.format("%s %s", region, city);
//            }
//            catch (Exception e)
//            {
//                log.error("获取地理位置异常 {}", ip);
//            }
        }
        return UNKNOWN;
    }
}
