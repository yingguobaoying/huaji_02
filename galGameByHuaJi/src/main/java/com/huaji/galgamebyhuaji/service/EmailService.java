package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.jwtToken.VerifyEmail;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

/**
 * 这个是用来发送邮件的工具类
 */
@Service
public class EmailService {
	@Autowired
	RedisMemoryService redisMemoryService;
	/**
	 * ip:userId
	 */
	private static final String template = "VerifyEmail:%s:%s";
	// 发件人的邮箱地址
	@Value("${mail.smtp-email}")
	private String FROM_EMAIL;
	
	public String getFROM_EMAIL() {
		return FROM_EMAIL;
	}
	
	// 发件人的SMTP授权码
	@Value("${mail.smtp-authorization-code}")
	private String SMTP_AUTH_CODE;
	// SMTP服务器
	@Value("${mail.smtp-host}")
	private String SMTP_HOST;
	// SMTP端口
	@Value("${mail.smtp-port}")
	private String SMTP_PORT;
	
	/**
	 * 发送邮件
	 *
	 * @param targetMailbox 收件人邮箱
	 * @param txt           邮件内容
	 * @param topic         邮件主题
	 * @param isHTML        是否为HTML格式
	 */
	
	public void sendEmail(VerifyEmail targetMailbox, String txt, String topic, Boolean isHTML) {
		if (MyStringUtil.isValidChinesePhoneNumber(targetMailbox.getEmail())) {
			throw new OperationException("错误的邮箱格式!");
		}
		String key = template.formatted(targetMailbox.getIp(), targetMailbox.getUserId());
		Date lastSendTime = redisMemoryService.getData(key, Date.class);
		if (
				lastSendTime!=null&&
				1000 * 60 * 60 >= (System.nanoTime() - lastSendTime.getTime())
		) {//每小时仅允许发送一次
			throw new OperationException("我们已经发送了邮件到您的邮箱%s中,如果未发现请检查垃圾邮件\n如果您还未收到邮件请于%s再次获取或者是再次检查您的邮箱             ".formatted(
					targetMailbox.getEmail(), TimeUtil.getVisualDateFormatTime(new Date(
							lastSendTime.getTime() + 1000 * 60 * 60))
			));
		}
		// 配置SMTP服务器的属性
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); // 使用TLS加密
		// 创建Session对象
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FROM_EMAIL, SMTP_AUTH_CODE);
			}
		});
		try {
			// 创建MimeMessage对象
			MimeMessage message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(FROM_EMAIL));
			// 设置收件人
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(targetMailbox.getEmail()));
			// 设置邮件主题
			message.setSubject(topic);
			// 设置邮件内容
			if (isHTML) {
				message.setContent(txt, "text/html; charset=utf-8"); // HTML 格式
			} else {
				message.setText(txt); // 纯文本格式
			}
			// 发送邮件
			Transport.send(message);
			System.out.println("邮件发送成功！");
		} catch (MessagingException e) {
			MyLogUtil.error(EmailService.class, "邮件发送失败!" + e.getMessage());
			e.printStackTrace();
			System.out.println("邮件发送失败: " + e.getMessage());
		}
	}
	
}
