package com.xzc.login.util;

import java.net.*;
import java.util.Enumeration;

/**
 * IP地址工具类
 * 提供获取本机IP地址的相关方法
 */
public class IpUtils {
    public static final String DEFAULT_IP = "127.0.0.1";

    /**
     * 通过网卡获取本机IP地址
     * 遍历所有网络接口，找到第一个非回环、已启用的IPv4地址
     * 如果找不到则使用InetAddress.getLocalHost()获取
     *
     * @return 本机IP地址字符串，如果发生异常则返回默认IP(127.0.0.1)
     */
    public static String getLocalIpByNetCard(){
        try {
            // 遍历所有网络接口
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();e.hasMoreElements();){
                NetworkInterface item = e.nextElement();
                // 跳过回环接口和未启用的接口
                if (item.isLoopback() || !item.isUp()){
                    continue;
                }
                // 查找IPv4地址
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (address.getAddress() instanceof Inet4Address){
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();
                        return inet4Address.getHostAddress();
                    }
                }
            }
            // 如果遍历完所有网络接口都没找到，则使用本地主机地址
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            // 发生异常时返回默认IP地址
            return DEFAULT_IP;
        }
    }

    private static volatile String ip;

    /**
     * 获取本机IP地址（带缓存）
     * 使用双重检查锁定模式确保线程安全，避免重复获取IP地址
     *
     * @return 本机IP地址字符串
     */
    public static String getLocalIp(){
        // 第一次检查，避免不必要的同步
        if (ip == null){
            // 同步块确保线程安全
            synchronized (IpUtils.class){
                // 第二次检查，确保只初始化一次
                if (ip == null){
                    ip = getLocalIpByNetCard();
                }
            }
        }
        return ip;
    }
}
