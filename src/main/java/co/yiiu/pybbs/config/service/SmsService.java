package co.yiiu.pybbs.config.service;

import co.yiiu.pybbs.service.ISystemConfigService;
import co.yiiu.pybbs.util.JsonUtil;
//import com.aliyuncs.CommonRequest;
//import com.aliyuncs.CommonResponse;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;


@Component
@DependsOn("mybatisPlusConfig")
public class SmsService {

    private Logger log = LoggerFactory.getLogger(SmsService.class);

    @Resource
    private ISystemConfigService systemConfigService;

//    private IAcsClient client;
    private String signName;
    private String templateCode;
    private String regionId;

    private SmsService() {
    }

    // public IAcsClient instance() {
    //     if (client != null) return client;
    //     String accessKeyId = (String) systemConfigService.selectAllConfig().get("sms_access_key_id");
    //     String secret = (String) systemConfigService.selectAllConfig().get("sms_secret");
    //     signName = (String) systemConfigService.selectAllConfig().get("sms_sign_name");
    //     templateCode = (String) systemConfigService.selectAllConfig().get("sms_template_code");
    //     regionId = (String) systemConfigService.selectAllConfig().get("sms_region_id");
    //     if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(secret) || StringUtils.isEmpty(signName) ||
    //             StringUtils.isEmpty(templateCode) || StringUtils.isEmpty(regionId)) {
    //         return null;
    //     }
    //     DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
    //     IAcsClient client = new DefaultAcsClient(profile);
    //     this.client = client;
    //     return client;
    // }
    // Client初始化方法
private AsyncClient instance() {
    String accessKeyId = (String) systemConfigService.selectAllConfig().get("sms_access_key_id");
    String accessKeySecret = (String) systemConfigService.selectAllConfig().get("sms_secret");
    signName = (String) systemConfigService.selectAllConfig().get("sms_sign_name");
    templateCode = (String) systemConfigService.selectAllConfig().get("sms_template_code");
    regionId = (String) systemConfigService.selectAllConfig().get("sms_region_id");
    if (StringUtils.isEmpty(accessKeyId) || StringUtils.isEmpty(accessKeySecret) || StringUtils.isEmpty(signName) ||
            StringUtils.isEmpty(templateCode) || StringUtils.isEmpty(regionId)) {
        return null;
    }
    try {
        // 构建凭证
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // 构建配置
        ClientOverrideConfiguration configuration = ClientOverrideConfiguration.create()
                .setEndpointOverride("dysmsapi.aliyuncs.com");

        // 创建客户端
        return AsyncClient.builder()
                .region(regionId)
                .credentialsProvider(provider)
                .overrideConfiguration(configuration)
                .build();
                
    } catch (Exception e) {
        log.error("初始化短信客户端异常: " + e.getMessage(), e);
        return null;
    }
}

    // 发短信
    public boolean sendSms(String mobile, String code) {
        try {
            if (StringUtils.isEmpty(mobile)) return false;
            
            // 获取连接
            AsyncClient client = this.instance();
            if (client == null) return false;
    
            // 构建请求
            SendSmsRequest request = SendSmsRequest.builder()
                    .phoneNumbers(mobile)
                    .signName(signName)
                    .templateCode(templateCode)
                    .templateParam(String.format("{\"code\":\"%s\"}", code))
                    .build();
    
            // 发送短信并等待响应
            SendSmsResponse response = client.sendSms(request).get();
    
            // 处理响应
            if (response != null && response.getBody() != null 
                && "OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功 - 手机号: {}, 请求ID: {}", 
                    mobile, response.getBody().getRequestId());
                return true;
            }
            
            // 记录失败信息
            if (response != null && response.getBody() != null) {
                log.error("短信发送失败: code={}, message={}", 
                    response.getBody().getCode(), 
                    response.getBody().getMessage());
            }
            
        } catch (Exception e) {
            log.error("发送短信异常: " + e.getMessage(), e);
        }
        return false;
    }
}
