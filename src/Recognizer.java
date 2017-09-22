import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.b.b.e;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import jdk.nashorn.internal.parser.JSONParser;

/**
 * Created by Mshu 2017/7/11. ����ʶ��ص�
 */
public class Recognizer implements RecognizerListener {
	// ��ʼ¼��

	private String ReturnString="NULL";
	
	public void onBeginOfSpeech() {
		System.out.println("..��ʼ¼��..");
	}

	// ����ֵ0~30
	public void onVolumeChanged(int volume) {
		/* System.out.println("��ǰ����"+volume); */
	}

	// ����¼��
	public void onEndOfSpeech() {
		System.out.println("¼������");
	}

	// ��չ�ýӿ�
	public void onEvent(int eventType, int arg1, int arg2, String msg) {
	}

	// ��д����ص��ӿ�(����Json��ʽ������û��ɲμ���¼)��
	// һ������»�ͨ��onResults�ӿڶ�η��ؽ����������ʶ�������Ƕ�ν�����ۼӣ�
	// ���ڽ���Json�Ĵ���ɲμ�MscDemo��JsonParser�ࣻ
	// isLast����trueʱ�Ự������
	public void onResult(RecognizerResult results, boolean isLast) {
		 System.out.println("Result:"+results.getResultString ());
		if(results.getResultString()=="NULL"){
			//ȷ��ֱ���ܵ�˵�ĵ�һ�仰����ֹ������
		ReturnString = results.getResultString();
		}

		
		// ���ʵ��
		// Result:{"sn":1,"ls":false,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[{"sc":0.00,"w":"���"}]}]}
		// sn:�ڼ���
		// ls:�Ƿ��ǵ�һ��
		// bg����ʼ
		// ed:����
		// ws:����
		// cw:���ķִ�
		// w:����
		// sc:����
	
	}

	// �Ự��������ص��ӿ�
	public void onError(SpeechError error) {
		// error.getPlainDescription(true); //��ȡ����������
		System.out.println(error.getErrorDesc());
	}
	
	public String GetReturnString(){
		return ReturnString; //�����ӿ�
	}
}
