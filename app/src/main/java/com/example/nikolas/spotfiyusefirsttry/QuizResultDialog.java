package com.example.nikolas.spotfiyusefirsttry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class QuizResultDialog extends DialogFragment {



    public static QuizResultDialog newInstance(String[] content) {

        QuizResultDialog frag = new QuizResultDialog();
        Bundle args = new Bundle();
        args.putString("title", content[0]);
        args.putString("msg1", content[1]);
        args.putString("msg2", content[2]);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.QuizResultDialog);
        String title = getArguments().getString("title");
        String msg1 = getArguments().getString("msg1");
        String msg2 = getArguments().getString("msg2");
        builder.setTitle(title);
        builder.setMessage(msg1+"\n"+msg2);
        return builder.create();
    }
}
