package com.ruoyi.common.utils.ip;

import org.lionsoul.ip2region.xdb.Searcher;
import java.util.concurrent.TimeUnit;

public class IpRegionDemo {

    public static void main(String[] args) {
             // 1、项目的 xdb 文件路径
        String dbPath = "D:\\区块链比赛\\区块链赛前B站笔记++++\\Spring系列\\个人项目实战\\刷题项目\\paperProject\\daming-admin\\ruoyi-admin\\src\\main\\resources\\data\\ip2region_v4.xdb";

        // 2、创建查询对象
        // 备注：xdb 支持从文件查询、也可以预加载到内存以提升速度。这里展示从文件查询。
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithFileOnly(dbPath);

            // 3、查询 IP
            String ip = "120.85.41.22";
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - sTime);

            // 4、打印结果
            // 格式：国家|区域|省份|城市|ISP
            System.out.printf("IP: %s, 地域: %s, 耗时: %d μs\n", ip, region, cost);

        } catch (Exception e) {
            System.err.printf("查询失败(%s): %s\n", dbPath, e.getMessage());
        } finally {
            // 5、关闭资源
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}