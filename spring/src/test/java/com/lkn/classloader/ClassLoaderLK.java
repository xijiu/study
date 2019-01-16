package com.lkn.classloader;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;



public class ClassLoaderLK extends ClassLoader {


	public static void main(String[] args) {
		String ext = "java.ext.dirs";
		System.out.println("java.ext.dirs :\n" + System.getProperty(ext));
		String cp = "java.class.path";
		System.out.println("java.class.path :\n" + System.getProperty(cp));

		ClassLoader currentClassloader = ClassLoaderLK.class.getClassLoader();

		String pp = "d:\\testcl\\";
		ClassLoaderLK cl = new ClassLoaderLK(currentClassloader, pp);

		System.out.println();
		System.out.println("currentClassloader is " + currentClassloader);
		System.out.println();
		String name = "com.lkn.classloader.AbcBean";
		try {
			Class<?> loadClass = cl.loadClass(name);
			Object object = loadClass.newInstance();
			System.out.println("\n invoke some method !\n");
			Method method = loadClass.getMethod("greeting");
			method.invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private ClassLoader parent = null; // parent classloader
	private String path;

	public ClassLoaderLK(ClassLoader parent, String path) {
		super(parent);
		this.parent = parent; // 这样做其实是无用的
		this.path = path;
	}

	public ClassLoaderLK(String path) {
		this.path = path;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> cls = findLoadedClass(name);
		if (cls == null) {
//            cls = getSystemClassLoader().loadClass(name); (2)// SystemClassLoader 会从classpath下加载
//            if (cls == null) {(2)
			// 默认情况下， 当前cl的parent是 SystemClassLoader，
			// 而当前cl的parent的parent 才是ExtClassLoader
			ClassLoader parent2 = getParent().getParent();
//                System.out.println("Classloader is : " + parent2);

			try {
				System.out.println("try to use ExtClassLoader to load class : " + name);
				cls = parent2.loadClass(name);
			} catch (ClassNotFoundException e) {
				System.out.println("ExtClassLoader.loadClass :" + name + " Failed");
			}

			if (cls == null) {
				System.out.println("try to ClassLoaderLK load class : " + name);
				cls = findClass(name);

				if (cls == null) {
					System.out.println("ClassLoaderLK.loadClass :" + name + " Failed");
				} else {
					System.out.println("ClassLoaderLK.loadClass :" + name + " Successful");
				}

			} else {
				System.out.println("ExtClassLoader.loadClass :" + name + " Successful");
			}
		}
		return cls;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Class<?> findClass(String name) throws ClassNotFoundException {
//        return super.findClass(name);
		System.out.println("try findClass " + name);
		InputStream is = null;
		Class class1 = null;
		try {
			String classPath = name.replace(".", "\\") + ".class";

			String classFile = path + classPath;
			byte[] data = getClassFileBytes(classFile);

			class1 = defineClass(name, data, 0, data.length);
			if (class1 == null) {
				System.out.println("ClassLoaderLK.findClass() ERR ");
				throw new ClassFormatError();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return class1;
	}

	private byte[] getClassFileBytes(String classFile) throws Exception {
		FileInputStream fis = new FileInputStream(classFile);
		FileChannel fileC = fis.getChannel();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		WritableByteChannel outC = Channels.newChannel(baos);
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		while (true) {
			int i = fileC.read(buffer);
			if (i == 0 || i == -1) {
				break;
			}
			buffer.flip();
			outC.write(buffer);
			buffer.clear();
		}
		fis.close();
		return baos.toByteArray();
	}

}
