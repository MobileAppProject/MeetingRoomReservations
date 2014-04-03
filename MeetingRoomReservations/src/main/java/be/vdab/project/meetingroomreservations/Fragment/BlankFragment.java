package be.vdab.project.meetingroomreservations.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import be.vdab.project.meetingroomreservations.R;


public class BlankFragment extends Fragment {

    private String userName, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.testfragment, container, false);

        Button bLogin = (Button) view.findViewById(R.id.buttonLogin);
        final EditText etUserName = (EditText) view.findViewById(R.id.etUserName);
        final EditText etPassword = (EditText) view.findViewById(R.id.etPassword);


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = etUserName.getText().toString();
                password = etPassword.getText().toString();
                Toast.makeText(getActivity(), "userName: " + userName + "\npassword: " + password, Toast.LENGTH_SHORT).show();

            }
        });

        // Inflate the layout for this fragment
        return view;
    }



}
