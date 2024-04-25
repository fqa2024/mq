package com.example.demo.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Configuration
public class Config {

    /**
     * 初始化RabbitTemplate
     *
     * @param connectionFactory 默认连接工厂，使用配置文件中的配置，如需额外配置，可手动创建连接工厂的Bean
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate createTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        //消息发布回调，配置文件中publisher-confirm-type设置为CORRELATED时，开启发布确认模式
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (Objects.isNull(correlationData)) {
                return;
            }
            String id = correlationData.getId();
            if (ack) {
                System.out.println(id + " is success!");
            } else {
                System.out.println(id + " is false!" + cause);
            }
        });
        //路由失败的回调----这里只关注路由失败的
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            Message message = returnedMessage.getMessage();
            String exchange = returnedMessage.getExchange();
            String replyText = returnedMessage.getReplyText();
            String routingKey = returnedMessage.getRoutingKey();
            System.out.println("无法路由的消息，需要考虑另外处理。");
            System.out.println("Returned replyText：" + replyText);
            System.out.println("Returned exchange：" + exchange);
            System.out.println("Returned routingKey：" + routingKey);
            String msgJson = new String(message.getBody());
            System.out.println("Returned Message：" + msgJson);
        });
        return rabbitTemplate;
    }

    /**
     * 用指定的路由键绑定交换器与队列
     *
     * @return
     */
    @Bean
    public Binding bindingExchange() {
        return BindingBuilder.bind(queue()).to(createDirectExchange()).with("an.queue");
    }

    /**
     * 新建队列
     *
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue("an.queue");
    }

    /**
     * 新建交换器
     *
     * @return
     */
    @Bean
    public DirectExchange createDirectExchange() {
        return ExchangeBuilder.directExchange("an.direct").build();
    }

    @Bean
    public DirectExchange createDlxExchange() {
        return ExchangeBuilder.directExchange("exchange-dlx").build();
    }

    @Bean
    public Queue queueDLX() {
        return new Queue("queue_dlx");
    }

    @Bean
    public Binding bindingDlxExchange() {
        return BindingBuilder
                .bind(queueDLX())
                .to(createDlxExchange()).with("dlx.route");
    }


    @Bean
    public Queue deadQueue() {
        Map<String, Object> arguments = new HashMap<>();
        // 所有消息存活时间，时间单位是毫秒,30秒没消费，-》死信
        arguments.put("x-message-ttl", 30 * 1000);
        // 指定死信消息转发的路由，即发送到该队列的消息成为死信后，会转发到exchange-dlx交换器所绑定的路由键为dlx.route的队列
        arguments.put("x-dead-letter-exchange", "exchange-dlx");
        arguments.put("x-dead-letter-routing-key", "dlx.route");
        return new Queue("dead.queue", true, false, false, arguments);
    }
}
