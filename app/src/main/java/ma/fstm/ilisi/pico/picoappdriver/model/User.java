package ma.fstm.ilisi.pico.picoappdriver.model;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

public class User {
    @NonNull
    private String mPhoneNumber;
    @NonNull
    private String mPassword;

    public User(@NonNull final String PhoneNumber, @NonNull final String password) {
        mPhoneNumber = PhoneNumber;
        mPassword = password;
    }

    @NonNull
    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(@NonNull String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    @NonNull
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@NonNull final String password) {
        mPassword = password;
    }

    public boolean isInputDataValid() {
        return !TextUtils.isEmpty(getmPhoneNumber()) && Patterns.PHONE.matcher(getmPhoneNumber()).matches() && getPassword().length() > 5;
    }
}