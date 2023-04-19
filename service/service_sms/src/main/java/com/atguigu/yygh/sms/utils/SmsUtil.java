package com.atguigu.yygh.sms.utils;

import com.atguigu.yygh.vo.sms.SmsVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsUtil {

    /**
     * 腾讯云发送短信
     *
     * @param smsVo 手机号，模板id，发送的消息
     */
    public static boolean sendMsg(SmsVo smsVo) throws TencentCloudSDKException {
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        String secretId = "AKIDxod7VmxyqY7oHaBcu6BHDzc2KROE2oX8";
        String secretKey = "aHuNehaDl7Mar242whO2d9XnGfy9A4oP";

        Credential cred = new Credential(secretId, secretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        SmsClient client = new SmsClient(cred, "ap-beijing", clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        SendSmsRequest req = new SendSmsRequest();
        String[] phoneNumberSet1 = {"+86"+smsVo.getPhone()};
        req.setPhoneNumberSet(phoneNumberSet1);

        req.setSmsSdkAppId("1400813780");
        req.setSignName("巨山超力霸123公众号");
        req.setTemplateId(smsVo.getTemplateCode());

        //发送的消息
        req.setTemplateParamSet(smsVo.getParam());

        // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
        SendSmsResponse resp = client.SendSms(req);

        //解析返回结果
        SendStatus sendStatus = resp.getSendStatusSet()[0];
        if (sendStatus.getCode().equals("Ok")) {
            //成功
            return true;
        }

        //失败，记录错误日志
        log.error("发送短信出错：" + sendStatus.getMessage());
        return false;
    }
}
