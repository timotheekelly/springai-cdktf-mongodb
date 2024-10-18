package com.mycompany.app;


import com.hashicorp.cdktf.App;

public class CdktfMain {

    public static void main(String[] args) {
        final App app = new App();
        new MainStack(app, "terraform-springai");
        app.synth();
    }
}
