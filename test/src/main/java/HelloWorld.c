#include "jni.h"
#include "helloJni.h"
#include <stdio.h>
#include <stdlib.h>
//方法名必须为本地方法的全类名点改为下划线，传入的两个参数必须这样写，
//第一个参数为Java虚拟机的内存地址的二级指针，用于本地方法与java虚拟机在内存中交互
//第二个参数为一个java对象，即是哪个对象调用了这个 c方法
/*
函数名及返回值类型需一致
*/
JNIEXPORT void JNICALL Java_helloJni_displayHelloWorld(JNIEnv * env, jobject obj)

        {

          printf("c_lib::: ok!You have successfully passed the Java call c\n");
          if (1 == 1) {
            while (1) {
                // 一直执行本地代码
            }
          }
          printf("c_lib::: over\n");
        }