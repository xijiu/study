/**
 * 参考链接： https://blog.csdn.net/wlfbitfc/article/details/81357736
 */
public class HelloJni {
	public native void displayHelloWorld();

	static {
		System.loadLibrary("hello");//载入本地库
	}

	public static void main(String[] args) {
		new HelloJni().displayHelloWorld();
	}
}