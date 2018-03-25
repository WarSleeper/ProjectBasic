package com.gemo.service;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gemo.component.JsonObjectMapper;
import com.gemo.mvc.service.impl.CoreServiceImpl;

/**
 * 对外接口业务方法基类
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseBusinessService<DTO> extends CoreServiceImpl implements BusinessService {

	private static final Logger log = Logger.getLogger(BaseBusinessService.class);

	@Resource(name = "objectMapper")
	protected JsonObjectMapper objectMapper;
	// 标准日志转换格式
	protected final SimpleDateFormat standard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// 当前时间
	protected final Date currentTime = new Date();

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void handle(String jsonString, List content) throws Exception {
		// TODO Auto-generated method stub
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();

		Class<DTO> cls = (Class<DTO>) type.getActualTypeArguments()[0];

		DTO dto = objectMapper.readValue(jsonString, cls);

		log.debug("参数转化对象：" + dto);

		handle(dto, content);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public abstract void handle(DTO dto, List content) throws Exception;

}
