package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lee
 * @description 语音项目里面包含的错误提示
 */
@AllArgsConstructor
public enum BusinessExceptionEnum {
    DEMO_MOBILE_NOT_NULL("手机号不能为空！！!"),

    SMS_CODE_TOO_FREQUENT("短信请求过于频繁"),
    SMS_CODE_SEND_ERROR("短信发送失败，请联系管理员或稍候重试"),
    SMS_CODE_ERROR("短信验证码不正确"),
    SMS_CODE_EXPIRED("短信验证码未发送或已过期，请重新发送短信"),

    MEMBER_MOBILE_HAD_REGISTER("手机号已注册"),
    MEMBER_MOBILE_NOT_REGISTER("手机号未注册"),
    MEMBER_LOGIN_ERROR("手机号未注册或密码错误"),

    USER_LOGIN_ERROR("登录名未注册或密码错误"),

    IMAGE_CODE_ERROR("图片验证码不正确或已过期"),

    FILETRANS_CAL_AMOUNT_ERROR("收费金额计算异常"),
    FILETRANS_VIDEO_INFO_NOT_FOUND("获取视频信息为空"),

    ALIPAY_ERROR("调用支付宝接口失败"),
    PAY_ERROR("调用支付接口失败"),

    FILETRANS_TRANS_ERROR("录音文件识别请求失败"),

    GEN_SUBTITLE_ERROR("生成字幕文件失败"),
    ;

    @Getter
    private final String desc;
}
