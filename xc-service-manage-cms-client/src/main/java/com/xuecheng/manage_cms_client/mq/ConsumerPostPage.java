package com.xuecheng.manage_cms_client.mq;

//import com.rabbitmq.client.Channel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.manage_cms_client.service.CmsClientService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :whywhathow
 * Consumer:  处理RabbitMQ发来的页面发布请求,
 **/
@Component
public class ConsumerPostPage {

    private static  final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    /**
     *  cms_client 将gridFs中静态html文件download到自己的nginx服务器上
     * @param msg JSON 转换,传递数据是cmsPage
//     * @param message
//     * @param channel
     */
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public  void  dealWithPostPage(String msg, Message message, Channel channel){
        if(StringUtils.isEmpty(msg)){
            LOGGER.error("error in cmsClient.postPage, msg is :{}",msg);
            RuntimeExceptionCast.cast(CmsCode.CMS_POSTPAGE_PAGEISNULL);
        }
        CmsPage page = JSON.parseObject(msg, CmsPage.class);
        service.PostPage(page);
    }
    @Autowired
    CmsClientService service ;
}
