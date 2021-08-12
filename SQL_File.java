/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*; 
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;
import javax.imageio.ImageIO;
/**
 *
 * @author agupt263
 */
public class SQL_File {
   
    public static final String ACCOUNT_SID = "<enter your credentials>";
    public static final String AUTH_TOKEN = "<enter your credentials>";
    final String url = "jdbc:mysql://localhost:3306/ift520_2factor";
    final String user = "root";
    final String password = "";

    boolean Client_exist_check(ArrayList<String> credentials) 
    {
        boolean ret=false;
        try 
        {
            Connection con = DriverManager.getConnection(url, user, password);
            
            String query="SELECT * FROM user WHERE Email = '" + credentials.get(0) + "'";
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
            {
                ret=true;
            }
            
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(SQL_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    private String getMD5(String pass)
    {
        try { 
  
            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5"); 
  
            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(pass.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
            return hashtext; 
        }  
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) 
        { 
            throw new RuntimeException(e); 
        } 
    }
    
    boolean check_user_passcode(ArrayList<String> credentials) 
    {
        boolean ret=false;
        try 
        {
             
            Connection con = DriverManager.getConnection(url, user, password);
            
            String query="SELECT passcode FROM user WHERE Email = '" + credentials.get(0) + "'";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
            {
                String retpasscode=rs.getString("passcode");
                if (retpasscode.equals(getMD5(credentials.get(1)))) ret=true;
            }
            
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(SQL_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public  String get_client_contact(ArrayList<String> credentials)
    {
        String ret = null;
        try 
        {
            
            Connection con = DriverManager.getConnection(url, user, password);
            
            String query="SELECT Phone_Number FROM user WHERE Email = '" + credentials.get(0) + "'";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
            {
                ret= rs.getString("Phone_Number");
            }
            
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(SQL_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    public int getRandomNumberUsingNextInt() 
    {
        Random random = new Random();
        return random.nextInt(999999 - 100000) + 100000;
    }
    
    public String sendOTP(ArrayList<String> credentials) 
    {
        String randonn=""+getRandomNumberUsingNextInt();
        String clientcontact=get_client_contact(credentials);
        
        Send_SMS smsob=new Send_SMS(randonn,clientcontact);
        Send_Email emailob=new Send_Email(credentials.get(0),randonn);
        
        smsob.start();
        emailob.start();
        
        return randonn;
    }
    public String sendUpdateCredentialOTP(String newphonenumber,String newemail) 
    {
        String randonn=""+getRandomNumberUsingNextInt();
        Send_SMS smsob=new Send_SMS(randonn,newphonenumber);
        Send_Email emailob=new Send_Email(newemail,randonn);
        
        smsob.start();
        emailob.start();
        
        return randonn;
    }
    
    public profileobject getprofile(String id) 
    {
        profileobject ret = null;
        try 
        {
            
            Connection con = DriverManager.getConnection(url, user, password);
            
            String query="SELECT User_ID, User_Name, Phone_Number, Email, Pic, Designation FROM user WHERE Email = '" + id + "'";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
            {
                ret= new profileobject(rs.getLong("User_ID"),rs.getString("User_Name"),rs.getLong("Phone_Number"),rs.getString("Email")
                        ,rs.getBlob("Pic"),rs.getInt("Designation"));
            }
            else
            {
                System.out.print("No element extracted");
            }
            
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(SQL_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public boolean editsaveprofile(profileobject a,String pass) 
    {
        boolean succ=false;
        try 
        {
            Connection con = DriverManager.getConnection(url, user, password);
            
            String query="UPDATE `user` SET `Pic` = ?,`Email` = ?, `Phone_Number` = ?, `User_Name` = ?, `Passcode` = ? "
                    + "WHERE `user`.`User_ID` = '"+a.getuserid()+"'";
            
            PreparedStatement ps =con.prepareStatement(query);
            
            Image image = a.getimage().getImage();
            BufferedImage bi = imageToBufferedImage(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos );
            byte[] imageInByte = baos.toByteArray();  
            
            ps.setBytes(1, imageInByte);
            ps.setString(2,a.getemail());
            ps.setBigDecimal(3, new BigDecimal(a.getphonenuber()));
            ps.setString(4, a.getusername());
            ps.setString(5, getMD5(pass));
            ps.executeUpdate();
            
            con.close();
        }
        catch (SQLException | IOException ex) 
        {
            Logger.getLogger(SQL_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return succ;
    }
    public boolean editsaveprofile(profileobject a) 
    {
        boolean succ=false;
        try 
        {
            Connection con = DriverManager.getConnection(url, user, password);
            
            String query="UPDATE `user` SET `Pic` = ?,`Email` = ?, `Phone_Number` = ?, `User_Name` = ?"
                    + "WHERE `user`.`User_ID` = '"+a.getuserid()+"'";
            
            PreparedStatement ps =con.prepareStatement(query);
            
            Image image = a.getimage().getImage();
            BufferedImage bi = imageToBufferedImage(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos );
            byte[] imageInByte = baos.toByteArray();  
            
            ps.setBytes(1, imageInByte);
            ps.setString(2,a.getemail());
            ps.setBigDecimal(3, new BigDecimal(a.getphonenuber()));
            ps.setString(4, a.getusername());
            ps.executeUpdate();
      
            con.close();
        }
        catch (SQLException | IOException ex) 
        {
            Logger.getLogger(SQL_File.class.getName()).log(Level.SEVERE, null, ex);
        }
        return succ;
    }
    public static BufferedImage imageToBufferedImage(Image im) 
    {
        BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }
    
}
