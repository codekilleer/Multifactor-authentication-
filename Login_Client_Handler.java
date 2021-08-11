/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agupt263
 */
public class Login_Client_Handler implements Runnable
{
    private Socket client;
    SQL_File ob=null;
    ObjectInputStream in=null;
    ObjectOutputStream out=null;
    
    public Login_Client_Handler(Socket client) {
        try 
        {
            if (client.isClosed())
            {
                System.out.print("sdsd");
            }
            this.client = client;
            ob=new SQL_File();
            out = new ObjectOutputStream(this.client.getOutputStream());
            in =new ObjectInputStream(this.client.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Login_Client_Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    private void close_connection()
    {
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Login_Client_Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void dashboardloader(ArrayList<String> credentials)
    {
        
        try 
        {
            boolean rep=true;
            do
            { 
                String clientinput=(String) in.readObject();
                
                if (clientinput.equalsIgnoreCase("fin_connection"))
                {
                    rep=false;
                    close_connection();
                }
                else if (clientinput.equalsIgnoreCase("load"))
                {
                    profileobject profileob=ob.getprofile(credentials.get(0));
                    out.writeObject(profileob);
                    out.flush();
                    out.reset();
                }
                else if (clientinput.equalsIgnoreCase("saveeditprofile"))
                {
                    String temp= (String) in.readObject();
                    
                    if (temp.equalsIgnoreCase("savewithoutpasscode"))
                    {
                        profileobject tp= (profileobject) in.readObject();
                        System.out.print(tp.getimage());
                        ob.editsaveprofile(tp);
                    }
                    else
                    {
                        profileobject tp= (profileobject) in.readObject();
                        String tpass= (String) in.readObject();
                        
                        ob.editsaveprofile(tp, tpass);
                    }
                }
                else if (clientinput.equalsIgnoreCase("emailcheck"))
                {
                    String email=in.readObject()+"";
                    
                    ValidateEmail ve=new ValidateEmail();
                    
                    Boolean sc= ve.isValidEmailAddress(email);
                    out.writeBoolean(sc);
                    out.flush();
                    out.reset();
                 
                }
                else if (clientinput.equalsIgnoreCase("phonenumbercheck"))
                {
                    String pnumber=in.readObject()+"";
                    
                    ValidatePhoneNumber ve=new ValidatePhoneNumber();
                    
                    Boolean sc= ve.isValidPhoneNumber(pnumber);
                    out.writeBoolean(sc);
                    out.flush();
                    out.reset();
                 
                }
                else if (clientinput.equalsIgnoreCase("EditConfirmOTP"))
                {
                    String newphonenumber=in.readObject()+"";
                    String newemail=in.readObject()+"";
                    long start = System.currentTimeMillis();
                    String generatedotp=ob.sendUpdateCredentialOTP(newphonenumber,newemail);
                    
                    String clientotp=in.readObject()+"";
                    if (!clientotp.equalsIgnoreCase("cancelEditConfirmOTP"))
                    {
                        long end = System.currentTimeMillis();
                        if ((end - start)>60000)
                        {
                            out.writeBoolean(false);
                            out.flush();
                            out.reset();
                        }
                        else
                        {
                            if (clientotp.equalsIgnoreCase(generatedotp))
                            {
                                out.writeBoolean(true);
                                out.flush();
                                out.reset();
                            }
                            else
                            {
                                out.writeBoolean(false);
                                out.flush();
                                out.reset();
                            }
                        }
                    }
                }
                
            }while(rep);
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Login_Client_Handler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Login_Client_Handler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    @Override
    public void run() 
    {
        
        boolean repeat;
        ArrayList<String> credentials =null;
        try 
        {
            do
            {
                repeat=true;
                Object object = in.readObject();
                if (object.equals("fin_connection"))
                {
                    repeat=false;
                    close_connection();
                }
                else
                {
                    credentials = (ArrayList<String>) object;
                
                    boolean retb=ob.Client_exist_check(credentials);
                    if (!retb)
                    {
                        out.writeObject(1);
                        out.flush();
                        out.reset();
                    }
                    else
                    {
                        retb=ob.check_user_passcode(credentials);
                        if (!retb)
                        {
                            out.writeObject(2);
                            out.flush();
                            out.reset();
                        }
                        else
                        {
                            long start = System.currentTimeMillis();
                            String generatedotp=ob.sendOTP(credentials);
                            if (generatedotp.equals("-9999"))
                            {
                                out.writeObject(4);
                                out.flush();
                                out.reset();
                                repeat=false;
                            }
                            else
                            {
                                out.writeObject(3);
                                out.flush();
                                out.reset();
                                
                                String clientotp=(String) in.readObject();
                                if (!clientotp.equals("OTPcancel"))
                                {
                                    long end = System.currentTimeMillis();
                                    if ((end - start)>60000)
                                    {
                                        out.writeObject("TimeOut");
                                        out.flush();
                                        out.reset();
                                    }
                                    else
                                    {
                                        if (generatedotp.equalsIgnoreCase(clientotp))
                                        {
                                            out.writeObject("true");
                                            out.flush();
                                            out.reset();
                                            dashboardloader(credentials);
                                            repeat=false;
                                        }
                                        else
                                        {
                                            out.writeObject("false");
                                            out.flush();
                                            out.reset();
                                        }
                                    }
                                }
                            }
                            
                        }
                    }
                }
           
            }while(repeat);
            
            
            
        } 
        catch (IOException | ClassNotFoundException ex) 
        {
            Logger.getLogger(Login_Client_Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
    }
    
}
