package group1.tcss450.uw.edu.bsanews;

import android.app.Activity;
import android.os.Bundle;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;



public class AuthenticatorActivity extends Activity {

    public static AWSCredentialsProvider credentialsProvider;
    public static AWSConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // Add a call to initialize AWSMobileClient
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {



                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
                signin.login(AuthenticatorActivity.this, MainActivity.class).execute();
            }
        }).execute();

        credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        configuration = AWSMobileClient.getInstance().getConfiguration();
    }
}
