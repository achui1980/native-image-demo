package com.achui.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public class PasswordSftpClient implements SftpClient {
    private static final Logger logger = LoggerFactory.getLogger(PasswordSftpClient.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private ChannelSftp channelSftp;
    private Session session;

    @Autowired
    private GenericObjectPool<ChannelSftp> sftpPool;

    public PasswordSftpClient(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() throws JSchException {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            logger.info("SFTP connection established to {}:{}", host, port);
        } catch (JSchException e) {
            logger.error("Failed to connect to SFTP server: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void disconnect() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        logger.info("SFTP connection closed");
    }

    @Override
    public void upload(String remotePath, InputStream inputStream) throws Exception {
        try {
            channelSftp.put(inputStream, remotePath);
            logger.info("File uploaded successfully to {}", remotePath);
        } catch (Exception e) {
            logger.error("Failed to upload file to {}: {}", remotePath, e.getMessage());
            throw e;
        }
    }

    @Override
    public InputStream download(String remotePath) throws Exception {
        try {
            return channelSftp.get(remotePath);
        } catch (Exception e) {
            logger.error("Failed to download file from {}: {}", remotePath, e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String remotePath) throws Exception {
        try {
            channelSftp.rm(remotePath);
            logger.info("File deleted successfully: {}", remotePath);
        } catch (Exception e) {
            logger.error("Failed to delete file {}: {}", remotePath, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean exists(String remotePath) throws Exception {
        try {
            channelSftp.lstat(remotePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void mkdir(String remotePath) throws Exception {
        try {
            channelSftp.mkdir(remotePath);
            logger.info("Directory created successfully: {}", remotePath);
        } catch (Exception e) {
            logger.error("Failed to create directory {}: {}", remotePath, e.getMessage());
            throw e;
        }
    }
}