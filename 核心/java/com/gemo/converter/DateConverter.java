package com.gemo.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date> {

	private String pattern;
	
	public DateConverter(String pattern){
		this.pattern = pattern;
	}
	
	@Override
	public Date convert(String source) {
		// TODO Auto-generated method stub
		if(source==null || "".equals(source)){
			return null;
		}else{
			Date target;
			try {
				target = new SimpleDateFormat(pattern).parse(source);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
			return target;
		}
	}

}
