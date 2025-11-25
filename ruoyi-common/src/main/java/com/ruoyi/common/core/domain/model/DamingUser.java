package com.ruoyi.common.core.domain.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 刷题用户对象 daming_user
 *
 * @author zww
 * @date 2024-10-18
 */
public class DamingUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户id */
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 用户密码 */
    @Excel(name = "用户密码")
    private String password;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    /** 头像地址 */
    @Excel(name = "头像地址")
    private String avatar;

    /** 微信OpenID */
    private String wxOpenId;

    /** 微信UnionID */
    private String wxUnionId;

    /** 删除标志 */
    private String delFlag;

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getNickName()
    {
        return nickName;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setWxOpenId(String wxOpenId)
    {
        this.wxOpenId = wxOpenId;
    }

    public String getWxOpenId()
    {
        return wxOpenId;
    }

    public void setWxUnionId(String wxUnionId)
    {
        this.wxUnionId = wxUnionId;
    }

    public String getWxUnionId()
    {
        return wxUnionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("password", getPassword())
            .append("nickName", getNickName())
            .append("avatar", getAvatar())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
