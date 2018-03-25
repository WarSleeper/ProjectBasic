package com.gemo.utils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import com.gemo.constant.SystemConstant;
import com.gemo.dto.interpreter.InterfaceResult;

import freemarker.template.Template;

/**
 * 普通工具类
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommonUtils {

	public static void sleep(long millisecond) {
		try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}

	public static String getExceptionStackTrace(Exception e) {
		String result;
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
			result = sw.toString();
		} catch (Exception ex) {
			result = "bad getErrorInfoFromException";
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (sw != null) {
				try {
					sw.close();
				} catch (Exception ei) {
					// TODO Auto-generated catch block
				}
			}
		}
		return result;
	}

	public static String getSqlByTemplate(Map root, String templateName) throws Exception {
		Template template = SystemConstant.freemarker.getTemplate(templateName, "UTF-8");
		StringWriter out = new StringWriter();
		template.process(root, out);
		out.flush();
		out.close();
		return out.toString();
	}

	public static InterfaceResult createInterfaceResult() {
		InterfaceResult interfaceResult = new InterfaceResult();
		interfaceResult.setVersion("1.0");
		interfaceResult.setContent(new ArrayList());
		return interfaceResult;
	}

	public static boolean isWrapClass(Class<?> clz) {
		try {
			return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	public static void sort(List<?> list, final Map<String, Integer> comparatorMap) {
		Comparator<Object> comparator = new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				for (Map.Entry<String, Integer> entry : comparatorMap.entrySet()) {
					int sort = 0;
					try {
						Comparable v1 = null;
						Comparable v2 = null;
						if (o1 instanceof Map) {
							v1 = (Comparable) ((Map) o1).get(entry.getKey());
							v2 = (Comparable) ((Map) o2).get(entry.getKey());
						} else {
							PropertyDescriptor pd1 = new PropertyDescriptor(entry.getKey(), o1.getClass());
							PropertyDescriptor pd2 = new PropertyDescriptor(entry.getKey(), o2.getClass());
							v1 = (Comparable) pd1.getReadMethod().invoke(o1);
							v2 = (Comparable) pd2.getReadMethod().invoke(o2);
						}
						if (entry.getValue() == 1) {
							if (v1.compareTo(v2) > 0) {
								sort = 1;
							} else if (v1.compareTo(v2) < 0) {
								sort = -1;
							}
						} else {
							if (v1.compareTo(v2) > 0) {
								sort = -1;
							} else if (v1.compareTo(v2) < 0) {
								sort = 1;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException(e);
					}
					if (sort != 0) {
						return sort;
					}
				}

				return 0;
			}

		};
		Collections.sort(list, comparator);
	}

	public static String md5(String source) {
		StringBuffer sb = new StringBuffer(32);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(source.getBytes("utf-8"));

			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
			}
		} catch (Exception e) {
			return null;
		}

		return sb.toString();
	}

	public static <T extends Object> T MapToBean(Set<Converter> converters, Class<T> cls, Map valueMap)
			throws Exception {
		T obj = cls.newInstance();
		BeanWrapper bw = new BeanWrapperImpl(obj);
		if (converters != null && converters.size() > 0) {
			GenericConversionService gcs = new GenericConversionService();
			for (Converter converter : converters) {
				gcs.addConverter(converter);
			}
			bw.setConversionService(gcs);
		}
		bw.setPropertyValues(valueMap);

		return obj;
	}

	public static Properties getPropertiesByResource(String resource) {
		InputStream inputStream = CommonUtils.class.getResourceAsStream(resource);
		Properties properties = new Properties();
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return properties;
	}

	public static void main(String[] args) {

	}
}
