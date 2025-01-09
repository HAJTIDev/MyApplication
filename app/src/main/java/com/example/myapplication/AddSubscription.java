package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddSubscription extends AppCompatActivity {

    private List<Subscription> subscriptions = new ArrayList<>();
    private TextView monthlyCostTextView;
    private Subscription newSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            newSubscription = new Subscription(name, cost, date);
            subscriptions.add(newSubscription);
            updateMonthlyCost();
        });

        viewSubscriptionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddSubscription.this, SubscriptionListActivity.class);
            intent.putExtra("subscription", newSubscription);
            startActivity(intent);
        });
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