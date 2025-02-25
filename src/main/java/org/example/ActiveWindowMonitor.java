package org.example;

import com.sun.jna.*;
import com.sun.jna.win32.*;

public class ActiveWindowMonitor {
    interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        void GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);
        Pointer GetForegroundWindow();
    }

//    public static void main(String[] args) {
//        while (true) {
//            Pointer hWnd = User32.INSTANCE.GetForegroundWindow();
//            byte[] windowText = new byte[512];
//            User32.INSTANCE.GetWindowTextA(hWnd, windowText, 512);
//            System.out.println("Active Window: " + Native.toString(windowText));
//            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
//        }
//    }
}
