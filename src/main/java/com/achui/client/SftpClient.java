package com.achui.client;

import java.io.InputStream;
import java.io.OutputStream;

public interface SftpClient {
    void connect() throws Exception;
    void disconnect();
    void upload(String remotePath, InputStream inputStream) throws Exception;
    InputStream download(String remotePath) throws Exception;
    void delete(String remotePath) throws Exception;
    boolean exists(String remotePath) throws Exception;
    void mkdir(String remotePath) throws Exception;
}