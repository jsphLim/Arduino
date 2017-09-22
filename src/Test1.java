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
			// 1.����SpeechRecognizer����
			SpeechRecognizer mIat = SpeechRecognizer.createRecognizer();
			// 2.������д�����������MSC Reference Manual��SpeechConstant��
			mIat.setParameter(SpeechConstant.DOMAIN, "iat"); // ������ź��ճ����iat
																// (Ĭ��)����Ƶ��video����ͼ��poi�����֣�music
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");// �������ģ�zh_cn��Ĭ�ϣ�����ʽӢ�ģ�en_us
			mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");// ������ͨ����mandarin(Ĭ��);��
																	// �cantonese�Ĵ�����lmz;���ϻ���henanese
			mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "./tts_test.pcm"); // ʶ����ɺ��ڱ��ر���һ����Ƶ�ļ�
			mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "1"); // �����дĬ���ǡ�1������1���Ǵ���˷��ȡ��������-1���Ǵ�.pcm��Ƶ�ļ���ȡ����
			// mIat.setParameter(SpeechConstant.VAD_BOS, "2000");
			// //����ǰ�˵���ʱ��Ϊ500ms
			mIat.setParameter(SpeechConstant.VAD_EOS, "500"); // ���ú�˵���ʱ��Ϊ100ms
			// 3.��ʼ��д-
			Recognizer recognizer = new Recognizer();
			
			mIat.startListening(recognizer);
			Thread.sleep(6000); //�ؼ����裬û����һ���Ļ����ڻ�û¼����������»��ָ��͵����ڹ�ȥ��Ҳ���ǰ�"NULL"����ȥ
			
			String temp= recognizer.GetReturnString();
			
			if (temp.contains("��ת")) {

				sPortListener.write("<BLTD>");
			} else if (temp.contains("��ת")) {

				sPortListener.write("<BRTD>");
			} else if (temp.contains("ǰ��")) {

				sPortListener.write("<BUPD>");
			} else if (temp.contains("����")) {
				sPortListener.write("<BDND>");
			} else if (temp.contains("ֹͣ")) {
				sPortListener.write("<BUPU>");
			} else {
				System.out.println("�������");
			}

			
			

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
