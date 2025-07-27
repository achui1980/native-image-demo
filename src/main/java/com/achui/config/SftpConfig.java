package com.achui.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SftpConfig {

    @Value("${app.sftp.pool.max-total}")
    private int maxTotal;

    @Value("${app.sftp.pool.max-idle}")
    private int maxIdle;

    @Value("${app.sftp.pool.min-idle}")
    private int minIdle;

    @Value("${app.sftp.pool.max-wait-millis}")
    private long maxWaitMillis;

    @Bean
    public GenericObjectPool<ChannelSftp> sftpPool() {
        GenericObjectPoolConfig<ChannelSftp> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);

        return new GenericObjectPool<>(new SftpChannelFactory(), config);
    }

    private static class SftpChannelFactory extends BasePooledObjectFactory<ChannelSftp> {

        @Override
        public ChannelSftp create() throws Exception {
            // 这里的连接参数将通过具体的SftpClient来设置
            return null;
        }

        @Override
        public PooledObject<ChannelSftp> wrap(ChannelSftp channelSftp) {
            return new DefaultPooledObject<>(channelSftp);
        }

        @Override
        public void destroyObject(PooledObject<ChannelSftp> p) throws Exception {
            ChannelSftp channelSftp = p.getObject();
            if (channelSftp != null) {
                Session session = channelSftp.getSession();
                if (session != null) {
                    session.disconnect();
                }
                channelSftp.disconnect();
            }
        }

        @Override
        public boolean validateObject(PooledObject<ChannelSftp> p) {
            ChannelSftp channelSftp = p.getObject();
            return channelSftp != null && channelSftp.isConnected() && !channelSftp.isClosed();
        }
    }
}