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

public class KeyBasedSftpClient implements SftpClient {
    private static final Logger logger = LoggerFactory.getLogger(KeyBasedSftpClient.class);

    private final String host;
    private final int port;
    private final String username;
    private final String privateKeyPath;
    private final String passphrase;
    private ChannelSftp channelSftp;
    private Session session;

    @Autowired
    private GenericObjectPool<ChannelSftp> sftpPool;

    public KeyBasedSftpClient(String host, int port, String username, String privateKeyPath, String passphrase) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.privateKeyPath = privateKeyPath;
        this.passphrase = passphrase;
    }

    @Override
    public void connect() throws JSchException {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKeyPath, passphrase);
            session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            logger.info("SFTP connection established to {}:{} using key-based authentication", host, port);
        } catch (JSchException e) {
            logger.error("Failed to connect to SFTP server using key-based authentication: {}", e.getMessage());
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