package com.amine.blog.repositories;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SignInFile {
    private final File signInFileOfGuest;
    private final Context context;

    public SignInFile(Context context){
        this.context = context;
        String fileName = "sign-in-info.txt";
        this.signInFileOfGuest = new File(context.getCacheDir(), fileName);
    }

    public String readFile(){
        String guestName = "";
        try {
            if(signInFileOfGuest.exists()){
                Scanner scanner = new Scanner(signInFileOfGuest);
                if(scanner.hasNextLine()){
                    guestName = scanner.nextLine();
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();;
        }

        return guestName;
    }

    public void writeFile(String guestName){
        try {
            PrintWriter pr = new PrintWriter(signInFileOfGuest);
            pr.println(guestName);
            pr.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();;
        }
    }

    public void createFile(){
        try {
            boolean isCreated = signInFileOfGuest.createNewFile();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();;
        }
    }

    public void deleteFile(){
        try {
            boolean isDeleted = signInFileOfGuest.delete();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();;
        }
    }

}
