/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author agupt263
 */
public class profileobject implements Serializable{
    BigInteger useid=null;
    String name=null;
    BigInteger phonenumber=null; 
    String email=null;
    ImageIcon image=null;
    int designation;

    profileobject(Long string, String string0, Long aInt, String string1, java.sql.Blob blob, int aInt0) {
        try {
            useid=BigInteger.valueOf(string);
            name=string0;
            phonenumber=BigInteger.valueOf(aInt);
            email=string1;
            
            InputStream in = blob.getBinaryStream(); 
            BufferedImage bfimage =  ImageIO.read(in);
            image =new ImageIcon(bfimage);
            
            designation=aInt0;
        } catch (SQLException | IOException ex) {
            Logger.getLogger(profileobject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setusername(String a)
    {
        name=a;
    }
    public void setphonenumber(BigInteger a)
    {
        phonenumber=a;
    }
    public void setemail(String a)
    {
        email=a;
    }
    public void setimage(ImageIcon a)
    {
        image=a;
    }
    public BigInteger getuserid()
    {
        return useid;
    }
    public String getusername()
    {
        return name;
    }
    public BigInteger getphonenuber()
    {
        return phonenumber;
    }
    public String getemail()
    {
        return email;
    }
    public ImageIcon getimage()
    {
        return image;
    }
    public int getdesignation()
    {
        return designation;
    }
    
}
