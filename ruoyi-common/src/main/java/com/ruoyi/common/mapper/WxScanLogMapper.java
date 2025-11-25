package com.ruoyi.common.mapper;

import com.ruoyi.common.domain.WxScanLog;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 微信扫码记录Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface WxScanLogMapper {
    
    /**
     * 插入扫码记录
     */
    @Insert("INSERT INTO wx_scan_log(scene_str, scene_id, qrcode_url, qrcode_image_url, session_id, " +
            "ip_address, user_agent, scan_status, redirect_url, create_time, expire_time) " +
            "VALUES(#{sceneStr}, #{sceneId}, #{qrcodeUrl}, #{qrcodeImageUrl}, #{sessionId}, " +
            "#{ipAddress}, #{userAgent}, #{scanStatus}, #{redirectUrl}, #{createTime}, #{expireTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WxScanLog scanLog);
    
    /**
     * 根据场景值字符串查询
     */
    @Select("SELECT * FROM wx_scan_log WHERE scene_str = #{sceneStr}")
    @Results(id = "wxScanLogResultMap", value = {
            @Result(column = "scene_str", property = "sceneStr"),
            @Result(column = "scene_id", property = "sceneId"),
            @Result(column = "qrcode_url", property = "qrcodeUrl"),
            @Result(column = "qrcode_image_url", property = "qrcodeImageUrl"),
            @Result(column = "session_id", property = "sessionId"),
            @Result(column = "ip_address", property = "ipAddress"),
            @Result(column = "user_agent", property = "userAgent"),
            @Result(column = "scan_status", property = "scanStatus"),
            @Result(column = "wx_open_id", property = "wxOpenId"),
            @Result(column = "wx_union_id", property = "wxUnionId"),
            @Result(column = "wx_nickname", property = "wxNickname"),
            @Result(column = "daming_user_id", property = "damingUserId"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "scan_time", property = "scanTime"),
            @Result(column = "auth_time", property = "authTime"),
            @Result(column = "login_time", property = "loginTime"),
            @Result(column = "expire_time", property = "expireTime"),
            @Result(column = "redirect_url", property = "redirectUrl"),
            @Result(column = "error_msg", property = "errorMsg")
    })
    WxScanLog selectBySceneStr(String sceneStr);
    
    /**
     * 根据场景值数字查询
     */
    @Select("SELECT * FROM wx_scan_log WHERE scene_id = #{sceneId}")
    @ResultMap("wxScanLogResultMap")
    WxScanLog selectBySceneId(Integer sceneId);
    
    /**
     * 根据会话ID查询最新记录
     */
    @Select("SELECT * FROM wx_scan_log WHERE session_id = #{sessionId} " +
            "ORDER BY create_time DESC LIMIT 1")
    @ResultMap("wxScanLogResultMap")
    WxScanLog selectLatestBySessionId(String sessionId);
    
    /**
     * 更新扫码状态为已扫码
     */
    @Update("UPDATE wx_scan_log SET scan_status = 1, scan_time = #{scanTime}, " +
            "wx_open_id = #{wxOpenId}, wx_union_id = #{wxUnionId}, wx_nickname = #{wxNickname} " +
            "WHERE scene_str = #{sceneStr}")
    int updateToScanned(@Param("sceneStr") String sceneStr,
                        @Param("scanTime") Date scanTime,
                        @Param("wxOpenId") String wxOpenId,
                        @Param("wxUnionId") String wxUnionId,
                        @Param("wxNickname") String wxNickname);
    
    /**
     * 更新扫码状态为已授权
     */
    @Update("UPDATE wx_scan_log SET scan_status = 2, auth_time = #{authTime} " +
            "WHERE scene_str = #{sceneStr}")
    int updateToAuthorized(@Param("sceneStr") String sceneStr,
                           @Param("authTime") Date authTime);
    
    /**
     * 更新扫码状态为登录成功
     */
    @Update("UPDATE wx_scan_log SET scan_status = 3, auth_time = #{authTime}, " +
            "login_time = #{loginTime}, daming_user_id = #{damingUserId} " +
            "WHERE scene_str = #{sceneStr}")
    int updateToSuccess(@Param("sceneStr") String sceneStr,
                        @Param("authTime") Date authTime,
                        @Param("loginTime") Date loginTime,
                        @Param("damingUserId") Integer damingUserId);
    
    /**
     * 更新扫码状态为已过期
     */
    @Update("UPDATE wx_scan_log SET scan_status = 4 WHERE scene_str = #{sceneStr}")
    int updateToExpired(String sceneStr);
    
    /**
     * 更新扫码状态为已取消
     */
    @Update("UPDATE wx_scan_log SET scan_status = 5 WHERE scene_str = #{sceneStr}")
    int updateToCancelled(String sceneStr);
    
    /**
     * 批量更新过期的二维码状态
     */
    @Update("UPDATE wx_scan_log SET scan_status = 4 " +
            "WHERE scan_status IN (0, 1, 2) AND expire_time < NOW()")
    int batchUpdateExpired();
    
    /**
     * 根据OpenID查询扫码历史
     */
    @Select("SELECT * FROM wx_scan_log WHERE wx_open_id = #{wxOpenId} " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    @ResultMap("wxScanLogResultMap")
    List<WxScanLog> selectByOpenId(@Param("wxOpenId") String wxOpenId,
                                    @Param("limit") int limit);
    
    /**
     * 删除指定天数前的记录
     */
    @Delete("DELETE FROM wx_scan_log WHERE create_time < #{beforeDate}")
    int deleteByCreateTimeBefore(Date beforeDate);
    
    /**
     * 统计各状态的数量
     */
    @Select("SELECT scan_status, COUNT(*) as count FROM wx_scan_log " +
            "WHERE create_time >= #{startTime} AND create_time <= #{endTime} " +
            "GROUP BY scan_status")
    List<java.util.Map<String, Object>> countByStatus(@Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime);
}
