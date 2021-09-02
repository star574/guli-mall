package com.lsh.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.order.dao.OmsOrderItemDao;
import com.lsh.gulimall.order.entity.OmsOrderItemEntity;
import com.lsh.gulimall.order.service.OmsOrderItemService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("omsOrderItemService")
public class OmsOrderItemServiceImpl extends ServiceImpl<OmsOrderItemDao, OmsOrderItemEntity> implements OmsOrderItemService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OmsOrderItemEntity> page = this.page(
				new Query<OmsOrderItemEntity>().getPage(params),
				new QueryWrapper<OmsOrderItemEntity>()
		);

		return new PageUtils(page);
	}

	/**
	 * @param message
	 * @return: void
	 * @Description: 参数可以写以下类型
	 * 1.Message 原生消息类型 body messageProperties
	 * 2.发送的消息的java类型  HashMap<String, Object>
	 * 3.Channel 当前传输数据的通道
	 */
	@RabbitListener(queues = {"atguiguTest"})
	public void recieveMessage(Message message, HashMap<String, Object> map, Channel channel) {
		/*接受到消息.....(Body:'{"是否毕业":false,"姓名":"张三","年龄":18}' MessageProperties [headers={__ContentTypeId__=java.lang.Object, __KeyTypeId__=java.lang.Object, __TypeId__=java.util.HashMap}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=hello.java.exchange, receivedRoutingKey=atguiguTest, deliveryTag=1, consumerTag=amq.ctag-TkbCdCfxqt2S0luR9QTfvw, consumerQueue=atguiguTest]) */
		byte[] body = message.getBody(); //消息体
		MessageProperties messageProperties = message.getMessageProperties(); //消息头
		log.info("接受到原生消息....." + message + "    类型:" + message.getClass());

//		接受到指定类型消息.....{是否毕业=false, 姓名=张三, 年龄=18}    类型:class java.util.HashMap
		log.info("接受到指定类型消息....." + map + "    类型:" + map.getClass());
	}


}