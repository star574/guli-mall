package com.lsh.gulimall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRabbitConfig {


	/**
	 * @return: MessageConverter
	 * @Description: 自定义Rabbit的MessageConverter
	 */
	@Bean
	public MessageConverter getMessageConverter() {
		/*容器中存在自定义的MessageConverter 就用容器中的 没有就用SimpleMessageConverter */
		return new Jackson2JsonMessageConverter();
	}

}
