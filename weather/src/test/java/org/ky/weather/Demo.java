package org.ky.weather;

public class Demo {

	public static void main(String[] args) {
		// 查询城市
		Double longitude = 116.41;// 经度
		Double latitude = 39.9316;// 纬度

		System.out.println(ChinaCityFinder.getLocationId(longitude, latitude));
		System.out.println(ChinaCityFinder.getCity(longitude, latitude));

		// 查询城市天气
		double t1 = System.currentTimeMillis();
		System.out.println(WeatherSearcher.getWeatherFromAPI(longitude, latitude));
		double t2 = System.currentTimeMillis();
		System.out.println("查询时间：" + (t2 - t1) / 1.0 + "s");

		System.out.println(WeatherSearcher.getWeatherFromHtml(longitude, latitude));
		double t3 = System.currentTimeMillis();
		System.out.println("查询时间：" + (t3 - t2) / 1.0 + "s");
	}

}
