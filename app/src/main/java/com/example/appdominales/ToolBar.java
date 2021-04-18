package com.example.appdominales;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToolBar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolBar extends Fragment {

    private ImageView menuButton;

    public ToolBar() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_toolbar, container, false);
        menuButton = v.findViewById(R.id.menuButton);
        return v;
    }

    public void setMenuButtonOnClick(View.OnClickListener pOnclickListener){
        menuButton.setOnClickListener(pOnclickListener);
    }
}