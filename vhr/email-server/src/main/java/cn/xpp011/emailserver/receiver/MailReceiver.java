package cn.xpp011.emailserver.receiver;

import cn.xpp011.vhr.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailReceiver {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String form;

    @Autowired
    TemplateEngine templateEngine;

    private static final Logger logger=LoggerFactory.getLogger(MailReceiver.class);

    @RabbitListener(queues = "xpp011Queue")
    public void MimeMailSend(Employee employee){
        logger.info("队列消费"+employee.toString());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage);
        try {
            //邮件主题
            helper.setSubject("入职欢迎");
            //发送人
            helper.setTo(employee.getEmail());
            //来自
            helper.setFrom(form);

            Context context = new Context();
            context.setVariable("name",employee.getName());
            context.setVariable("positionName",employee.getPosition().getName());
            context.setVariable("jobLevelName",employee.getJobLevel().getName());
            context.setVariable("departmentName",employee.getDepartment().getName());

            String mail = templateEngine.process("mail", context);

            helper.setText(mail,true);
            javaMailSender.send(mimeMessage);
            logger.info("员工"+employee.getName()+"的入职邮件发送成功");
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("邮件发送失败"+e.getMessage());
        }
    }

}
