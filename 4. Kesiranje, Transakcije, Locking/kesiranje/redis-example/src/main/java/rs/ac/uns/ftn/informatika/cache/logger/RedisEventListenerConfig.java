package rs.ac.uns.ftn.informatika.cache.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


@Configuration
public class RedisEventListenerConfig {

	private static final Logger LOG = LoggerFactory.getLogger(RedisEventListenerConfig.class);

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(LettuceConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);

		// Listen for EXPIRED events
		container.addMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] pattern) {
				String key = new String(message.getBody());
				LOG.info(String.format("Redis Cache Event -> EXPIRED: Key = %s", key));
			}
		}, new PatternTopic("__keyevent@*__:expired"));

		// Listen for DELETED/EVICTED events
		container.addMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] pattern) {
				String key = new String(message.getBody());
				LOG.info(String.format("Redis Cache Event -> EVICTED/REMOVED: Key = %s", key));
			}
		}, new PatternTopic("__keyevent@*__:del"));

		return container;
	}
}