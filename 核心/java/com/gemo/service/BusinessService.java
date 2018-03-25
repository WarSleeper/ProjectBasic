package com.gemo.service;

import java.util.List;

import com.gemo.mvc.service.CoreService;

/**
 * 对外接口业务方法接口
 */
@SuppressWarnings("rawtypes")
public interface BusinessService extends CoreService {

	public void handle(String jsonString, List content) throws Exception;

}
