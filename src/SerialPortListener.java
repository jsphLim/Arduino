import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.TooManyListenersException;
  
/** 
 * @项目名称 :串口监听 
 * @文件名称 : 
 * @所在包 : 
 * @功能描述 : 
 *    串口类 
 * @创建者 :hhc 
 * @创建日期 :2015.02.06 
 * @修改记录 : 
 */  
public class SerialPortListener implements Runnable, SerialPortEventListener {  
  
    private String appName = "串口通讯";  
    private int timeout = 2000;//open 端口时的等待时间  
    private int threadTime = 0;  
      
    private CommPortIdentifier commPort;  
    private SerialPort serialPort;  
    private InputStream inputStream;  
    private OutputStream outputStream;  
      
    /** 
     * @方法名称 :listPort 
     * @功能描述 :列出所有可用的串口 
     * @返回值类型 :void 
     */  
    @SuppressWarnings("rawtypes")  
    public void listPort(){  
        CommPortIdentifier cpid;//当前串口对象  
        Enumeration en = CommPortIdentifier.getPortIdentifiers();  
        System.out.print("列出所有端口：");  
        while(en.hasMoreElements()){  
            cpid = (CommPortIdentifier)en.nextElement();  
            //检测端口类型是否为串口  
            if(cpid.getPortType() == CommPortIdentifier.PORT_SERIAL){  
                System.out.println(cpid.getName() + ", " + cpid.getCurrentOwner());  
            }  
        }  
    }  
      
      
    /** 
     * @方法名称 :openPort 
     * @功能描述 :选择一个端口，比如：COM1 并实例 SerialPort 
     * @返回值类型 :void 
     * @param portName 
     */  
    public void openPort(String portName){  
        /* 打开该指定串口 */  
        this.commPort = null;  
        CommPortIdentifier cpid;  
        Enumeration en = CommPortIdentifier.getPortIdentifiers();  
  
        while(en.hasMoreElements()){  
            cpid = (CommPortIdentifier)en.nextElement();  
            if(cpid.getPortType() == CommPortIdentifier.PORT_SERIAL && cpid.getName().equals(portName)){  
                this.commPort = cpid;  
                break;  
            }  
        }  
        /* 实例 SerialPort*/  
        if(commPort == null)  
            log(String.format("无法找到名字为'%1$s'的串口！", portName));  
        else{  
            log("当前端口："+commPort.getName());  
            try{  
                //应用程序名【随意命名】，等待的毫秒数  
                serialPort = (SerialPort)commPort.open(appName, timeout);  
            }catch(PortInUseException e){  
                 // 端口已经被占用   
                throw new RuntimeException(String.format("端口'%1$s'正在使用中！",commPort.getName()));  
            }  
        }  
    }  
      
    /** 
     * @方法名称 :checkPort 
     * @功能描述 :检查端口是否正确连接 
     * @返回值类型 :void 
     */  
    private void checkPort(){  
        if(commPort == null)  
            throw new RuntimeException("没有选择端口，请使用 " +"selectPort(String portName) 方法选择端口");  
          
        if(serialPort == null){  
            throw new RuntimeException("SerialPort 对象无效！");  
        }  
    }  
      
    /** 
     * @方法名称 :write 
     * @功能描述 :向端口发送数据，请在调用此方法前 先选择端口，并确定SerialPort正常打开！ 
     * @返回值类型 :void 
     *    @param message 
     * @throws IOException  
     */  
      
    public void write(String message) throws InterruptedException {  
        checkPort();  
        try{  
            outputStream = new BufferedOutputStream(serialPort.getOutputStream());  
            outputStream.write(message.getBytes());  
            log("消息:'"+message+"'发送成功!");  
            outputStream.close();  
        }catch(IOException e){  
            throw new RuntimeException("向端口发送信息时出错："+e.getMessage());  
        }  
          
        /*另一种 
         try { 
            // 进行输入输出操作 
            OutputStreamWriter writer = new OutputStreamWriter(serialPort.getOutputStream()); 
            BufferedWriter bw = new BufferedWriter(writer); 
            bw.write(message); 
            bw.flush(); 
            bw.close(); 
            writer.close(); 
            System.out.println("消息:'"+message+"'发送成功!"); 
        } catch (IOException e) { 
            throw new RuntimeException("向端口发送信息时出错："+e.getMessage()); 
        }*/  
    }  
      
    /** 
     * @方法名称 :startRead 
     * @功能描述 :开始监听从端口中接收的数据 
     * @返回值类型 :void 
     *    @param time  监听程序时间，单位为秒，0 则是一直监听 
     */  
    public void startRead(int time){  
        checkPort();  
        try{  
            inputStream = new BufferedInputStream(serialPort.getInputStream());  
        }catch(IOException e){  
            throw new RuntimeException("获取端口的InputStream出错："+e.getMessage());  
        }  
          
        try{  
            serialPort.addEventListener(this);  
            // 设置可监听   
            serialPort.notifyOnDataAvailable(true);  
            log(String.format("开始监听来自'%1$s'的数据--------------", commPort.getName()));  
            serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);  
        }catch(TooManyListenersException e){  
             //端口监听者过多;    
            throw new RuntimeException(e.getMessage());  
        } catch (UnsupportedCommOperationException e) {  
            //"端口操作命令不支持";    
            e.printStackTrace();  
        }  
          
          
          
          
        /* 关闭监听 */  
        if(time > 0){  
            this.threadTime = time*1000;  
            Thread t = new Thread(this);  
            t.start();  
            log(String.format("监听程序将在%1$d秒后关闭。。。。", time));  
        }  
    }  
      
      
    /** 
     * @方法名称 :close 
     * @功能描述 :关闭 SerialPort 
     * @返回值类型 :void 
     */  
    public void close(){  
        serialPort.close();  
        serialPort = null;  
        commPort = null;  
    }  
      
      
    public void log(String msg){  
        System.out.println(appName+" --> "+msg);  
    }  
  
  
    /** 
     * 数据接收的监听处理函数 
     */  
    @Override  
    public void serialEvent(SerialPortEvent arg0){  
        switch(arg0.getEventType()){  
        case SerialPortEvent.BI:/*Break interrupt,通讯中断*/   
        case SerialPortEvent.OE:/*Overrun error，溢位错误*/   
        case SerialPortEvent.FE:/*Framing error，传帧错误*/  
        case SerialPortEvent.PE:/*Parity error，校验错误*/  
        case SerialPortEvent.CD:/*Carrier detect，载波检测*/  
        case SerialPortEvent.CTS:/*Clear to send，清除发送*/   
        case SerialPortEvent.DSR:/*Data set ready，数据设备就绪*/   
        case SerialPortEvent.RI:/*Ring indicator，响铃指示*/  
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*Output buffer is empty，输出缓冲区清空*/   
            break;  
        case SerialPortEvent.DATA_AVAILABLE:/*Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端*/  
            byte[] readBuffer = new byte[1024];  
            String readStr="";  
            String s2 = "";  
            try 
            {  
                while (inputStream.available() > 0) 
                {  
                    inputStream.read(readBuffer);  
                    readStr += new String(readBuffer).trim();  
                }  
                log("接收到端口返回数据(长度为"+readStr.length()+")："+readStr);  
            } 
            catch (IOException e) 
            {  
                throw new RuntimeException(e.getMessage());  
            }  
              
           /* 另一种// 进行输入输出操作 
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); 
            String line = null; 
            try { 
                while ((line = reader.readLine()) != null) { 
                    System.out.println(line); 
                } 
                reader.close(); 
            } catch (IOException e) { 
                //e.printStackTrace(); 
            }*/  
        }  
    }  
  
  
    @Override  
    public void run() {  
        try{  
            Thread.sleep(threadTime);  
            serialPort.close();  
            log(String.format("端口'%1$s'监听关闭了！", commPort.getName()));  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 测试 
     * */  
    public static void main(String[] args) throws InterruptedException {  
            SerialPortListener sp = new SerialPortListener();  
        
            /* 列出所有*/  
            sp.listPort();  
            /* 开打相应端口*/  
            sp.openPort("COM5");  
            /* 设置为一直监听*/  
            sp.startRead(0);  
            /* 首次连接后需暂停2秒再继续执行否则数据会有问题*/  
            Thread.sleep(5000);  
            String s;
            while(true)
            {
            	System.out.println("请输入数字：\n");
            	Scanner scn=new Scanner(System.in);
            	s=scn.nextLine();//输入字符串
            	/* 之后发送信息前后间隔至少40ms,否则会将上条返返回信息合并为一条接收(视硬件情况调节)*/  
            	Thread.sleep(40);  
            	sp.write(s);  
            }
    }  
}  