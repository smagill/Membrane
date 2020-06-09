package cloud.celldata.membrane.mapper;



import cloud.celldata.membrane.pojo.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户mapper
 */
public interface UserMapper {

    /**
     * 根据用户信息查询用户
     * @param userEntity 用户实体
     * @return 用户列表
     */
    List<UserEntity> getUserByUser(UserEntity userEntity);

    /**
     * 根据角色id查询所有用户id
     * @param roleId 角色id
     * @return 用户id列表
     */
    List<Integer> selectUserByRoleId(@Param("roleId") Integer roleId);



}
