//============================亚博智能===========================
//  智能小车前后左右综合实验
//  蓝牙和红外避障实验（带后退掉头避障）
//===============================================================
int Left_motor_back=9;     //左电机后退(IN1)
int Left_motor_go=5;     //左电机前进(IN2)

int Right_motor_go=6;    // 右电机前进(IN3)
int Right_motor_back=10;    // 右电机后退(IN4)

//int key=A0;//定义按键 A0 接口
//int beep=A1;//定义蜂鸣器 A1 接口

const int SensorRight = A0;   	//右循迹红外传感器(P3.2 OUT1)
const int SensorLeft = A1;     	//左循迹红外传感器(P3.3 OUT2)

const int SensorLeft_2 = A2;     //左红外传感器(P3.4 OUT3)
const int SensorRight_2 = A3;    //右红外传感器(P3.5 OUT4)

int SL;    //左循迹红外传感器状态
int SR;    //右循迹红外传感器状态
int SL_2;    //左红外传感器状态
int SR_2;    //右红外传感器状态

String comdata ="";//接收的字符串 
int index;  //字符串分割时用于标","的位置
String type;  //命令类型。
int car_speed=0;  //速度

int servopin=7;//设置舵机驱动脚到数字口9
int myangle;//定义角度变量
int pulsewidth;//定义脉宽变量
int val;

int Echo = A5;  // Echo回声脚(P2.0)
int Trig =A4;  //  Trig 触发脚(P2.1)

int Distance = 0;

void setup()
{
  //初始化电机驱动IO为输出方式
  pinMode(Left_motor_go,OUTPUT); // PIN 8 (PWM)
  pinMode(Left_motor_back,OUTPUT); // PIN 9 (PWM)
  pinMode(Right_motor_go,OUTPUT);// PIN 10 (PWM) 
  pinMode(Right_motor_back,OUTPUT);// PIN 11 (PWM)
  //pinMode(key,INPUT);//定义按键接口为输入接口
  //pinMode(beep,OUTPUT);
  pinMode(SensorRight, INPUT); //定义右循迹红外传感器为输入
  pinMode(SensorLeft, INPUT); //定义左循迹红外传感器为输入
  pinMode(SensorRight_2, INPUT); //定义右红外传感器为输入
  pinMode(SensorLeft_2, INPUT); //定义左红外传感器为输入
  
  pinMode(servopin,OUTPUT);//设定舵机接口为输出接口

    //初始化超声波引脚
  pinMode(Echo, INPUT);    // 定义超声波输入脚
  pinMode(Trig, OUTPUT);   // 定义超声波输出脚                 
  
  Serial.begin(9600);//串口波特率  
}

void servopulse(int servopin,int myangle)/*定义一个脉冲函数，用来模拟方式产生PWM值*/
{
  pulsewidth=(myangle*11)+500;//将角度转化为500-2480 的脉宽值
  digitalWrite(servopin,HIGH);//将舵机接口电平置高
  delayMicroseconds(pulsewidth);//延时脉宽值的微秒数
  digitalWrite(servopin,LOW);//将舵机接口电平置低
  delay(20-pulsewidth/1000);//延时周期内剩余时间
}


void Distance_test()   // 量出前方距离 
{
  digitalWrite(Trig, LOW);   // 给触发脚低电平2μs
  delayMicroseconds(2);
  digitalWrite(Trig, HIGH);  // 给触发脚高电平10μs，这里至少是10μs
  delayMicroseconds(10);
  digitalWrite(Trig, LOW);    // 持续给触发脚低电
  float Fdistance = pulseIn(Echo, HIGH);  // 读取高电平时间(单位：微秒)
  Fdistance= Fdistance/58;       //为什么除以58等于厘米，  Y米=（X秒*344）/2
  // X秒=（ 2*Y米）/344 ==》X秒=0.0058*Y米 ==》厘米=微秒/58
 // Serial.print("Distance:");      //输出距离（单位：厘米）
  Serial.println(Fdistance);         //显示距离
  Distance = Fdistance;

}  

void run(int time)     // 前进
{
       // Serial.println("run"); 
         //     Serial.println("car_speed="+car_speed);
	for (int i=0; i <= time; i++){
		//有信号为LOW  没有信号为HIGH
		SR_2 = digitalRead(SensorRight_2);
		SL_2 = digitalRead(SensorLeft_2);
        
	  if(Distance<=15){
       break;
    }
                    //Serial.println("running");
                    digitalWrite(Right_motor_go,HIGH);  // 右电机前进
                    digitalWrite(Right_motor_back,LOW);     
                    analogWrite(Right_motor_go,100);//PWM比例0~255调速，左右轮差异略增减
                    analogWrite(Right_motor_back,0);
                    digitalWrite(Left_motor_go,HIGH);  // 左电机前进
                    digitalWrite(Left_motor_back,LOW);
                    analogWrite(Left_motor_go,100);//PWM比例0~255调速，左右轮差异略增减
                    analogWrite(Left_motor_back,0);
  		    delay(100);	//执行时间，可以调整 
			  //Serial.println("finish running");
		
	}
}

void runWithAvoid(int time)     // 前进
{
	for (int i=0; i <= time;){
		//有信号为LOW  没有信号为HIGH
		SR_2 = digitalRead(SensorRight_2);
		SL_2 = digitalRead(SensorLeft_2);
		if(SL_2 == LOW)
		{
                       // Serial.println("a"); 
			back(1); //后退0.2s
                        delay(400);
                        spin_right(1);  //然后右转
                        delay(400);
                        run(5);  //前进
                        delay(400);
                        spin_left(1);  //然后左转
                        delay(400);
                        i++;
		}
                else if(SR_2==LOW)
		{
                     //   Serial.println("b"); 
			back(1); //后退0.2s
                        delay(400);
                        spin_left(1);  //然后左转
                        delay(400);
                        run(5);  //前进
                        delay(400);
                        spin_right(1);  //然后右转
                        delay(400);
                        i++;
		}
		else
		{
                    digitalWrite(Right_motor_go,HIGH);  // 右电机前进
                    digitalWrite(Right_motor_back,LOW);     
                    analogWrite(Right_motor_go,100);//PWM比例0~255调速，左右轮差异略增减
                    analogWrite(Right_motor_back,0);
                    digitalWrite(Left_motor_go,HIGH);  // 左电机前进
                    digitalWrite(Left_motor_back,LOW);
                    analogWrite(Left_motor_go,100);//PWM比例0~255调速，左右轮差异略增减
                    analogWrite(Left_motor_back,0);
  		    delay(100);	//执行时间，可以调整 
		    i++;	
		}
	}
}

void brake(int time)         //刹车，停车
{
  digitalWrite(Right_motor_go,LOW);
  digitalWrite(Right_motor_back,LOW);
  digitalWrite(Left_motor_go,LOW);
  digitalWrite(Left_motor_back,LOW);
  delay(time * 100);//执行时间，可以调整  
}

void left(int time)         //左转(左轮不动，右轮前进)
{
      //  Serial.println("left"); 
	for (int i=0; i <= time; i++){
		//有信号为LOW  没有信号为HIGH
		SR_2 = digitalRead(SensorRight_2);
		SL_2 = digitalRead(SensorLeft_2);
 if(Distance<=15){
       break;
    }

      digitalWrite(Right_motor_go,HIGH);	// 右电机前进
			digitalWrite(Right_motor_back,LOW);
			analogWrite(Right_motor_go,100); 
			analogWrite(Right_motor_back,0);//PWM比例0~255调速
			digitalWrite(Left_motor_go,LOW);   //左轮不动
			digitalWrite(Left_motor_back,LOW);
			analogWrite(Left_motor_go,0); 
			analogWrite(Left_motor_back,0);//PWM比例0~255调速
			delay(100);	//执行时间，可以调整 
			
		
	}
   
}

void spin_left(int time)         //左转(左轮后退，右轮前进)
{
     //   Serial.println("spin_left"); 
	for (int i=0; i <= time; i++){
		//有信号为LOW  没有信号为HIGH
		SR_2 = digitalRead(SensorRight_2);
		SL_2 = digitalRead(SensorLeft_2);
		 if(Distance<=15){
       break;
    }
      digitalWrite(Right_motor_go,HIGH);	// 右电机前进
			digitalWrite(Right_motor_back,LOW);
			analogWrite(Right_motor_go,80); 
			analogWrite(Right_motor_back,0);//PWM比例0~255调速
			digitalWrite(Left_motor_go,LOW);   //左轮后退
			digitalWrite(Left_motor_back,HIGH);
			analogWrite(Left_motor_go,0); 
			analogWrite(Left_motor_back,80);//PWM比例0~255调速
			delay(100);	//执行时间，可以调整  
			
		
	}

}

void right(int time)        //右转(右轮不动，左轮前进)
{
      //  Serial.println("right"); 
	for (int i=1;i<=time;i++)
	{
		//有信号为LOW  没有信号为HIGH
		SR_2 = digitalRead(SensorRight_2);
		SL_2 = digitalRead(SensorLeft_2);

	 if(Distance<=15){
       break;
    }
			
      digitalWrite(Right_motor_go,LOW);   //右电机不动
			digitalWrite(Right_motor_back,LOW);
			analogWrite(Right_motor_go,0); 
			analogWrite(Right_motor_back,0);//PWM比例0~255调速
			digitalWrite(Left_motor_go,HIGH);//左电机前进
			digitalWrite(Left_motor_back,LOW);
			analogWrite(Left_motor_go,100); 
			analogWrite(Left_motor_back,0);//PWM比例0~255调速
			delay(100);	//执行时间，可以调整  
		
	}
  
}

void spin_right(int time)        //右转(右轮后退，左轮前进)
{
     //   Serial.println("spin_right"); 
	for(int i=1;i<=time;i++)
	{
		//有信号为LOW  没有信号为HIGH
		SR_2 = digitalRead(SensorRight_2);
		SL_2 = digitalRead(SensorLeft_2);
	 if(Distance<=15){
       break;
    }

    digitalWrite(Right_motor_go,LOW);   //右电机后退
			digitalWrite(Right_motor_back,HIGH);
			analogWrite(Right_motor_go,0); 
			analogWrite(Right_motor_back,80);//PWM比例0~255调速
			digitalWrite(Left_motor_go,HIGH);//左电机前进
			digitalWrite(Left_motor_back,LOW);
			analogWrite(Left_motor_go,80); 
			analogWrite(Left_motor_back,0);//PWM比例0~255调速
			delay(100);	//执行时间，可以调整 
		
	}
   
}

void back(int time)          //后退
{
     //   Serial.println("back"); 
	digitalWrite(Right_motor_go,LOW);  //右轮后退
	digitalWrite(Right_motor_back,HIGH);
	analogWrite(Right_motor_go,0);
	analogWrite(Right_motor_back,150);//PWM比例0~255调速
	digitalWrite(Left_motor_go,LOW);  //左轮后退
	digitalWrite(Left_motor_back,HIGH);
	analogWrite(Left_motor_go,0);
	analogWrite(Left_motor_back,150);//PWM比例0~255调速
	delay(time * 100);     //执行时间，可以调整  
 
}

/*
void keysacn()//按键扫描
{
  int val;
  val=digitalRead(key);//读取数字7 口电平值赋给val
  while(!digitalRead(key))//当按键没被按下时，一直循环
  {
    val=digitalRead(key);//此句可省略，可让循环跑空
  }
  while(digitalRead(key))//当按键被按下时
  {
    delay(10);	//延时10ms
    val=digitalRead(key);//读取数字7 口电平值赋给val
    if(val==HIGH)  //第二次判断按键是否被按下
    {
      digitalWrite(beep,HIGH);		//蜂鸣器响
      while(!digitalRead(key))	//判断按键是否被松开
        digitalWrite(beep,LOW);		//蜂鸣器停止
    }
    else
      digitalWrite(beep,LOW);//蜂鸣器停止
  }
}
*/

void loop()
{
//  delay(2000); //延时2s后启动
//  back(10); //后退1s
//  brake(5);//停止0.5s
//  run(10);//前进1s
//  brake(5);//停止0.5s
//  left(10);//向左转1s
//  right(10);//向右转1s
//  spin_right(20); //向右旋转2s
//  spin_left(20);//向左旋转2s
//  brake(5);//停车
  
	//keysacn();	   //调用按键扫描函数
   if(Serial.available() > 0){  
        /* 接收字符串 */  
        while (Serial.available() > 0)    
        {  
            comdata += char(Serial.read());  
            delay(2);  
        }  
        if (comdata.length() > 0)
        {  
           int index = comdata.indexOf(',');
           type = comdata.substring(0, index);
           //car_speed = comdata.substring(index + 1, comdata.length()).toInt();
          car_speed=6;
            //  Serial.println("type=" + type); 
          //  Serial.println("car_speed="+ car_speed); 
            //向左
            if(type=="4"){  
             //   Serial.println("left"); 
                 left(car_speed);//向左转
                 brake(1);//停止0.1s
            }  
            
             //向左
            if(type=="1"){  
              //  Serial.println("left"); 
                 spin_left(car_speed);//向左转
                 brake(1);//停止0.1s
            }  
            
             //向右
            if(type=="6"){  
            //    Serial.println("right");
                 right(car_speed);//向右转
                  brake(1);//停止0.1s
            }  
            
             //向右
            if(type=="3"){  
             //   Serial.println("right");
                 spin_right(car_speed);//向右转
                  brake(1);//停止0.1s
            }  
            
            //向前
            if(type=="8"){  
                Serial.println("run");  
                 run(car_speed);//前进
                  brake(1);//停止0.1s
            } 
            //向前
            if(type=="88"){  
           //     Serial.println("run");  
                 runWithAvoid(car_speed);//前进
                  brake(1);//停止0.1s
            }  
            
             //向后
             if(type=="2"){  
           //     Serial.println("car back");  
                 back(car_speed); //后退
                  brake(1);//停止0.1s
            }  
            
            //原地不动
             if(type=="5"){  
               // Serial.println("stop");  
                 brake(car_speed);//停止0.1s
            } 
            
            //陀机
            if(type=="10"){  
                val=car_speed*(180/9);//将数字转化为角度，例9*（180/9）=180
              //  Serial.print("moving servo to ");
	//	Serial.print(val,DEC);
	//	Serial.println();
		for(int i=0;i<=50;i++) //产生PWM个数，等效延时以保证能转到响应角度
		{
		    servopulse(servopin,val);//模拟产生PWM
		}
            }  
            comdata = "";  
        }  
          
    }  

     Distance_test();


  delay(250);

}


