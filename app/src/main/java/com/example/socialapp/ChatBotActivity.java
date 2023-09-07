package com.example.socialapp;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.example.socialapp.adapter.MessageAdapter;
import com.example.socialapp.databinding.ActivityChatBotBinding;
import com.example.socialapp.models.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatBotActivity extends AppCompatActivity {
    ActivityChatBotBinding binding;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    TextToSpeech speak;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        messageList = new ArrayList<>();

        messageAdapter = new MessageAdapter(messageList);
        binding.recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = binding.messageEditText.getText().toString().trim();
                addToChat(question,Message.SENT_BY_ME);
                binding.messageEditText.setText("");
                callAPI(question);
                binding.welcomeText.setVisibility(View.GONE);
            }
        });
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if(result.getData() != null){
                                ArrayList<String> data = result.getData().getStringArrayListExtra(
                                        RecognizerIntent.EXTRA_RESULTS);
                                binding.messageEditText.setText(Objects.requireNonNull(data).get(0));
                            }else{
                                Toast.makeText(ChatBotActivity.this, "Oops! Dzui lòng thử lại.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        binding.voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speak to text");
                try {
                    activityResultLauncher.launch(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(ChatBotActivity.this, "Loi roi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        speak = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    speak.setLanguage(Locale.getDefault());
                }
            }
        });

    }


    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                binding.recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });

    }
    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    void callAPI(String question){

        messageList.add(new Message("Typing...",Message.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo");

            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role","user");
            obj.put("content",question);
            messageArr.put(obj);

            jsonBody.put("messages",messageArr);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer yourapikey")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                        speak.speak(result.trim(),TextToSpeech.QUEUE_FLUSH,null, UUID.randomUUID().toString());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    addResponse("Failed to load response due to "+response.body().toString());
                }
            }
        });

    }
}