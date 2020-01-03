package zqit.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import zqit.server.entity.SysUser;
import zqit.server.mapper.SysUserMapper;
import zqit.server.util.JacksonUtil;

@RestController
@RequestMapping("/test/mybatis")
public class MybatisTestAPI {

	@Autowired
	SysUserMapper userMapper;
	
	@GetMapping("/a")
	public String a() throws JsonProcessingException {
		List<SysUser> list = userMapper.selectAll();
		return JacksonUtil.defaultInstance().pojo2json(list);
	}
	
}
