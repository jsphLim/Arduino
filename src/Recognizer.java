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
 * Created by Mshu 2017/7/11. 语音识别回掉
 */
public class Recognizer implements RecognizerListener {
	// 开始录音

	private String ReturnString="NULL";
	
	public void onBeginOfSpeech() {
		System.out.println("..开始录音..");
	}

	// 音量值0~30
	public void onVolumeChanged(int volume) {
		/* System.out.println("当前音量"+volume); */
	}

	// 结束录音
	public void onEndOfSpeech() {
		System.out.println("录音结束");
	}

	// 扩展用接口
	public void onEvent(int eventType, int arg1, int arg2, String msg) {
	}

	// 听写结果回调接口(返回Json格式结果，用户可参见附录)；
	// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
	// 关于解析Json的代码可参见MscDemo中JsonParser类；
	// isLast等于true时会话结束。
	public void onResult(RecognizerResult results, boolean isLast) {
		 System.out.println("Result:"+results.getResultString ());
		if(results.getResultString()=="NULL"){
			//确保直接受到说的第一句话，防止外界干扰
		ReturnString = results.getResultString();
		}

		
		// 结果实例
		// Result:{"sn":1,"ls":false,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[{"sc":0.00,"w":"你好"}]}]}
		// sn:第几句
		// ls:是否是第一句
		// bg：开始
		// ed:结束
		// ws:词语
		// cw:中文分词
		// w:单词
		// sc:分数
	
	}

	// 会话发生错误回调接口
	public void onError(SpeechError error) {
		// error.getPlainDescription(true); //获取错误码描述
		System.out.println(error.getErrorDesc());
	}
	
	public String GetReturnString(){
		return ReturnString; //保留接口
	}
}
