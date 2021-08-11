
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Loign_Server 
{
    private static ArrayList<Login_Client_Handler> lch=new ArrayList<>();
    private static ExecutorService pool=Executors.newFixedThreadPool(4);
    
    public static void main(String[] args)throws IOException
    {
        ServerSocket ss=new ServerSocket(2424);
        while(true)
        {
            Socket client=ss.accept();
            Login_Client_Handler clientThread=new Login_Client_Handler(client);
            lch.add(clientThread);
            pool.execute(clientThread);
        }
    }
}
