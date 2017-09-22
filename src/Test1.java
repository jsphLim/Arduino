import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;

public class Test1 {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String APPID = "598baf3b";
		try {
		
			SerialPortListener sPortListener = new SerialPortListener();
			sPortListener.listPort();
			sPortListener.openPort("COM10");
			sPortListener.startRead(0);

			Thread.sleep(2000);
			SpeechUtility.createUtility("appid=" + APPID);
			// 1.创建SpeechRecognizer对象
			SpeechRecognizer mIat = SpeechRecognizer.createRecognizer();
			// 2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
			mIat.setParameter(SpeechConstant.DOMAIN, "iat"); // 领域短信和日常用语：iat
																// (默认)；视频：video；地图：poi；音乐：music
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");// 简体中文：zh_cn（默认）；美式英文：en_us
			mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");// 方言普通话：mandarin(默认);粤
																	// 语：cantonese四川话：lmz;河南话：henanese
			mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "./tts_test.pcm"); // 识别完成后在本地保存一个音频文件
			mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "1"); // 如果不写默认是“1”，“1”是从麦克风读取声音，“-1”是从.pcm音频文件读取声音
			// mIat.setParameter(SpeechConstant.VAD_BOS, "2000");
			// //设置前端点检测时间为500ms
			mIat.setParameter(SpeechConstant.VAD_EOS, "500"); // 设置后端点检测时间为100ms
			// 3.开始听写-
			Recognizer recognizer = new Recognizer();
			
			mIat.startListening(recognizer);
			Thread.sleep(6000); //关键步骤，没有这一步的话，在还没录完音的情况下会把指令传送到串口过去，也就是把"NULL"传过去
			
			String temp= recognizer.GetReturnString();
			
			if (temp.contains("左转")) {

				sPortListener.write("<BLTD>");
			} else if (temp.contains("右转")) {

				sPortListener.write("<BRTD>");
			} else if (temp.contains("前进")) {

				sPortListener.write("<BUPD>");
			} else if (temp.contains("后退")) {
				sPortListener.write("<BDND>");
			} else if (temp.contains("停止")) {
				sPortListener.write("<BUPU>");
			} else {
				System.out.println("错误口令");
			}

			
			

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
