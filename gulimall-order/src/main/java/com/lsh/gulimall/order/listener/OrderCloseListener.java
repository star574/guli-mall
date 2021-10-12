package com.lsh.gulimall.order.listener;

import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = {"order.release.queue"})
@Service
@Slf4j
public class OrderCloseListener {

	@Autowired
	OrderService orderService;

	@RabbitListener(queues = {"order.release.queue"})
	public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
		log.warn("收到过期订单信息 准备关闭订单{}", orderEntity);
		/*确认收到消息*/
		try {
			orderService.closeOrder(orderEntity);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
			log.warn("关闭订单失败 ,等待下次关闭 !");
			// 重新回到消息队列
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}

	}
}
