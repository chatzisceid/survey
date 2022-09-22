package com.example.survey;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.survey.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

public class SecondFragment extends Fragment {

    public int QUESTION = 0;
    public int[] FLAG_QUESTION = new int[20];
    public int QUESTION_SUBMITTED = 0;

    private FragmentSecondBinding binding;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://powerful-peak-54206.herokuapp.com/questions")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            //JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
            String a = response.body().string();
            System.out.println(a);
            String b = a.substring(1,a.length() - 1);
            String[] strArray = null;
            strArray = b.split(Pattern.quote("},"));
            Log.d("Myapp", String.valueOf(strArray.length));
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i< strArray.length; i++){
                strArray[i] = strArray[i] + "}";
                JSONObject jobj = new JSONObject();
                try {
                    jobj = new JSONObject(strArray[i]);
                    Log.d("MyApp",jobj.toString());
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
                jsonArray.put(jobj);
                FLAG_QUESTION[i] = 0;
            }
            Log.d("Myapp", String.valueOf(jsonArray.length()));
            Log.d("Myapp", String.valueOf(jsonArray.getJSONObject(QUESTION)));

            binding.questNum.setText("Question "+String.valueOf(jsonArray.getJSONObject(QUESTION).getString("id"))+"/"+String.valueOf(jsonArray.length()));
            binding.quest.setText(String.valueOf(jsonArray.getJSONObject(QUESTION).getString("question")));

            binding.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QUESTION++;
                    binding.answer.setText("");
                    if(QUESTION==jsonArray.length()-1)
                        binding.next.setVisibility(View.INVISIBLE);
                    binding.previous.setVisibility(View.VISIBLE);
                    try {
                        binding.questNum.setText("Question "+String.valueOf(jsonArray.getJSONObject(QUESTION).getString("id"))+"/"+String.valueOf(jsonArray.length()));
                        binding.quest.setText(String.valueOf(jsonArray.getJSONObject(QUESTION).getString("question")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(FLAG_QUESTION[QUESTION]==1){
                        binding.buttonSecond.setText("Submited");
                        binding.buttonSecond.setClickable(false);
                    }else{
                        binding.buttonSecond.setText("Submit");
                        binding.buttonSecond.setClickable(true);
                    }
                }
            });

            binding.previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.answer.setText("");
                    QUESTION--;
                    if(QUESTION==0) binding.previous.setVisibility(View.INVISIBLE);
                    binding.next.setVisibility(View.VISIBLE);
                    try {
                        binding.questNum.setText("Question "+String.valueOf(jsonArray.getJSONObject(QUESTION).getString("id"))+"/"+String.valueOf(jsonArray.length()));
                        binding.quest.setText(String.valueOf(jsonArray.getJSONObject(QUESTION).getString("question")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(FLAG_QUESTION[QUESTION]==1){
                        binding.buttonSecond.setText("Submited");
                        binding.buttonSecond.setClickable(false);
                    }else{
                        binding.buttonSecond.setText("Submit");
                        binding.buttonSecond.setClickable(true);
                    }
                }
            });

            binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String content = binding.answer.getText().toString();
                    Log.d("Myaa", content);
                    if(!content.equals("")){
                        try {
                            String body = "{\"id\":" +String.valueOf(jsonArray.getJSONObject(QUESTION).getString("id"))+ ",\"answer\":\"" +content+ "\"}";
                            Log.d("Myaa",body);
                            RequestBody formBody = new FormBody.Builder()
                                    .add("raw",body)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("https://powerful-peak-54206.herokuapp.com/question/submit")
                                    .post(formBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (!response.isSuccessful()){
                                Toast.makeText(getContext(), "Your answer was NOT submitted successfully. Try submitting again", Toast.LENGTH_LONG).show();
                            }
                            if(response.code()==200){
                                FLAG_QUESTION[QUESTION]=1;
                                binding.buttonSecond.setText("Submited");
                                binding.buttonSecond.setClickable(false);
                                Toast.makeText(getContext(), "Your answer was submitted successfully", Toast.LENGTH_LONG).show();
                                QUESTION_SUBMITTED++;
                                binding.questSub.setText("Questions submitted: "+QUESTION_SUBMITTED);
                            }
                        }catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "Your answer was NOT submitted successfully. Write an answer then submit it", Toast.LENGTH_LONG).show();
                    }
                }
            });

            binding.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_FirstFragment);
                }
            });

        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
