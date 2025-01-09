package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SubscriptionListActivity extends AppCompatActivity {

    private LinearLayout subscriptionListLayout;
    private List<Subscription> subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        subscriptionListLayout = findViewById(R.id.subscriptionListLayout);

        Subscription newSubscription = (Subscription) getIntent().getSerializableExtra("subscription");
        if (newSubscription != null) {
            if (subscriptions != null) {
                subscriptions.add(newSubscription);
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void addSubscriptionView(Subscription subscription, int index) {
        TextView subscriptionDetails = new TextView(this);
        String dueDate = calculateDueDate(subscription.getRenewalDate());

        subscriptionDetails.setText(
                "Name: " + subscription.getName() + "\n" +
                        "Cost: $" + subscription.getCost() + "\n" +
                        "Renewal Date: " + subscription.getRenewalDate() + "\n" +
                        "Payment Due Date: " + dueDate + "\n"
        );

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriptions.remove(index);
                refreshSubscriptionList();
            }
        });

        subscriptionListLayout.addView(subscriptionDetails);
        subscriptionListLayout.addView(deleteButton);
    }

    private String calculateDueDate(String renewalDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(renewalDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 30);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calculating due date";
        }
    }

    private void refreshSubscriptionList() {
        subscriptionListLayout.removeAllViews();
        if (subscriptions != null) {
            for (int i = 0; i < subscriptions.size(); i++) {
                Subscription subscription = subscriptions.get(i);
                addSubscriptionView(subscription, i);
            }
        }
    }
}