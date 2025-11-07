package com.xzc.login.rest;

import com.github.hui.quick.plugin.base.DomUtil;
import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeGenWrapper;
import com.github.hui.quick.plugin.qrcode.wrapper.QrCodeOptions;
import com.google.zxing.WriterException;
import com.xzc.login.util.IpUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 二维码登录控制器
 * 处理二维码登录相关的请求
 */
@CrossOrigin
@Controller
public class QrLoginRest {

    @Value(("${server.port}"))
    private int port;

    /**
     * 生成二维码登录页面
     * 该方法会生成一个唯一的ID，构建扫码地址、订阅地址和重定向地址，
     * 并生成对应的二维码图片，最后返回登录页面视图
     *
     * @param data 用于向视图传递数据的Map对象，包含二维码图片、重定向地址和订阅地址等信息
     * @return 返回登录页面的视图名称
     * @throws IOException IO异常
     * @throws WriterException 二维码生成异常
     */
    @GetMapping(path = "login")
    public String qr(Map<String, Object> data) throws IOException, WriterException {
        // 生成唯一标识符
        String id = UUID.randomUUID().toString();
        // 获取本地IP地址
        String ip = IpUtils.getLocalIp();

        // 构建基础URL前缀
        String pref = "http://"+ip+":"+port+"/";
        // 设置重定向地址
        data.put("redirect",pref+"home");
        // 设置订阅地址
        data.put("subscribe",pref+"subscribe?id=" + id);

        // 构建扫码地址并生成二维码
        String qrUrl = pref+"scan?id"+id;
        String qrCode = QrCodeGenWrapper.of(qrUrl).setW(200).setDrawPreColor(Color.RED)
                .setDrawStyle(QrCodeOptions.DrawStyle.CIRCLE).asString();
        // 将二维码转换为DOM图片源格式并放入数据Map
        data.put("qrcode", DomUtil.toDomSrc(qrCode, MediaType.ImageJpg));
        return "login";
    }

        private Map<String, SseEmitter> cache = new ConcurrentHashMap<>();

    /**
     * 订阅SSE连接
     * @param id 客户端唯一标识符
     * @return SseEmitter对象，用于向客户端发送数据
     */
    public SseEmitter subscribe(String id){
        // 创建SSE发射器，设置超时时间为5分钟
        SseEmitter sseEmitter = new SseEmitter(5 * 60 * 1000L);
        // 将发射器缓存到map中
        cache.put(id,sseEmitter);
        // 设置超时回调，从缓存中移除对应的发射器
        sseEmitter.onTimeout(()->cache.remove(id));
        // 设置错误回调，从缓存中移除对应的发射器
        sseEmitter.onError((e)-> cache.remove(id));
        return sseEmitter;
    }

    /**
     * 扫描处理方法
     * @param model 视图模型对象，用于向前端传递数据
     * @param request HTTP请求对象，用于获取请求参数
     * @return 返回视图名称"scan"
     * @throws IOException IO异常
     */
    public String scan(Model model,HttpServletRequest request) throws IOException {
        // 获取请求参数中的id
        String id = request.getParameter("id");
        // 从缓存中获取对应的SSE发射器
        SseEmitter sseEmitter = cache.get(id);
        // 如果发射器存在，则向客户端发送"scan"消息
        if (sseEmitter != null){
            sseEmitter.send("scan");
        }

        // 构造回调URL并添加到模型中
        String url = "http://" + IpUtils.getLocalIp() + ":" + port + "/accpet?id=" + id;
        model.addAttribute("url",url);
        return "scan";
    }

    /**
     * 处理二维码登录确认请求
     *
     * @param id 二维码唯一标识符，用于获取对应的SSE连接
     * @param token 登录令牌，将通过SSE发送给客户端
     * @return 返回登录成功信息及令牌
     * @throws IOException 当SSE发送数据失败时抛出
     */
    @ResponseBody
    @GetMapping(path = "accept")
    public String accept(String id,String token) throws IOException {
        SseEmitter sseEmitter = cache.get(id);
        if (sseEmitter != null){
            // 向客户端发送登录令牌并关闭SSE连接
            sseEmitter.send("login#qrlogin=" + token);
            sseEmitter.complete();
            cache.remove(id);
        }
        return "登录成功"+token;
    }

    /**
     * 处理首页访问请求，检查用户登录状态
     *
     * @param request HTTP请求对象，用于获取Cookie信息
     * @return 返回欢迎信息或未登录提示
     */
    @ResponseBody
    @GetMapping(path = {"home", ""})
    public String home(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        // 检查是否存在Cookie且包含qrlogin信息
        if (cookies == null || cookies.length == 0){
            return "未登录";
        }
        Optional<Cookie> cookie = Stream.of(cookies).filter(s->s.getName().equalsIgnoreCase("qrlogin")).findFirst();
        return cookie.map(cookie1 -> "欢迎进入首页："+cookie1.getValue()).orElse("未登录");
    }

}
