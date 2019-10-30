package org.ky.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 辅助Jsoup支持xpath
 * 
 * @author snowmeteor
 *
 */
public class JsoupHelper {

	/**
	 * 根据xpath从html中获取指定的文本串
	 * 
	 * @param html
	 * @param xpath
	 * @return
	 */
	public static List<String> fetchTextList(String html, String xpath) {
		List<String> valueList = new ArrayList<>();
		if (StringUtils.isBlank(xpath) || StringUtils.isBlank(html)) {
			return valueList;
		}

		try {
			HtmlCleaner hCleaner = new HtmlCleaner();
			TagNode tNode = hCleaner.clean(html);
			Document dom = new DomSerializer(new CleanerProperties()).createDOM(tNode);
			XPath xPath = XPathFactory.newInstance().newXPath();
			Object result = xPath.evaluate(xpath, dom, XPathConstants.NODESET);
			if (result instanceof NodeList) {
				NodeList nodeList = (NodeList) result;
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					String value = node.getNodeValue();
					value = value == null ? node.getTextContent() : value;
					if ("#".equals(value)) {
						continue;
					}
					valueList.add(value);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return valueList;
	}

	/**
	 * 采用HTML cleaner提取单个文本
	 * 
	 * @param html
	 * @param xpath
	 * @return
	 */
	public static String fetchText(String html, String xpath) {
		List<String> valueList = fetchTextList(html, xpath);
		return CollectionUtils.isEmpty(valueList) ? "" : valueList.get(0);
	}

	/**
	 * 获取xpath下的nodelist
	 * 
	 * @param url
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static NodeList fecthNode(String url, String xpath) throws Exception {
		String html = null;
		try {
			Connection connect = Jsoup.connect(url);
			html = connect.get().body().html();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		HtmlCleaner hc = new HtmlCleaner();
		TagNode tn = hc.clean(html);
		Document dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
		XPath xPath = XPathFactory.newInstance().newXPath();

		return (NodeList) xPath.evaluate(xpath, dom, XPathConstants.NODESET);
	}

	/**
	 * 获取xpath下的a标签的文本值及href属性值
	 * 
	 * @param url
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> fecthByMap(String url, String xpath) throws Exception {
		Map<String, String> nodeMap = new LinkedHashMap<>();

		Object result = fecthNode(url, xpath);

		NodeList nodeList = (NodeList) result;

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}
			nodeMap.put(node.getTextContent(),
					node.getAttributes().getNamedItem("href") != null
							? node.getAttributes().getNamedItem("href").getTextContent()
							: "");

			System.out.println(node.getTextContent() + " : " + node.getAttributes().getNamedItem("href"));
		}

		return nodeMap;
	}

	/**
	 * 获取xpath下的某个属性值
	 * 
	 * @param url
	 * @param xpath
	 * @param attr
	 * @return
	 * @throws Exception
	 */
	public static List<String> fecthAttr(String url, String xpath, String attr) throws Exception {
		List<String> list = new ArrayList<>();

		Object result = fecthNode(url, xpath);

		NodeList nodeList = (NodeList) result;

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}
			list.add(node.getAttributes().getNamedItem(attr).getTextContent());

		}

		return list;
	}
}