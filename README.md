# kyWeather

根据经纬度获取中国城市的天气（数据来源-[中国天气网](http://www.weather.com.cn/)）

## 代码示例

```
// 查询城市
double longitude = 116.41;// 经度
double latitude = 39.9316;// 纬度

System.out.println(ChinaCityFinder.getLocationId(longitude, latitude));
System.out.println(ChinaCityFinder.getCity(longitude, latitude));

// 通过API查询城市天气
double t1 = System.currentTimeMillis();
System.out.println(WeatherSearcher.getWeatherFromAPI(longitude, latitude));
double t2 = System.currentTimeMillis();
System.out.println("查询时间：" + (t2 - t1));

// 爬取网页查询城市天气
System.out.println(WeatherSearcher.getWeatherFromHtml(longitude, latitude));
double t3 = System.currentTimeMillis();
System.out.println("查询时间：" + (t3 - t2));
```
