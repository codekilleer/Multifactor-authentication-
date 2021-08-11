/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author agupt263
 */
public class ValidatePhoneNumber 
{
    public static boolean isValidPhoneNumber(String pnumber) 
    {
        try {
            PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(pnumber,"US");
            return  numberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException ex) {
            Logger.getLogger(ValidatePhoneNumber.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
}
