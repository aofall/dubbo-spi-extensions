/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.redis.jedis;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.remoting.redis.RedisClient;
import org.apache.dubbo.remoting.redis.support.AbstractRedisClient;

import redis.clients.jedis.*;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.dubbo.common.constants.CommonConstants.COMMA_SPLIT_PATTERN;

public class ClusterRedisClient extends AbstractRedisClient implements RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(ClusterRedisClient.class);

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final int DEFAULT_SO_TIMEOUT = 2000;

    private static final int DEFAULT_MAX_ATTEMPTS = 5;

    private final JedisCluster jedisCluster;
    private Pattern COLON_SPLIT_PATTERN = Pattern.compile("\\s*[:]+\\s*");

    public ClusterRedisClient(URL url) {
        super(url);
        Set<HostAndPort> nodes = getNodes(url);
        if (url.hasParameter("db.index")) {
            logger.warn("Redis Cluster does not support multiple databases, the SELECT command is not allowed. So the setting of db.index will not be effect");
        }
        jedisCluster = new JedisCluster(nodes, url.getParameter("connection.timeout", DEFAULT_TIMEOUT),
                url.getParameter("so.timeout", DEFAULT_SO_TIMEOUT), url.getParameter("max.attempts", DEFAULT_MAX_ATTEMPTS),
                url.getPassword(), getPoolConfig());
    }

    @Override
    public Long hset(String key, String field, String value) {
        return jedisCluster.hset(key, field, value);
    }

    @Override
    public Long publish(String channel, String message) {
        return jedisCluster.publish(channel, message);
    }

    @Override
    public boolean isConnected() {
        Map<String, ConnectionPool> poolMap = jedisCluster.getClusterNodes();
        for (ConnectionPool jedisPool : poolMap.values()) {
            Connection jedis = jedisPool.getResource();
            if (jedis.isConnected()) {
                jedisPool.returnResource(jedis);
                return true;
            } else {
                jedisPool.returnResource(jedis);
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        jedisCluster.close();
    }

    @Override
    public Long hdel(String key, String... fields) {
        return jedisCluster.hdel(key, fields);
    }

    @Override
    public Set<String> scan(String pattern) {

        Map<String, ConnectionPool> nodes = jedisCluster.getClusterNodes();
        Set<String> result = new HashSet<>();
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams params = new ScanParams();
        params.match(pattern);
        while (true) {
            ScanResult<String> scanResult = jedisCluster.scan(cursor, params);
            List<String> list = scanResult.getResult();
            if (CollectionUtils.isNotEmpty(list)) {
                result.addAll(list);
            }
            if (ScanParams.SCAN_POINTER_START.equals(scanResult.getCursor())) {
                break;
            }
            cursor = scanResult.getCursor();
        }
        return result;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return jedisCluster.hgetAll(key);
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        jedisCluster.psubscribe(jedisPubSub, patterns);
    }

    @Override
    public void disconnect() {
        jedisCluster.close();
    }

    @Override
    public void close() {
        jedisCluster.close();
    }

    private Set<HostAndPort> getNodes(URL url) {
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        hostAndPorts.add(new HostAndPort(url.getHost(), url.getPort()));
        String backupAddresses = url.getBackupAddress(6379);
        String[] nodes = StringUtils.isEmpty(backupAddresses) ? new String[0] : COMMA_SPLIT_PATTERN.split(backupAddresses);
        for (String node : nodes) {
            String[] hostAndPort = COLON_SPLIT_PATTERN.split(node);
            hostAndPorts.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        return hostAndPorts;
    }
}
