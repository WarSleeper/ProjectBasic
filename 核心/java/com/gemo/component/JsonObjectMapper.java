package com.gemo.component;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * jackson自定义ObjectMapper 用于特定规则初始化
 */
@Component("objectMapper")
public class JsonObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = 5151674957904164934L;

	@PostConstruct
	private void init() {
		this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
}
