package com.example.myapplication;

import static android.app.Service.START_STICKY;

import static java.sql.DriverManager.println;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlinx.coroutines.channels.Send;

public class AddSubscription extends AppCompatActivity {

    private List<Subscription> subscriptions = new ArrayList<>();
    private TextView monthlyCostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText subscriptionNameEditText = findViewById(R.id.subscriptionName);
        EditText subscriptionCostEditText = findViewById(R.id.subscriptionCost);
        DatePicker subscriptionDatePicker = findViewById(R.id.subscriptionDatePicker);
        Button addButton = findViewById(R.id.addButton);
        Button viewSubscriptionsButton = findViewById(R.id.viewSubscriptionsButton);
        monthlyCostTextView = findViewById(R.id.monthlyCost);

        addButton.setOnClickListener(v -> {
            String name = subscriptionNameEditText.getText().toString();
            String costStr = subscriptionCostEditText.getText().toString();

            int day = subscriptionDatePicker.getDayOfMonth();
            int month = subscriptionDatePicker.getMonth();
            int year = subscriptionDatePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);

            if (name.isEmpty() || costStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double cost = Double.parseDouble(costStr);
            Subscription subscription = new Subscription(name, cost, date);
            subscriptions.add(subscription);
            updateMonthlyCost();
            
            scheduleNotification(subscription);
            
//            SendNotification(subscription);

        });

        viewSubscriptionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddSubscription.this, SubscriptionListActivity.class);
            intent.putExtra("subscriptions", (Serializable) subscriptions);
            startActivity(intent);
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Subscription Reminders";
            String description = "Channel for subscription renewal reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private void SendNotification(Subscription subscription) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Subscription Reminder")
                .setContentText("Jutro masz opłatę za " + subscription.getName() + " w wysokości $"+ subscription.getCost())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, builder.build());
    }
    private void scheduleNotification(Subscription subscription) {
        try {
            Calendar renewalDate = Calendar.getInstance();
            String[] dateParts = subscription.getRenewalDate().split("-");
            renewalDate.set(
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[2])
            );

            renewalDate.add(Calendar.DAY_OF_MONTH,  -1);

            Calendar now = Calendar.getInstance();
            Toast.makeText(this, "Renewal Date: " + renewalDate.get(Calendar.DAY_OF_MONTH) + "/" + renewalDate.get(Calendar.MONTH) + "/" + renewalDate.get(Calendar.YEAR), Toast.LENGTH_SHORT).show();
             if (renewalDate.after(now)) {
                 Toast.makeText(this, "Renewal Date is after now", Toast.LENGTH_SHORT).show();
                Calendar dayBeforeRenewal = (Calendar) renewalDate.clone();
                dayBeforeRenewal.add(Calendar.DAY_OF_MONTH, -1);
                if (dayBeforeRenewal.before(now)) {
                    Toast.makeText(this, "Day before renewal date is before now", Toast.LENGTH_SHORT).show();
                    SendNotification(subscription);
                }
             }
            } catch (Exception e) {
            e.printStackTrace();
         }
    }

    private void updateMonthlyCost() {
        double totalCost = 0;
        for (Subscription subscription : subscriptions) {
            totalCost += subscription.getCost();
        }
        monthlyCostTextView.setText("Total Monthly Cost: $" + String.format("%.2f", totalCost));
    }

}

class Subscription implements Serializable {
    private String name;
    private double cost;
    private String renewalDate;

    public Subscription(String name, double cost, String renewalDate) {
        this.name = name;
        this.cost = cost;
        this.renewalDate = renewalDate;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getRenewalDate() {
        return renewalDate;
    }
}