import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.leapmotion.leap.Arm;
import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Pointable;
import com.leapmotion.leap.Vector;


class LeapMotionTo6DofV3 {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private static boolean printByMqtt = true;
	private static MqttSendCmd mqttSCmd;
	private static SerialPortListener sp; 
	private static int sendedPwm1 = 1500;
	private static int sendedPwm2 = 1500;
	private static int sendedPwm3 = 1500;
	private static int sendedPwm4 = 1500;
	private static int sendedPwm5 = 1500;
	private static int sendedPwm6 = 1500;
	
    public static void main(String[] args) {
        // Create a sample listener and controller
        LeapMotionListener listener = new LeapMotionListener();
        Controller controller = new Controller();

       
        String s;
        
        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        Vector plam = new Vector();
        Point point = new Point();
        Point leftpalm = new Point();
        
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try 
        {
        	 sp = new SerialPortListener();  
        	 mqttSCmd = new MqttSendCmd();
             
        	 if(!printByMqtt)
        	 {
        		 
        		 /* �г�����*/  
        		 sp.listPort();  
        		 /* ������Ӧ�˿�*/  
        		 sp.openPort("COM5");  
        		 /* ����Ϊһֱ����*/  
        		 sp.startRead(0);  
        	 }
             /* �״����Ӻ�����ͣ2���ټ���ִ�з�����ݻ�������*/  
             Thread.sleep(2000);  
        	
            //System.in.read();
            VideoCapture capture = new VideoCapture();
    		capture.open(0);
    		JFrame frame1 = new JFrame("show image");
    		frame1.setTitle("����ͷ����");
    		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		frame1.setSize(640, 480);
    		frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());
    		Panel panel1 = new Panel();
    		frame1.setContentPane(panel1);
    		frame1.setVisible(true);
    		frame1.addKeyListener(kl);
    		float oldGrabStrength = -1;
    		float currentGrabStrength = -1;
   		
    		//�ֱ�ģʽ�µĵ���
    		int xoff = 40;
    		int yoff = 0;
    		Point p1 = new Point(180+xoff,180+yoff);
    		Point p2 = new Point(460-xoff,180+yoff);
    		Point p3 = new Point(460-xoff,300-yoff);
    		Point p4 = new Point(180+xoff,300-yoff);
    		
    		int r = 60;
    		Point up = new Point(130,220);
    		Point dp = new Point(130,420);
    		Point lp = new Point(65,320);
    		Point rp = new Point(200,320);
    		
    		//С��ģʽ�µĵ���
    		int tw = 80;
    		int th = 120;
    		Point up1 = new Point(320 - tw,160);
    		Point up2 = new Point(320,160 - th);
    		Point up3 = new Point(320 + tw,160);
    		
    		Point dp1 = new Point(320 - tw,320);
    		Point dp2 = new Point(320 + tw,320);
    		Point dp3 = new Point(320,320 + th);
    		
    		int gap = 15;
    		Point lp1 = new Point(up1.x - gap,up1.y);
    		Point lp2 = new Point(up1.x - gap,dp1.y);
    		Point lp3 = new Point(up1.x - gap - th,240);
    		
    		Point rp1 = new Point(up3.x + gap,up3.y);
    		Point rp2 = new Point(rp1.x + th,240);
    		Point rp3 = new Point(dp2.x + gap,dp2.y);
    		Arm arm;
    		Vector palm;
    		Vector wrist;
    		Vector elbow;
    		Vector tmp;
    		float grabAngle;
    		int pwm1 = 0;
    		int oldpwm1 = 0;
    		int pwm2 = 0;
    		int oldpwm2 = 0;
    		int pwm3 = 0;
    		int oldpwm3 = 0;
    		int pwm4 = 0;
    		int oldpwm4 = 0;
    		int pwm5 = 0;
    		int oldpwm5 = 0;
    		int pwm6 = 0;
    		int oldpwm6 = 0;
    		Vector palmNormal;   ///���Ʒ�����������������ָ���ⲿ
    		Vector oldpalmNormal = new Vector();   ///���Ʒ�����������������ָ���ⲿ
    		double wristAngle;   //�������ת�Ƕ�
    		double armAngle = 0;
    		double oldArmAngle = 0;
    		Vector direction;
    		float pitch;
    		boolean first = false;
    		long time = 0;
    		int enter = 0;
    		float y;
    		float oldy = 0;
    		float x;
    		float oldx = 0;
    		float ybase = 0;
    		Vector palmVelocity;
    		Vector oldpalmVelocity1 = new Vector();
    		Vector oldpalmVelocity2 = new Vector();
    		boolean isMotionless = false;
    		boolean oldisMotionless1 = true;
    		boolean oldisMotionless2 = true;
    		long motionlessTime = 0;  //��ֹʱ��
    		boolean firstMotionless = true;
    		String sendedCmd = "";
    		
    		int servoNumber = 0;
    		float distanceOfTips = 0;
    		float oldDistanceOfTips = 0;
    		Pointable pointable1;
        	Pointable pointable2;
    		boolean clawClosed = false;  //ץ�ӵ�״̬
        	boolean handModel = false;  //ģʽ�л���С��/�ֱ�
        	int fingerCount = -1;
        	int oldfingerCount = -1;
        	int v = 60; //�ٶȷ�ֵ���ж��Ƿ��˶�
    		if (!capture.isOpened()) 
    		{
    			System.out.println("Error");
    		}
    		else 
    		{
    			Mat webcam_image = new Mat();
    			capture.read(webcam_image);
    			frame1.setSize(webcam_image.width() + 40,webcam_image.height() + 60);
    			while (true) 
    			{
    				Thread.sleep(80);  //����֡��
    				capture.read(webcam_image);
    				//System.out.println("������ͷ��ͼ����ʾ��������");
    				//System.out.println("palm positon:" + listener.getPalm());
    				plam = listener.getPalm();
    				if(listener.isEmpty())
    				{
    					first = true;
    					enter = 0;
    					clawClosed = false;
    				}


    				if(null != plam)
    				{
    					
    					point.x = plam.getX() + 320;
    					point.y = plam.getZ() + 240;
    					if(first)
        				{
        					oldy = plam.getY();
        					first = false;
        				}
    				}
    				
    				HandList hands = listener.getHands();
                	if(null != hands)
                	{
                		
                		if(handModel)  //С��ģʽ���Կ���С�����ֱ�ģʽ���԰ѿ����ֱ۵Ķ���
                		{
                			Imgproc.putText(webcam_image,"hand model", new Point(10,30), 3, 1.2, new Scalar(0, 0, 255));
                		}
                		else
                		{
                			clawClosed = true;
                			Imgproc.putText(webcam_image,"car model", new Point(10,30), 3, 1.2, new Scalar(0, 0, 255));
                		}
                		
                		if(clawClosed)  //��ʾץ�ӵ�״̬����ס�������סʱ���ܿ���ץ�ӣ������ܿ���6�Ŷ�����Կ���5�Ŷ�������ܿ���ץ�ӣ����ܿ���6�Ŷ�������Կ���5�Ŷ��
            			{
            				Imgproc.putText(webcam_image,"claw locked", new Point(340,30), 3, 1.2, new Scalar(0, 0, 255));
            			}
            			else
            			{
            				Imgproc.putText(webcam_image,"claw unlocked", new Point(340,30), 3, 1.2, new Scalar(0, 0, 255));
            			}
                		
                		for(Hand hand : hands)
                		{
                			//����������ȭ����ʱ�����Կ���С��������
                			if(hand.isRight())
                			{
                				if(1 == hands.count())
                				{
                					clawClosed = false;
                				}
                				
                				
                				
                				if(handModel)  //�ֱۿ���ģʽ
                				{
                					//��ȡ�ֽ����ȶ���ĸ߶�
                    				if(hands.rightmost().timeVisible() > 0.5 && hands.rightmost().timeVisible() < 0.6)
                    				{
                    					ybase = hands.rightmost().palmPosition().getY();
                    					System.out.println("ybase======================== " + ybase);
                    				}
                    				
                    				leftpalm.y = hand.palmPosition().getY();
                    				System.out.println("plam.getY:  " + plam.getY());
                    				
                    	            Vector normal = hands.rightmost().palmNormal();
                    	            direction = hands.rightmost().direction();
                    	            palmVelocity = hands.rightmost().palmVelocity();
                    	            
                    	           
                	            	//1�Ŷ��
                	            	//�ֱ۵�����ɨ���ĽǶ�
                    				tmp = hands.rightmost().palmPosition();
                					Imgproc.line(webcam_image,new Point(320,240),new Point(hands.rightmost().palmPosition().getX() + 320,hands.rightmost().palmPosition().getZ() + 240),new Scalar(0,0,255),10);
                					armAngle = Math.atan(-1*hands.rightmost().palmPosition().getZ()/hands.rightmost().palmPosition().getX());
                					pwm1 = (int) Math.toDegrees(armAngle);
                					if(pwm1 < 0)
                					{
                						pwm1 = pwm1 + 180;
                					}
                					//System.out.println("oldpwm1=" + oldpwm1 + ",pwm1=" + pwm1);
                					if(Math.abs(oldpwm1 - pwm1) > 2 && tmp.getZ() < 0)  //�ֱ۵�����ɨ���Ƕȴ���5ʱ������Ҫ����������ɨ��
                					{
                						
                						sendedPwm1 = (int)(1150 + 2.5*pwm1);
                						if(sendedPwm1 > 1600)
                						{
                							sendedPwm1 = 1600;
                						}
                						else if(sendedPwm1 < 1150)
                						{
                							sendedPwm1 = 1150;
                						}
                						//Imgproc.putText(webcam_image,"angle:" + pwm1, new Point(50,400), 3, 1.2, new Scalar(0, 0, 255));
                						write("<SUP-" + sendedPwm1 + ">"); 
                						Thread.sleep(40); 
                						System.out.println("1�Ŷ��"); 
                					}
                					oldpwm1 = pwm1;
                    	         
                    	         
                    	            	
                					//2�Ŷ��
                	            	y = hands.rightmost().palmPosition().getY();
                	            	if(Math.abs(oldy - y) > 3)
                    				{
                	            		System.out.println("2�Ŷ��");
                	            		if(leftpalm.y > ybase)
                	            		{
                	            			write("<SLT-1500>");  //3�Ŷ��(ǰ��ǰ��180ת)
                							Thread.sleep(40);
                							sendedPwm2 = 1500;
                	            		}
                	            		else if(leftpalm.y < ybase)
                	            		{
                	            			int off = (int) (ybase - leftpalm.y);
                	            			sendedPwm3 = (int)(1500 - 3.5*off);
                	            			write("<SLT-" + sendedPwm3 + ">");  //3�Ŷ��(ǰ��ǰ��180ת)
                							Thread.sleep(40);
                							sendedPwm2 = (int)(1500 + 1.75*off);
                	            		}
                						
                						if(sendedPwm2 > 1800)
                						{
            								sendedPwm2 = 1800;
                						}
                						else if(sendedPwm2 < 1500)
                						{
                							sendedPwm2 = 1500;
                						}
            							write("<SDN-" + sendedPwm2 + ">"); 
            							Thread.sleep(40); 
            							sendedCmd = "SDN";
                    				}
                	            	oldy = y;
                    	            
                    	            
                    	       
            	            		//6�Ŷ�����״̬����Ĵָ��ʳָ�ľ���仯����5��
                	            	//ץ�ӵ���ת
                	            	grabAngle = hands.rightmost().grabAngle();
                	            	pwm6 = (int) (1000*grabAngle/3.14 + 800);
                	            	
                    				if(Math.abs(oldpwm6 - pwm6) > 3)
                    				{
                    					System.out.println("6");
                    					
                    					//Thread.sleep(100);
                    					sendedPwm6 = (int)(Math.ceil(pwm6));
                    					if(sendedPwm6 > 1800)
                						{
                    						sendedPwm6 = 1800;
                						}
                						else if(sendedPwm6 < 800)
                						{
                							sendedPwm6 = 800;
                						}
                    					
                    					if(!clawClosed)
                    					{
                    						
                    						write("<SDR-" + sendedPwm6 + ">"); 
                    						Thread.sleep(40); 
                    						sendedCmd = "SDR";
                    						System.out.println("6�Ŷ��"); 
                    					}
                    					//Imgproc.putText(webcam_image,"6 servo", new Point(50,100), 3, 1.2, new Scalar(255, 0, 0));
                    				}
                    				oldpwm6 = pwm6;
                	            	
                	            	
                	            	//5�Ŷ����ס״̬�����ֵķ���仯����5��
                    				armAngle = Math.toDegrees(Vector.xAxis().angleTo(hand.palmNormal()))-90;  //���Ƶ���ת�Ƕ�,�涨һ������˳ʱ����תΪ��֮Ϊ���� 
                	            	if(Math.abs(oldArmAngle - armAngle) > 3)  
                	            	{
                	            		System.out.println("5");
                	            		
                	            		//��ת
                	            		if(armAngle > 0) 
                	            		{
                	            			sendedPwm5 = (int)(1500 - 1400*armAngle/90);
                        					
                	            		}
                	            		else
                	            		{
                	            			sendedPwm5 = (int)(1500 - 1000*armAngle/90);
                	            		}
                	            		
                	            		if(sendedPwm5 > 2500)
                	            		{
                	            			sendedPwm5 = 2500;
                	            		}
                	            		else if(sendedPwm5 < 100)
                	            		{
                	            			sendedPwm5 = 100;
                	            		}
                	            		
                	            		write("<SDL-" + sendedPwm5 + ">"); 
                	            		Thread.sleep(40); 
                	            	}
                    	            
                    	            oldisMotionless2 = oldisMotionless1;
                    	            oldisMotionless1 = isMotionless;
                    	            oldpalmVelocity2 = oldpalmVelocity1;
                    	            oldpalmVelocity1 = palmVelocity;
                    	        	oldDistanceOfTips = distanceOfTips;
                    	        	oldArmAngle = armAngle;
                				}
                				else           //С������ģʽ
                				{
                					//ǰ��
                					if(isInTriangle(up1, up2, up3, point))
                					{
                						//System.out.println("foward");
                						write("<BUPD>");
                						Imgproc.putText(webcam_image,"move: foward", new Point(10,60), 3, 1.2, new Scalar(0, 0, 255));
                					}
                							
                					//����
                					else if(isInTriangle(dp1, dp2, dp3, point))
                					{
                						//System.out.println("back");
                						write("<BDND>");
                						Imgproc.putText(webcam_image,"move: back", new Point(10,60), 3, 1.2, new Scalar(0, 0, 255));
                					}
                					
                					//��ת
                					else if(isInTriangle(lp1, lp2, lp3, point))
                					{
                						//System.out.println("left");
                						write("<BLTD>");
                						Imgproc.putText(webcam_image,"move: left", new Point(10,60), 3, 1.2, new Scalar(0, 0, 255));
                					}
                					
                					//��ת
                					else if(isInTriangle(rp1, rp2, rp3, point))
                					{
                						//System.out.println("right");
                						write("<BRTD>");
                						Imgproc.putText(webcam_image,"move: move", new Point(10,60), 3, 1.2, new Scalar(0, 0, 255));
                					}
                				}
                			}
                			
                			
                			//������ȭʱ�����Կ����ӻ��ת��
                			if(hand.isLeft())
                			{
                				FingerList fingers = hand.fingers().extended();
                				fingerCount = fingers.count();
                				//System.out.println(fingerCount);
                				//System.out.println("time : " + hand.pointables().get(1).timeVisible());
                				if(hand.pointables().get(1).timeVisible() > 0.2 && hand.pointables().get(1).timeVisible() < 0.3)
                				{
                					clawClosed = true; //���ֽ�ȥ����סץ��
                				}
                				
                				
                				if(5 == fingerCount && hand.pointables().get(1).timeVisible() > 0.2 && hand.pointables().get(1).timeVisible() < 0.3)
                				{
                					handModel = !handModel;
                					if(!handModel) //С��ģʽ��סץ��
                					{
                						clawClosed = true;
                					}
                					else
                					{
                						
                						//2�Ŷ��
                						write("<SDN-1500>");
                						Thread.sleep(40); 
                						//3�Ŷ��
                						write("<SLT-1500>");
                					}
                				}
                				
                				
                				double aa = Math.toDegrees(Vector.xAxis().angleTo(hand.palmNormal())); 
                				//System.out.println("aa========================" + aa);
                				//System.out.println("y:" + hand.palmNormal().getY() + ",x:" + hand.palmNormal().getX());
            					//forward
                				if(1 == fingerCount && 0 == oldfingerCount)
                				{
                					
                					if(aa < 45)
                					{
                						write("<BUPD>");
                						System.out.println("forward==============");
                					}
                					
                					//back
                					else if(aa > 100)
                					{
                						write("<BDND>");
                						System.out.println("back================");
                					}
                					
                					//left
                					else if(aa > 45  && aa < 100 && hand.palmNormal().getY() > 0)
                					{
                						write("<BLTD>");
                						System.out.println("left=================");
                					}
                					
                					//right
                					else if(aa > 45  && aa < 100 && hand.palmNormal().getY() < 0)
                					{
                						write("<BRTD>");
                						System.out.println("right==================");
                					}
                				}
                				oldfingerCount = fingerCount;
                			}


                			//System.out.println(hand.isLeft() ? "Left hand" : "Right hand");
                			//hand.grabStrength() = 1 ʱΪ��ȭ ��hand.grabStrength() = 0 ʱΪ��ָ��ȫ�ſ�������ֵ������������֮�䡣
                			/*
                			 * hand.grabAngle()���ص��ǳ�ȴ��Ĵָ�ĸ���ָ��ƽ������̶ȣ����Խ���ȭͷ��ʱ����ֵΪ3.14,��ȫչ��ʱΪ0
                			 */
                			//System.out.println("grab info:" + hand.grabAngle() + "," + hand.grabStrength());
                			oldGrabStrength = currentGrabStrength;
                			currentGrabStrength = hand.grabStrength() ;
                			//palm position����λ�ã���������λ�þ�leap motionԭ��ľ��룬��λ����
                			//palm velocity�����ٶȣ���λmm/s
                			//palm normal���Ʒ�����������������ָ���ⲿ
                			//direction��������ָ����ָ������
                			palm = hand.palmPosition();
                			arm = hand.arm();
                			//Imgproc.putText(webcam_image,"arm-x:" + arm.direction().getX(), new Point(50,150), 3, 1.2, new Scalar(255, 0, 0));
                			//Imgproc.putText(webcam_image,"arm-y:" + arm.direction().getY(), new Point(50,200), 3, 1.2, new Scalar(255, 0, 0));
                			//Imgproc.putText(webcam_image,"arm-z:" + arm.direction().getZ(), new Point(50,250), 3, 1.2, new Scalar(255, 0, 0));
                			wrist = arm.wristPosition();
                			elbow = arm.elbowPosition();
                			//System.out.println(hand.palmNormal());
                			//���ĵ��������
                			Imgproc.line(webcam_image,new Point(palm.getX() + 320,palm.getZ() + 240),new Point(wrist.getX() + 320,wrist.getZ() + 240),new Scalar(255,0,0),15);
                			//����������
                			Imgproc.line(webcam_image,new Point(wrist.getX() + 305,wrist.getZ() + 240),new Point(wrist.getX() + 335,wrist.getZ() + 240),new Scalar(255,0,0),15);
                			
                			Imgproc.line(webcam_image,new Point(elbow.getX() + 305,elbow.getZ() + 240),new Point(wrist.getX() + 305,wrist.getZ() + 240),new Scalar(255,0,0),15);
                			Imgproc.line(webcam_image,new Point(elbow.getX() + 335,elbow.getZ() + 240),new Point(wrist.getX() + 335,wrist.getZ() + 240),new Scalar(255,0,0),15);
                			//System.out.println("palmNormal:" + hand.palmNormal() + "," + hand.palmVelocity());
                			
                			if(handModel)
                			{
                				
                				for (Finger finger : hand.fingers()) 
                				{
                					for(Bone.Type boneType : Bone.Type.values())
                					{
                						Bone bone = finger.bone(boneType);
                						// System.out.println(bone.center());
                						Imgproc.line(webcam_image,new Point(bone.center().getX() + 320,bone.center().getZ() + 240),new Point(bone.center().getX() + 320,bone.center().getZ() + 240),new Scalar(0,255,0),8);
                					}
                				}
                				Imgproc.line(webcam_image,point,point,new Scalar(0,0,255),1);
                				Imgproc.line(webcam_image,new Point(320,240),point,new Scalar(0,0,255),1);
                			}
                		}
                	}
    				
                	if(handModel)  //�ֱ�ģʽ�����Կ����ֱ۵Ķ���
    				{
    					
    					Imgproc.line(webcam_image,p1,p2,new Scalar(255,0,0),2);
    					Imgproc.line(webcam_image,p2,p3,new Scalar(255,0,0),2);
    					Imgproc.line(webcam_image,p3,p4,new Scalar(255,0,0),2);
    					Imgproc.line(webcam_image,p4,p1,new Scalar(255,0,0),2);
    					
    					
    					Imgproc.line(webcam_image,new Point(600,40),new Point(600,100),new Scalar(0,0,200),1);
    					Imgproc.line(webcam_image,new Point(600,100),new Point(600,300),new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,new Point(600,100),new Point(620,100),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,150),new Point(620,150),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,200),new Point(620,200),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,250),new Point(620,250),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,300),new Point(620,300),new Scalar(0,0,255),1);
    					
    					Imgproc.line(webcam_image,new Point(600,100),new Point(610,100),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,125),new Point(610,125),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,175),new Point(610,175),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,225),new Point(610,225),new Scalar(0,0,255),1);
    					Imgproc.line(webcam_image,new Point(600,275),new Point(610,275),new Scalar(0,0,255),1);
    					
    					if(leftpalm.y > ybase)
    					{
    						Imgproc.line(webcam_image,new Point(600,80),new Point(600,80),new Scalar(200,0,0),15);
    					}
    					else if(leftpalm.y < ybase)
    					{
    						int off = (int) (ybase - leftpalm.y);
    						Imgproc.line(webcam_image,new Point(600,100+off),new Point(600,100+off),new Scalar(200,0,0),15);
    					}
    				}
    				else
    				{
    					//���ĵ�
    					Imgproc.line(webcam_image,point,point,new Scalar(0,0,255),15);
    					//Imgproc.putText(webcam_image,"x == " + point.x, new Point(50,300), 3, 1.2, new Scalar(0, 0, 255));
    					//Imgproc.putText(webcam_image,"y == " + point.y, new Point(50,350), 3, 1.2, new Scalar(0, 0, 255));
    					//ǰ��
    					Imgproc.line(webcam_image,up1,up2,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,up2,up3,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,up3,up1,new Scalar(0,0,255),2);
    					
    					//����
    					Imgproc.line(webcam_image,dp1,dp2,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,dp2,dp3,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,dp3,dp1,new Scalar(0,0,255),2);
    					
    					//��ת
    					Imgproc.line(webcam_image,lp1,lp2,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,lp2,lp3,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,lp3,lp1,new Scalar(0,0,255),2);
    					
    					//��ת
    					Imgproc.line(webcam_image,rp1,rp2,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,rp2,rp3,new Scalar(0,0,255),2);
    					Imgproc.line(webcam_image,rp3,rp1,new Scalar(0,0,255),2);
    				}
    				
    				panel1.setimagewithMat(webcam_image);
    				frame1.repaint();
    			}
    		}
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
    
    private static double triAngleArea(Point A, Point B, Point C) {// ����������������������������
        double result = Math.abs((A.x * B.y + B.x * C.y + C.x * A.y - B.x * A.y - C.x * B.y - A.x * C.y) / 2.0D);
        return result;
    }
    
    private static double distance(Point A, Point B) {// ����������������������������
        double result = Math.sqrt(Math.abs(A.x - B.x)*Math.abs(A.x - B.x) + Math.abs(A.y - B.y)*Math.abs(A.y - B.y));
        return result;
    }
    
    public static boolean isInTriangle(Point A, Point B, Point C, Point P) {
        double ABC = triAngleArea(A, B, C);
        double ABp = triAngleArea(A, B, P);
        double ACp = triAngleArea(A, C, P);
        double BCp = triAngleArea(B, C, P);
        if (ABC == ABp + ACp + BCp) 
        {// �����֮�͵���ԭ��������֤�������������
            return true;
        }
        else 
        {
            return false;
        }
    }
    public static void write(String message) throws MqttPersistenceException, MqttException, InterruptedException
    {
    	//mqtt�����Զ��
    	if(printByMqtt)
    	{
    		//log("��Ϣ:'"+message+"'���ͳɹ�!");  
    		mqttSCmd.service.publish(message.replace("<","").replace(">", ""));
    	}
    	//�������������
    	else
    	{
    		sp.write(message);
    	}
    }
    
    private static KeyListener kl = new KeyListener(){
		public void keyPressed(KeyEvent e) 
	    {
			try
			{
				 if (e.getKeyCode() == KeyEvent.VK_DOWN)   //����
			        {
			         	System.out.println("down");
			         	write("<BDND>");
			        }
			        else if (e.getKeyCode() == KeyEvent.VK_UP) 	//ǰ��
			        {
			        	System.out.println("up");
			        	write("<BUPD>");
			        } 
			        else if (e.getKeyCode() == KeyEvent.VK_LEFT)	//����
			        {
			        	System.out.println("left");
			        	write("<BLTD>");
			        } 
			        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) 	//����
			        {
			        	System.out.println("right");
			        	write("<BRTD>");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_W)	//ǰ��
			        {
			        	System.out.println("up");
			        	write("<BUPD>");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_S)	//����
			        {
			        	System.out.println("down");
			        	write("<BDND>");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_A)	//����
			        {
			        	System.out.println("left");
			        	write("<BLTD>");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_D)	//����
			        {
			        	System.out.println("right");
			        	write("<BRTD>");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_1)   //�ֱ�����ʱ��ת
			        {
			        	System.out.println("1");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_2)	//�ֱ�����ת
			        {
			        	System.out.println("2");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_3)	//�ֱ���˳ʱ��ת
			        {
			        	System.out.println("3");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_4)	//�ֱ�����ת
			        {
			        	System.out.println("4");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_5)	//����
			        {
			        	System.out.println("5");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_6)	//�ֱ�����ת
			        {
			        	System.out.println("6");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_7)	//�ֱ�ץ��ץ��
			        {
			        	System.out.println("7");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_8)	//�ֱ�����ת
			        {
			        	System.out.println("8");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_9)	//�ֱ�ץ�ӷſ�
			        {
			        	System.out.println("9");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD1)	//�ֱ�����ʱ��ת
			        {
			        	System.out.println("1");
			        	sendedPwm5 = sendedPwm5 + 50;
			        	write("<SDL-" + sendedPwm5 + ">"); 
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD2)	//�ֱ�����ת
			        {
			        	System.out.println("2");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD3)	//�ֱ���˳ʱ��ת
			        {
			        	System.out.println("3");
			        	sendedPwm5 = sendedPwm5 - 50;
			        	write("<SDL-" + sendedPwm5 + ">"); 
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD4)	//�ֱ�����ת
			        {
			        	System.out.println("4");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD5)	//����
			        {
			        	System.out.println("5");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD6)	//�ֱ�����ת
			        {
			        	System.out.println("6");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD7)	//�ֱ�ץ��ץ��
			        {
			        	System.out.println("7");
			        	sendedPwm6 = sendedPwm6 + 50;
			        	write("<SDR-" + sendedPwm6 + ">");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD8)	//�ֱ�����ת
			        {
			        	System.out.println("8");
			        }
			        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD9)	//�ֱ�ץ�ӷſ�
			        {
			        	System.out.println("9");
			        	sendedPwm6 = sendedPwm6 - 50;
			        	write("<SDR-" + sendedPwm6 + ">");
			        }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
	       
	    }
	  
	    public void keyTyped(KeyEvent e) {
	        // TODO Auto-generated method stub
	          
	    }
	  
	    public void keyReleased(KeyEvent e) {
	        // TODO Auto-generated method stub
	          
	    }
	};
}
