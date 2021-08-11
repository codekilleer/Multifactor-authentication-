/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author agupt263
 */
public class Send_Email extends Thread
{
    final String acc="ift.520.otp@gmail.com";
    final String pass="Iftgmailpassword1!";
    
    String to=null;
    String rno=null;
    public Send_Email(String to, String rno)
    {
        this.to=to;
        this.rno=rno;
    }
    public void run()
    {
        send_otp();
    }
    private boolean checkemailidexist(String email)
    {
        boolean isValid = false;
        try {
            // Create InternetAddress object and validated the supplied
            // address which is this case is an email address.
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return isValid;
    }
    
    public boolean send_otp()
    {
        try 
        {
            
            Properties properties =new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            
            Session session= Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(acc,pass); //To change body of generated methods, choose Tools | Templates.
                }
                
            });
            
            Message message= create_message(session);
            Transport.send(message);
            return true;
        } catch (MessagingException ex) {
            Logger.getLogger(Send_Email.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return false;
    }

    private Message create_message(Session session) 
    {
        try 
        {
            Message message=new MimeMessage(session);
            message.setFrom(new InternetAddress(acc));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Login OTP");
            message.setText("OTP : "+rno+"\nValid for 1 min only");
            return message;
        } catch (AddressException ex) {
            Logger.getLogger(Send_Email.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Send_Email.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
