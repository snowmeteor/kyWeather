package org.ky.weather;

/**
 * 天气预报
 * 
 * @author snowmeteor
 *
 */
public class Weather {
	private String cityName;// 城市名称
	private String sportsSuggestion;// 运动建议
	private String sportsHint;// 运动指数
	private String description;// 天气描述
	private String minTemp;// 最低温度
	private String maxTemp;// 最高温度
	private String currentTemp;// 当前温度
	private String windDirection;// 风向
	private String windPower;// 风力
	private String humidity;// 湿度

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getSportsSuggestion() {
		return sportsSuggestion;
	}

	public void setSportsSuggestion(String sportsSuggestion) {
		this.sportsSuggestion = sportsSuggestion;
	}

	public String getSportsHint() {
		return sportsHint;
	}

	public void setSportsHint(String sportsHint) {
		this.sportsHint = sportsHint;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(String minTemp) {
		this.minTemp = minTemp;
	}

	public String getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(String maxTemp) {
		this.maxTemp = maxTemp;
	}

	public String getCurrentTemp() {
		return currentTemp;
	}

	public void setCurrentTemp(String currentTemp) {
		this.currentTemp = currentTemp;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public String getWindPower() {
		return windPower;
	}

	public void setWindPower(String windPower) {
		this.windPower = windPower;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	@Override
	public String toString() {
		return "Weather{" + "cityName='" + cityName + '\'' + ", sportsSuggestion='" + sportsSuggestion + '\''
				+ ", sportsHint='" + sportsHint + '\'' + ", description='" + description + '\'' + ", minTemp='"
				+ minTemp + '\'' + ", maxTemp='" + maxTemp + '\'' + ", currentTemp='" + currentTemp + '\''
				+ ", windDirection='" + windDirection + '\'' + ", windPower='" + windPower + '\'' + ", humidity='"
				+ humidity + '\'' + '}';
	}
}