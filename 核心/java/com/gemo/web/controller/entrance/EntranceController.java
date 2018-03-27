package com.gemo.web.controller.entrance;

import java.io.File;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gemo.constant.EMSConstant.Result;
import com.gemo.constant.SystemConstant;
import com.gemo.dto.interpreter.InterfaceResult;
import com.gemo.mvc.service.CoreService;
import com.gemo.service.BusinessService;
import com.gemo.utils.CommonUtils;
import com.gemo.web.controller.entrance.dto.FileConfigDTO;

/**
 * 统一对外接口
 */
@Scope("prototype")
@Controller("entranceController")
@RequestMapping("/entrance")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntranceController {

	private static final Logger log = Logger.getLogger(EntranceController.class);

	public static final String HANDLE_TYPE = "handleType";
	public static final String JSON_STRING = "jsonString";

	@Resource(name = "coreService")
	protected CoreService coreService;
	@Resource
	protected ApplicationContext context;

	@RequestMapping(value = "/unifier/download")
	public ResponseEntity<byte[]> unifierDownload(@RequestParam(value = "resource", required = true) String resource)
			throws Exception {

		if (resource == null || "".equals(resource)) {
			throw new Exception("resource 不能为空！");
		}

		Properties properties = new Properties();
		properties.load(EntranceController.class.getResourceAsStream("/config/system.properties"));
		String fileStorageDirectory = properties.getProperty("file.storage.directory");

		String filePath = fileStorageDirectory + "/" + resource;
		File f = new File(filePath);

		// 避免中文及URL特殊字符无法正常显示
		String fileName = "name.xlsx";
		fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);

		byte[] body = FileUtils.readFileToByteArray(f);

		ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(body, headers, HttpStatus.CREATED);

		return responseEntity;
	}

	@RequestMapping(value = "/unifier/upload")
	@ResponseBody
	public InterfaceResult unifierUpload(@RequestParam(value = JSON_STRING, required = true) String jsonString,
			@RequestParam(value = "file", required = true) MultipartFile[] files) throws Exception {
		InterfaceResult interfaceResult = CommonUtils.createInterfaceResult();
		try {
			jsonString = URLDecoder.decode(jsonString, "UTF-8");
			System.out.println(jsonString);
			List<FileConfigDTO> configList = SystemConstant.jsonMapper.readValue(jsonString,
					new TypeReference<List<FileConfigDTO>>() {
					});
			if (files.length != configList.size()) {
				throw new Exception("配置文件数：" + configList.size() + "，接收文件数：" + files.length + " 不一致！");
			}
			for (int i = 0; i < files.length; i++) {
				MultipartFile file = files[i];
				String originalFilename = file.getOriginalFilename();

				FileConfigDTO fileConfig = configList.get(i);
				String suffix = fileConfig.getFileSuffix();
				String name = fileConfig.getFileName();
				String subdirectory = fileConfig.getSubdirectory();

				if ((suffix == null || "".equals(suffix)) && originalFilename.indexOf(".") != -1) {
					int index = originalFilename.lastIndexOf(".");
					suffix = originalFilename.substring(index + 1);
				}
				if ((name == null || "".equals(name)) && originalFilename.indexOf(".") != -1) {
					int index = originalFilename.lastIndexOf(".");
					name = originalFilename.substring(0, index);
				} else if (name != null && !"".equals(name) && name.indexOf(".") != -1) {
					int index = name.lastIndexOf(".");
					suffix = name.substring(index + 1);
					name = name.substring(0, index);
				}

				if (subdirectory == null || "".equals(subdirectory)) {
					throw new Exception("subdirectory 不能为空！");
				}
				if (!subdirectory.startsWith("/")) {
					throw new Exception("subdirectory 必须以/开头！");
				}

				Properties properties = new Properties();
				properties.load(EntranceController.class.getResourceAsStream("/config/system.properties"));
				String fileStorageDirectory = properties.getProperty("file.storage.directory");

				String directory = fileStorageDirectory + subdirectory;
				String filePath = directory;
				File f = new File(filePath);
				FileUtils.writeByteArrayToFile(f, file.getBytes());
			}

			interfaceResult.setResult(Result.SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			interfaceResult.setResult(Result.FAILURE);
			interfaceResult.setReason(CommonUtils.getExceptionStackTrace(e));
			interfaceResult.getContent().clear();
			e.printStackTrace();
		}

		return interfaceResult;
	}

	@RequestMapping(value = "/unifier/{" + HANDLE_TYPE + "}")
	@ResponseBody
	public InterfaceResult unifier(@PathVariable(HANDLE_TYPE) String handleType,
			@RequestParam(value = JSON_STRING) String jsonString) throws Exception {

		Long start = null;
		String uuid = UUID.randomUUID().toString();
		String ip = getIpAddr();
		log.info("IP:" + ip + "访问接口：" + handleType + "[" + uuid + "] 开始...... ");
		start = System.currentTimeMillis();

		InterfaceResult interfaceResult = CommonUtils.createInterfaceResult();

		try {
			BusinessService businessService = (BusinessService) context.getBean(handleType);
			jsonString = URLDecoder.decode(jsonString, "UTF-8");
			businessService.handle(jsonString, interfaceResult.getContent());
			interfaceResult.setResult(Result.SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			interfaceResult.setResult(Result.FAILURE);
			Map reason = new LinkedHashMap<>();
			String message = e.getMessage();
			if (message == null || "null".equals(message) || message.indexOf("Index") != -1) {
				reason.put("message", "后台系统逻辑错误！");
			} else {
				reason.put("message", message);
			}

			String error = CommonUtils.getExceptionStackTrace(e);
			reason.put("stackTrace", error);
			interfaceResult.setReason(SystemConstant.jsonMapper.writeValueAsString(reason));
			interfaceResult.getContent().clear();
			e.printStackTrace();

		}

		long end = System.currentTimeMillis();
		long timeInterval = (end - start) / 1000;

		String result = "成功！";
		if (interfaceResult.getResult().equals(Result.FAILURE)) {
			result = "失败！";
		}

		log.info("IP:" + ip + "访问接口：" + handleType + "[" + uuid + "] 结束-" + result + "。历时：" + timeInterval + " 秒");

		if (timeInterval > 4) {
			log.info("接口：" + handleType + "；参数：" + jsonString);
		}

		return interfaceResult;
	}

	@RequestMapping(value = "/upload")
	@ResponseBody
	public InterfaceResult upload(@RequestParam(value = JSON_STRING, required = true) String jsonString,
			@RequestParam(value = "file", required = true) MultipartFile[] files) throws Exception {
		InterfaceResult interfaceResult = CommonUtils.createInterfaceResult();
		try {
			jsonString = URLDecoder.decode(jsonString, "UTF-8");
			List<FileConfigDTO> configList = SystemConstant.jsonMapper.readValue(jsonString,
					new TypeReference<List<FileConfigDTO>>() {
					});
			if (files.length != configList.size()) {
				throw new Exception("配置文件数：" + configList.size() + "，接收文件数：" + files.length + " 不一致！");
			}
			for (int i = 0; i < files.length; i++) {
				MultipartFile file = files[i];
				String originalFilename = file.getOriginalFilename();

				FileConfigDTO fileConfig = configList.get(i);
				String suffix = fileConfig.getFileSuffix();
				String name = fileConfig.getFileName();
				String subdirectory = fileConfig.getSubdirectory();

				if ((suffix == null || "".equals(suffix)) && originalFilename.indexOf(".") != -1) {
					int index = originalFilename.lastIndexOf(".");
					suffix = originalFilename.substring(index + 1);
				}
				if ((name == null || "".equals(name)) && originalFilename.indexOf(".") != -1) {
					int index = originalFilename.lastIndexOf(".");
					name = originalFilename.substring(0, index);
				} else if (name != null && !"".equals(name) && name.indexOf(".") != -1) {
					int index = name.lastIndexOf(".");
					suffix = name.substring(index + 1);
					name = name.substring(0, index);
				}

				if (subdirectory == null || "".equals(subdirectory)) {
					throw new Exception("subdirectory 不能为空！");
				}
				if (!subdirectory.startsWith("/")) {
					throw new Exception("subdirectory 必须以/开头！");
				}

				Properties properties = new Properties();
				properties.load(EntranceController.class.getResourceAsStream("/config/system.properties"));
				String fileStorageDirectory = properties.getProperty("file.storage.directory");

				String directory = fileStorageDirectory + subdirectory;
				String filePath = directory;
				File f = new File(filePath);
				FileUtils.writeByteArrayToFile(f, file.getBytes());
			}

			interfaceResult.setResult(Result.SUCCESS);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			interfaceResult.setResult(Result.FAILURE);
			interfaceResult.setReason(CommonUtils.getExceptionStackTrace(e));
			interfaceResult.getContent().clear();
			e.printStackTrace();
		}

		return interfaceResult;
	}

	@RequestMapping(value = "/download")
	public ResponseEntity<byte[]> download(@RequestParam(value = "resource", required = true) String resource)
			throws Exception {

		if (resource == null || "".equals(resource)) {
			throw new Exception("resource 不能为空！");
		}

		Properties properties = new Properties();
		properties.load(EntranceController.class.getResourceAsStream("/config/system.properties"));
		String fileStorageDirectory = properties.getProperty("file.storage.directory");

		String filePath = fileStorageDirectory;
		File f = new File(filePath);

		// 避免中文及URL特殊字符无法正常显示
		String fileName = "";
		// fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		fileName = URLEncoder.encode(fileName, "UTF-8");
		fileName = fileName.replace("+", "%20");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);

		byte[] body = FileUtils.readFileToByteArray(f);

		ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(body, headers, HttpStatus.CREATED);

		return responseEntity;
	}

	private String getIpAddr() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ip != null && ip.length() > 15) {
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}
}
