/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

/**
 *
 * @author agupt263
 */

public class Send_SMS extends Thread{
    String rno=null;
    String clientphonenumber=null;
    public static final String ACCOUNT_SID = "<Enter your credentials>";
    public static final String AUTH_TOKEN = "<Enter your credentials>";
    public Send_SMS(String rno, String clientphonenumber)
    {
        this.rno=rno;
        this.clientphonenumber=clientphonenumber;
    }
    public void run()
    {
        send_otp();
    }
    public boolean send_otp()
    {
        //try
        //{
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber("+1"+clientphonenumber),
                    new com.twilio.type.PhoneNumber("+14048692527"),
                    "Your OTP is: "+rno+"\nValid for 1 min only")
                .create();
        
            System.out.println(message.getSid());
            return true;
        //}
        //catch(Exception e)
        //{
            //System.out.println(e);
            
        //}
        //return false;
    }
    
}
