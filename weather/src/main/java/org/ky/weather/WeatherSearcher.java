package org.ky.weather;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;

/**
 * 根据经纬度查找所在城市天气
 * 
 * 下面的接口可获得城市天气情况： 实况天气获取：http://www.weather.com.cn/data/sk/城市代码.html
 * 城市信息获取：http://www.weather.com.cn/data/cityinfo/城市代码.html
 * 详细指数获取：http://www.weather.com.cn/data/zs/城市代码.html
 * 
 * @author snowmeteor
 *
 */
public final class WeatherSearcher {

	public static final String URL_SK = "http://weather.com.cn/data/sk/%d.html";// 实况天气
	public static final String URL_CITYINFO = "http://weather.com.cn/data/cityinfo/%d.html";// 城市信息
	public static final String URL_ZS = "http://weather.com.cn/data/zs/%d.html";// 详细指数

	/**
	 * 通过经纬度及中国天气网不公开API查询天气，信息比较完整
	 *
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static Weather getWeatherFromAPI(Double longitude, Double latitude) {
		if (null == longitude || null == latitude) {
			return null;
		}

		return getWeather(ChinaCityFinder.getLocationId(longitude, latitude));
	}

	/**
	 * 通过城市ID查询天气
	 *
	 * @param cityId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Weather getWeather(int cityId) {
		// 当前发现北京市东城西城取不着，改用北京的取，可能还有别的城市有类似情况
		if (101011600 == cityId || 101011700 == cityId) {
			cityId = 101010100;
		}

		Weather weather = new Weather();
		Gson gson = new Gson();
		try {

			String url = String.format(URL_CITYINFO, cityId);
			Document doc = Jsoup.connect(url).timeout(4000).get();

			Map<String, Object> mainMap = gson.fromJson(doc.text(), Map.class);
			Map<String, Object> map = (Map<String, Object>) MapUtils.getMap(mainMap, "weatherinfo");

			weather.setCityName(MapUtils.getString(map, "city"));
			weather.setMinTemp(MapUtils.getString(map, "temp1"));
			weather.setMaxTemp(MapUtils.getString(map, "temp2"));
			weather.setDescription(MapUtils.getString(map, "weather"));

			url = String.format(URL_ZS, cityId);
			doc = Jsoup.connect(url).timeout(4000).get();
			mainMap = gson.fromJson(doc.text(), Map.class);
			map = (Map<String, Object>) MapUtils.getMap(mainMap, "zs");
			weather.setSportsHint(MapUtils.getString(map, "yd_hint"));
			weather.setSportsSuggestion(MapUtils.getString(map, "yd_des"));

			url = String.format(URL_SK, cityId);
			doc = Jsoup.connect(url).timeout(4000).get();
			mainMap = gson.fromJson(doc.text(), Map.class);
			map = (Map<String, Object>) MapUtils.getMap(mainMap, "weatherinfo");
			weather.setCurrentTemp(MapUtils.getString(map, "temp"));
			weather.setWindDirection(MapUtils.getString(map, "WD"));
			weather.setWindPower(MapUtils.getString(map, "WS"));
			weather.setHumidity(MapUtils.getString(map, "SD"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return weather;

	}

	/**
	 * 通过经纬度从中国天气网网页解析网页元素获取天气，信息比较简略
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static Weather getWeatherFromHtml(Double longitude, Double latitude) {
		if (null == longitude || null == latitude) {
			return null;
		}

		Weather weather = new Weather();
		weather.setCityName(ChinaCityFinder.getCity(longitude, latitude).getCityZh());

		String url = "https://e.weather.com.cn/d/town/idetail?lat=%f&lon=%f&i=yd";
		url = String.format(url, latitude, longitude);

		try {
			Document doc = Jsoup.connect(url).timeout(4000).get();
			String html = doc.html();

			weather.setSportsHint(JsoupHelper.fetchText(html, "//h2[@id='shiyi2']/text()"));
			weather.setSportsSuggestion(
					JsoupHelper.fetchText(html, "//div[@class='zhishu-foot zhishu-foot2']/p/text()"));

			String tianqi = JsoupHelper.fetchText(html, "//p[@id='tianqi2']/text()");
			tianqi = tianqi.replaceFirst("天气：", "");
			weather.setDescription(StringUtils.trim(tianqi));

			String fengli = JsoupHelper.fetchText(html, "//p[@id='fengli2']/text()");
			fengli = fengli.replaceFirst("风力：", "");
			fengli = fengli.replaceFirst("&lt;", "<");
			fengli = fengli.replaceFirst("&gt;", ">");
			weather.setWindPower(StringUtils.trim(fengli));

			String wendu = JsoupHelper.fetchText(html, "//p[@id='wendu2']/text()");
			wendu = wendu.replaceFirst("温度：", "");
			wendu = wendu.replaceAll("&deg;C", "");
			String[] wendus = wendu.split("/");
			weather.setMinTemp(wendus[1]);
			weather.setMaxTemp(wendus[0]);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return weather;
	}

}
