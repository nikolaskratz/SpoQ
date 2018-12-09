package com.example.nikolas.spotfiyusefirsttry;

public class CustomChangeListener {

    private Listener mListener = null;
    private boolean myBoolean = false;

    public interface Listener {
        public void onStateChange(boolean state);
    }

    public void registerListener (Listener listener) {
        mListener = listener;
    }

    public void doYourWork() {
        myBoolean = true;
        if (mListener != null)
            mListener.onStateChange(myBoolean);
    }
}
