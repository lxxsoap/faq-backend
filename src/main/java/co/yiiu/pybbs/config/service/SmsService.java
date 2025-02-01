package co.yiiu.pybbs.config.service;

import co.yiiu.pybbs.service.ISystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import darabonba.core.client.ClientOverrideConfiguration;

@Component
@DependsOn("mybatisPlusConfig")
public class SmsService implements DisposableBean {

    private Logger log = LoggerFactory.getLogger(SmsService.class);

    @Resource
    private ISystemConfigService systemConfigService;

    private String signName;
    private String templateCode;
    private String regionId;
    private AsyncClient client;

     
    private final Cache<String, Long> lastSendTimeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)  // 设置5分钟后自动过期
            .maximumSize(10000)  // 设置最大缓存条数，防止无限增长
            .build();
    private static final long MIN_INTERVAL = 60000; // 60秒内不能重复发送

    private SmsService() {
    }

    @PreDestroy
    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.close();
            log.info("SMS client closed successfully");
        }
    }

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

    public boolean sendSms(String mobile, String code) {
        // 检查发送频率
        Long lastSendTime = lastSendTimeCache.getIfPresent(mobile);
        long now = System.currentTimeMillis();
        if (lastSendTime != null && now - lastSendTime < MIN_INTERVAL) {
            log.warn("发送过于频繁，请稍后再试 - 手机号: {}", mobile);
            return false;
        }

        boolean result = doSendSms(mobile, code);
        if (result) {
            lastSendTimeCache.put(mobile, now);
        }
        return result;
    }

    // 发短信
    public boolean doSendSms(String mobile, String code) {
        try {
            if (StringUtils.isEmpty(mobile))
                return false;

            // 获取连接
            AsyncClient client = this.instance();
            if (client == null)
                return false;

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
