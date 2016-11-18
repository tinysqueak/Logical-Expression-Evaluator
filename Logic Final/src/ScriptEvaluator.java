import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEvaluator {

	public static void main(String[] args) {

		String boolexpr1 = "(!(T||false)&&!((true)^(true)))";
		String boolexpr2 = "false||true&&!false";

		boolean result = false;

		try {
			result = (Boolean) new ScriptEngineManager().getEngineByName("javascript").eval(boolexpr2);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(result);

	}

}
