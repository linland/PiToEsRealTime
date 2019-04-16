package com.link.connect;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;


@Configuration
@EnableCaching // 启用缓存特性
public class JedisClusterConfig {

	@Value("${redis.address}")
	private String address;

	@Value("${redis.pool.max-active}")
	private int maxActive;

	@Value("${redis.pool.max-wait}")
	private long maxWaitMillis;

	@Value("${redis.pool.max-idle}")
	private int maxIdle;

	@Value("${redis.pool.min-idle}")
	private int minIdle;

	@Value("${redis.timeout}")
	private int timeout;

	@Value("${redis.commandTimeout}")
	private int commandTimeout;

	@Value("${redisCache}")
	private boolean redisCache;

	//	private static JedisCluster jedisCluster = null;

	@Bean
	public JedisCluster jedisCluster(){
			Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
			String [] clusterNode=address.split(",");
			for(String node : clusterNode) {
				String[] nodes = node.split(":");
				clusterNodes.add(new HostAndPort(nodes[0].trim(),Integer.parseInt(nodes[1].trim())));
			}
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxTotal(maxActive);//最大连接个数
			jedisPoolConfig.setMaxIdle(maxIdle);//最大空闲连接个数
			jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
			//		jedisPoolConfig.setMaxWaitMillis(timeout);//获取连接时的最大等待毫秒数，若超时则抛异常。-1代表不确定的毫秒数
			jedisPoolConfig.setTestOnBorrow(true);//获取连接时检测其有效性

		// 只有当jedisCluster为空时才实例化
		//        if (jedisCluster == null) {
		//            jedisCluster = new JedisCluster(clusterNodes,commandTimeout,jedisPoolConfig);
		//        }

		return  new JedisCluster(clusterNodes,commandTimeout,jedisPoolConfig);
	}
}
