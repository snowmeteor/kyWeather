package org.ky.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 根据经纬度查找所在城市（仅限中国）
 * 
 * @author snowmeteor
 *
 */
public final class ChinaCityFinder {
	private static String cityArrayJson = "";

	static {
		try {
			InputStream inStream = ChinaCityFinder.class.getResourceAsStream("/city.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			StringBuilder builder = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			cityArrayJson = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class City {
		private String id;// 城市ID和中国天气网保持一致
		private String cityEn;
		private String cityZh;
		private String provinceEn;

		private String provinceZh;
		private String leaderEn;
		private String leaderZh;
		private double lat;
		private double lon;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCityEn() {
			return cityEn;
		}

		public void setCityEn(String cityEn) {
			this.cityEn = cityEn;
		}

		public String getCityZh() {
			return cityZh;
		}

		public void setCityZh(String cityZh) {
			this.cityZh = cityZh;
		}

		public String getProvinceEn() {
			return provinceEn;
		}

		public void setProvinceEn(String provinceEn) {
			this.provinceEn = provinceEn;
		}

		public String getProvinceZh() {
			return provinceZh;
		}

		public void setProvinceZh(String provinceZh) {
			this.provinceZh = provinceZh;
		}

		public String getLeaderEn() {
			return leaderEn;
		}

		public void setLeaderEn(String leaderEn) {
			this.leaderEn = leaderEn;
		}

		public String getLeaderZh() {
			return leaderZh;
		}

		public void setLeaderZh(String leaderZh) {
			this.leaderZh = leaderZh;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		@Override
		public String toString() {
			return "City [id=" + id + ", cityEn=" + cityEn + ", cityZh=" + cityZh + ", provinceEn=" + provinceEn
					+ ", provinceZh=" + provinceZh + ", leaderEn=" + leaderEn + ", leaderZh=" + leaderZh + ", lat="
					+ lat + ", lon=" + lon + "]";
		}

	}

	/**
	 * KDTree里的节点
	 *
	 */
	private static class Node {
		List<Point> pointList = new LinkedList<Point>();
		Node l = null;
		Node r = null;
	}

	/**
	 * 数据点
	 *
	 */
	private static class Point implements Comparable<Point> {
		public static int deep = 0;
		double[] x;

		@Override
		public String toString() {
			return "Point [x=" + Arrays.toString(x) + ", id=" + id + ", name=" + name + ", full_name=" + full_name
					+ ", pid=" + pid + ", len=" + len + "]";
		}

		int id;
		String name;
		String full_name;
		int pid;
		double len;

		public Point(double len) {
			this.len = len;
		}

		public Point(double[] d) {
			x = new double[d.length];
			for (int i = 0; i < d.length; i++) {
				x[i] = d[i];
			}
		}

		public Point(int id, String full_name, int pid, String name, double[] x) {
			this.id = id;
			this.full_name = full_name;
			this.pid = pid;
			this.name = name;
			this.x = x;
		}

		public int compareTo(Point o) {
			Point other = (Point) o;
			if (this.x[deep] == other.x[deep])
				return 0;
			if (this.x[deep] > other.x[deep])
				return 1;
			return -1;
		}
	}

	private static List<City> cities = null;
	private static Map<String, City> cityMap = new HashMap<>();

	static {
		Gson gson = new Gson();
		cities = gson.fromJson(cityArrayJson, new TypeToken<List<City>>() {
		}.getType());
		for (City city : cities) {
			cityMap.put(city.getId(), city);
		}
	}

	public static List<City> getChinaCites() {
		return cities;
	}

	public static int KDTCount = 0; // 统计在KDT 搜索的时候，计算了和几个点的距离
	public static Node root;
	public static int count_point = 0;// 样本点个数(经纬度点的总个数)
	public static List<Point> pointList;

	static {
		int xn = 2; // 样本点维数
		int deep = 0; // 轴
		// 准备数据
		pointList = new LinkedList<Point>();
		try {
			Iterator<City> cityIterable = cities.iterator();
			while (cityIterable.hasNext()) {
				City city = cityIterable.next();

				int id = Integer.parseInt(city.getId());
				String full_name = city.getCityZh();
				int pid = id;
				String name = city.getCityEn();
				double lnt = city.getLon();
				double lat = city.getLat();
				double[] d = new double[xn];
				d[0] = lnt;
				d[1] = lat;
				pointList.add(new Point(id, full_name, pid, name, d));
				count_point++;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		// build tree
		root = new Node();
		insert(root, pointList, deep);
	}

	/**
	 * build KDTree
	 * 
	 * @param root
	 * @param pointList
	 * @param deep
	 */
	private static void insert(Node root, List<Point> pointList, int deep) {
		int mid = pointList.size() / 2;

		// 排序后拿到中位数
		Point.deep = deep;
		Collections.sort(pointList);

		// 类似快排的方法拿到中位数
		int pl = mid;
		int pr = mid;
		while (pl >= 0 && pointList.get(pl).x[deep] == pointList.get(mid).x[deep])
			pl--;
		while (pr < pointList.size() && pointList.get(pr).x[deep] == pointList.get(mid).x[deep])
			pr++;
		List<Point> pointListLeft = pointList.subList(0, pl + 1);
		List<Point> pointListMid = pointList.subList(pl + 1, pr);
		List<Point> pointListRight = pointList.subList(pr, pointList.size());
		root.pointList = pointListMid;
		if (pointListLeft.size() > 0) {
			root.l = new Node();
			insert(root.l, pointListLeft, (deep + 1) % pointList.get(0).x.length);
		}
		if (pointListRight.size() > 0) {
			root.r = new Node();
			insert(root.r, pointListRight, (deep + 1) % pointList.get(0).x.length);
		}

	}

	// search the nearest point to p in KDTree
	private static Point query(Node root, Point p, Point best_p, int deep) {
		if (root == null)
			return best_p;
		double dist;
		if (root.l == null && root.r == null) {
			for (int i = 0; i < root.pointList.size(); i++) {
				KDTCount++;
				dist = getDist(root.pointList.get(i), p);
				if (dist < best_p.len) {
					best_p = root.pointList.get(i);
					best_p.len = dist;
				}
			}
			return best_p;
		}

		// left or right
		if (p.x[deep] <= root.pointList.get(0).x[deep]) {
			best_p = query(root.l, p, best_p, (deep + 1) % p.x.length);
		} else {
			best_p = query(root.r, p, best_p, (deep + 1) % p.x.length);
		}
		// cur
		for (int i = 0; i < root.pointList.size(); i++) {
			KDTCount++;
			dist = getDist(root.pointList.get(i), p);
			if (dist < best_p.len) {
				best_p = root.pointList.get(i);
				best_p.len = dist;
			}
		}
		// another side
		if (best_p.len >= Math.abs(p.x[deep] - root.pointList.get(0).x[deep])) {
			Point another_p = new Point(Double.MAX_VALUE);
			if (p.x[deep] <= root.pointList.get(0).x[deep]) {
				another_p = query(root.r, p, best_p, (deep + 1) % p.x.length);
			} else {
				another_p = query(root.l, p, best_p, (deep + 1) % p.x.length);
			}
			if (another_p.len < best_p.len) {
				best_p = another_p;
				best_p.len = another_p.len;
			}
		}
		return best_p;
	}

	/**
	 * 欧式距离
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private static double getDist(Point p1, Point p2) {
		double sum = 0;
		for (int i = 0; i < p1.x.length; i++) {
			sum += (p1.x[i] - p2.x[i]) * (p1.x[i] - p2.x[i]);
		}
		if (sum == 0)
			return Double.MAX_VALUE;
		return Math.sqrt(sum);
	}

	/**
	 * 根据经纬度获取城市ID
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static int getLocationId(double longitude, double latitude) {
		double[] f = { longitude, latitude };
		Point p = new Point(f);
		double min_dis = Double.MAX_VALUE;
		Point resultPoint = query(root, p, new Point(min_dis), 0);
		return resultPoint.id;
	}

	/**
	 * 根据经纬度获取城市信息
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static City getCity(double longitude, double latitude) {
		String id = getLocationId(longitude, latitude) + "";
		return cityMap.get(id);
	}
}
