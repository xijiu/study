package junit_test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * @author www.yoodb.com
 */
public class RSAEncrypt {
	/** 加密算法 DESEDE */
	private static String ALGORITHM = "RSA";
	/** key值大小 */
	private static int KEYSIZE = 1024;
	/** 公钥存放文件自定义 */
	private static String PUBLIC_KEY_FILE = "PublicKey";
	/** 私钥存放文件自定义 */
	private static String PRIVATE_KEY_FILE = "PrivateKey";
	/** object 对象流 */
	private static ObjectInputStream ois = null;
	/**
	 * 生成密钥对
	 */
	private static void generateKeyPair() throws Exception {
		SecureRandom sr = new SecureRandom(); //RSA算法可信任的随机数源
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM); //RSA算法创建KeyPairGenerator对象
		kpg.initialize(KEYSIZE, sr); //随机数据源初始化KeyPairGenerator对象
		KeyPair kp = kpg.generateKeyPair(); //生成密匙对
		Key publicKey = kp.getPublic(); //得到公钥
		Key privateKey = kp.getPrivate(); //得到私钥
		ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE)); //用对象流将生成的密钥写入文件
		ois.writeObject(publicKey);
		ois.close();
		ois = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
		ois.writeObject(privateKey);
		ois.close();
	}
	/**
	 * 加密方法
	 * @param source 源数据
	 * @return
	 */
	public static String encrypt(String source) throws Exception {
		generateKeyPair();
		ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		Key key = (Key) ois.readObject();
		ois.close();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = source.getBytes();
		b = cipher.doFinal(b);
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(b);
	}
	/**
	 * 解密算法
	 * @param cryptograph
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String cryptograph) throws Exception {
		ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
		Key key = (Key) ois.readObject();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(cryptograph);
		b = cipher.doFinal(b);
		return new String(b);
	}
	public static void main(String[] args) throws Exception {
		String source = "Hello World!";// 要加密的字符串
		String cryptograph = encrypt(source);// 生成的密文
		System.out.println(cryptograph);
		String target = decrypt(cryptograph);// 解密密文
		System.out.println(target);
	}
}