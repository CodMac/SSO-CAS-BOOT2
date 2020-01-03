package zqit.server.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import zqit.server.entity.SysUser;

/**
 * 
 * @author mc
 */
@Mapper
public interface SysUserMapper {
	SysUser selectOne(SysUser one);
	List<SysUser> selectAll();
}
