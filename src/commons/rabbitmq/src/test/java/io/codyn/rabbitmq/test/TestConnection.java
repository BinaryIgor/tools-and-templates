package io.codyn.rabbitmq.test;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public class TestConnection implements Connection {

    private final TestChannel channel;

    public TestConnection(TestChannel channel) {
        this.channel = channel;
    }

    @Override
    public InetAddress getAddress() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getChannelMax() {
        return 0;
    }

    @Override
    public int getFrameMax() {
        return 0;
    }

    @Override
    public int getHeartbeat() {
        return 0;
    }

    @Override
    public Map<String, Object> getClientProperties() {
        return null;
    }

    @Override
    public String getClientProvidedName() {
        return null;
    }

    @Override
    public Map<String, Object> getServerProperties() {
        return null;
    }

    @Override
    public Channel createChannel() throws IOException {
        return channel;
    }

    @Override
    public Channel createChannel(int channelNumber) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void close(int closeCode, String closeMessage) throws IOException {

    }

    @Override
    public void close(int timeout) throws IOException {

    }

    @Override
    public void close(int closeCode, String closeMessage, int timeout) throws IOException {

    }

    @Override
    public void abort() {

    }

    @Override
    public void abort(int closeCode, String closeMessage) {

    }

    @Override
    public void abort(int timeout) {

    }

    @Override
    public void abort(int closeCode, String closeMessage, int timeout) {

    }

    @Override
    public void addBlockedListener(BlockedListener listener) {

    }

    @Override
    public BlockedListener addBlockedListener(BlockedCallback blockedCallback, UnblockedCallback unblockedCallback) {
        return null;
    }

    @Override
    public boolean removeBlockedListener(BlockedListener listener) {
        return false;
    }

    @Override
    public void clearBlockedListeners() {

    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public void addShutdownListener(ShutdownListener listener) {

    }

    @Override
    public void removeShutdownListener(ShutdownListener listener) {

    }

    @Override
    public ShutdownSignalException getCloseReason() {
        return null;
    }

    @Override
    public void notifyListeners() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
