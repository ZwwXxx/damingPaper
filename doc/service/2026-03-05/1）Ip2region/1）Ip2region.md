# 0）效果演示

![](http://cdn.zww0891.fun/image-20260305122912862.png)

# 1）依赖引入

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>ip2region</artifactId>
    <version>2.7.0</version>
</dependency>
```

# 2）准备数据库文件 (`ip2region.xdb`)

[ip2region/data at master · lionsoul2014/ip2region](https://github.com/lionsoul2014/ip2region/tree/master/data)

![image-20260305122529655](http://cdn.zww0891.fun/image-20260305122529655.png)

v4——ipv4

v6——ipv6

这里下载ipv4的即可

# 3）ip2region.xdb放入指定路径

![image-20260305122613954](http://cdn.zww0891.fun//image-20260305122613954.png)

# 4）在静态代码块中初始化，确保只加载一次内存

```java
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
```

# 5）调用方法获取地址

```java
public static String getRealAddressByIP(String ip) {
 
       
            try {
                String region = SEARCHER.search(ip); // 得到：中国|广东省|广州市|联通
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
```

获取的格式为中国|广东省|广州市|联通

可以通过正则分割为数组元素，然后判断是否为空拼接完整地址

