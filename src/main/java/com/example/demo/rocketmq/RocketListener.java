package com.example.demo.rocketmq;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
@RocketMQMessageListener(topic = "${rocketmq.consumer.topic}",consumerGroup = "${rocketmq.consumer.group}",messageModel = MessageModel.CLUSTERING)
public class RocketListener implements RocketMQListener<MessageExt> {
    private static final Logger logger = LoggerFactory.getLogger(RocketListener.class);
    @Override
    public void onMessage(MessageExt messageExt) {
        //1.解析消息内容
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        long lBorn =messageExt.getBornTimestamp();
        long lStore =messageExt.getStoreTimestamp();
        System.out.println("Rocket-Consumer-Receiver : " + body  +",BornTime:"+ stampToTime(lBorn)+",StoreTime:"+ stampToTime(lStore)+"");
    }

    public static String stampToTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //将时间戳转换为时间
        Date date = new Date(time);
        //将时间调整为yyyy-MM-dd HH:mm:ss时间样式
        return simpleDateFormat.format(date);
    }
}
