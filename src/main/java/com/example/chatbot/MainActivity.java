package com.example.chatbot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        // Setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Check for permissions
        checkPermissions();

        // 1. Starts with greeting the user while opening the application
        addBotMessage("Hello! 👋 I'm your responsive ChatBot. How can I help you today?");

        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            if (!question.isEmpty()) {
                // 2. Starts communicating after the user starts communicating
                addUserMessage(question);
                messageEditText.setText("");
                handleBotResponse(question);
            }
        });
    }

    private void addUserMessage(String message) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, Message.SENT_BY_ME));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        });
    }

    private void addBotMessage(String message) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, Message.SENT_BY_BOT));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        });
    }

    private void handleBotResponse(String question) {
        // Responsive behavior: Show "Typing..."
        Message typingMessage = new Message("Typing...", Message.SENT_BY_BOT);
        runOnUiThread(() -> {
            messageList.add(typingMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        });

        // Simulate thinking time
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                int index = messageList.indexOf(typingMessage);
                if (index != -1) {
                    messageList.remove(index);
                    messageAdapter.notifyItemRemoved(index);
                }
                
                String response = generateResponse(question);
                addBotMessage(response);
            });
        }, 1500);
    }

    private String generateResponse(String question) {
        String input = question.toLowerCase();
        if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return "Hi there! I'm here and ready to chat. What's on your mind?";
        } else if (input.contains("how are you")) {
            return "I'm functioning perfectly! Ready to assist you with anything. How about you?";
        } else if (input.contains("name")) {
            return "I am your Android Assistant. You can call me Chatty!";
        } else if (input.contains("help")) {
            return "I can answer questions, tell jokes, or just keep you company. Ask me anything!";
        } else if (input.contains("joke")) {
            return "Why did the smartphone go to the doctor? It had a bad 'cell' signal! 📱😂";
        } else if (input.contains("bye") || input.contains("goodbye")) {
            return "Goodbye! Have a fantastic day ahead!";
        } else {
            return "That's interesting! I'm still learning, but I'd love to hear more about that.";
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}