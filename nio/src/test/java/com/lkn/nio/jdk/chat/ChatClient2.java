package com.lkn.nio.jdk.chat;

import java.io.IOException;

public class ChatClient2 {
    public static void main(String[] args) {
        try {
            new ChatClient().startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
