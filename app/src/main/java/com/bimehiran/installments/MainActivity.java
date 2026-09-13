package com.bimehiran.installments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Policy> policies = new ArrayList<>();
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildDashboard();
    }

    private void buildDashboard() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("حسابداری بیمه ثالث");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 25);

        root.addView(title);

        Button add = button("➕ ثبت بیمه‌نامه");
        Button policiesBtn = button("📋 بیمه‌نامه‌ها");
        Button payments = button("💰 ثبت پرداخت");
        Button reports = button("📊 گزارش‌ها");

        root.addView(add);
        root.addView(policiesBtn);
        root.addView(payments);
        root.addView(reports);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 25, 0, 0);

        root.addView(content,
                new LinearLayout.LayoutParams(
                        -1, 0, 1));

        add.setOnClickListener(v -> showAddPolicy());
        policiesBtn.setOnClickListener(v -> showPolicies());
        payments.setOnClickListener(v -> showPayments());
        reports.setOnClickListener(v -> showReports());

        setContentView(root);
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(17);
        b.setAllCaps(false);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, 65);

        p.setMargins(0, 8, 0, 8);
        b.setLayoutParams(p);

        return b;
    }

    private void showAddPolicy() {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(30, 10, 30, 10);

        EditText customer = field("نام بیمه‌گذار");
        EditText phone = field("شماره تماس");
        EditText policyNo = field("شماره بیمه‌نامه");

        EditText premium = field("مبلغ کل حق‌بیمه");
        premium.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText cash = field("مبلغ نقدی پرداخت‌شده");
        cash.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText installments = field("تعداد اقساط");
        installments.setInputType(InputType.TYPE_CLASS_NUMBER);

        box.addView(customer);
        box.addView(phone);
        box.addView(policyNo);
        box.addView(premium);
        box.addView(cash);
        box.addView(installments);

        new AlertDialog.Builder(this)
                .setTitle("ثبت بیمه‌نامه")
                .setView(box)
                .setPositiveButton("ثبت", (dialog, which) -> {

                    long total = number(premium);
                    long paidCash = number(cash);
                    int count = (int) number(installments);

                    long remaining = Math.max(0, total - paidCash);

                    Policy p = new Policy();

                    p.customer = customer.getText().toString();
                    p.phone = phone.getText().toString();
                    p.policyNo = policyNo.getText().toString();
                    p.total = total;
                    p.cash = paidCash;
                    p.remainingAmount = remaining;
                    p.installments = count;

                    policies.add(p);

                    Toast.makeText(
                            this,
                            "بیمه‌نامه با موفقیت ثبت شد",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(16);
        e.setPadding(15, 10, 15, 10);
        return e;
    }

    private long number(EditText e) {
        try {
            String s = e.getText().toString()
                    .replace(",", "")
                    .trim();

            if (s.isEmpty()) return 0;

            return Long.parseLong(s);
        } catch (Exception ex) {
            return 0;
        }
    }

    private void showPolicies() {

        content.removeAllViews();

        TextView title = new TextView(this);
        title.setText("بیمه‌نامه‌های ثبت‌شده");
        title.setTextSize(22);
        title.setPadding(0, 0, 0, 15);

        content.addView(title);

        if (policies.isEmpty()) {

            TextView empty = new TextView(this);
            empty.setText("هنوز بیمه‌نامه‌ای ثبت نشده است.");
            empty.setTextSize(17);

            content.addView(empty);
            return;
        }

        for (Policy p : policies) {

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 15, 20, 15);

            TextView info = new TextView(this);

            info.setText(
                    "بیمه‌گذار: " + p.customer +
                    "\nشماره بیمه‌نامه: " + p.policyNo +
                    "\nحق‌بیمه: " + p.total +
                    "\nنقدی: " + p.cash +
                    "\nمانده: " + p.remainingAmount +
                    "\nتعداد اقساط: " + p.installments
            );

            info.setTextSize(16);

            Button edit = button("✏️ ویرایش");
            Button delete = button("🗑 حذف");

            edit.setOnClickListener(v -> editPolicy(p));
            delete.setOnClickListener(v -> {

                new AlertDialog.Builder(this)
                        .setTitle("حذف بیمه‌نامه")
                        .setMessage("آیا از حذف این بیمه‌نامه مطمئن هستید؟")
                        .setPositiveButton("حذف", (d, w) -> {
                            policies.remove(p);
                            showPolicies();
                        })
                        .setNegativeButton("انصراف", null)
                        .show();
            });

            card.addView(info);
            card.addView(edit);
            card.addView(delete);

            content.addView(card);
        }
    }

    private void editPolicy(Policy p) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        EditText customer = field("نام بیمه‌گذار");
        EditText phone = field("شماره تماس");
        EditText policyNo = field("شماره بیمه‌نامه");
        EditText premium = field("مبلغ کل");
        EditText cash = field("نقدی");
        EditText installments = field("تعداد اقساط");

        customer.setText(p.customer);
        phone.setText(p.phone);
        policyNo.setText(p.policyNo);
        premium.setText(String.valueOf(p.total));
        cash.setText(String.valueOf(p.cash));
        installments.setText(String.valueOf(p.installments));

        box.addView(customer);
        box.addView(phone);
        box.addView(policyNo);
        box.addView(premium);
        box.addView(cash);
        box.addView(installments);

        new AlertDialog.Builder(this)
                .setTitle("ویرایش بیمه‌نامه")
                .setView(box)
                .setPositiveButton("ذخیره", (d, w) -> {

                    p.customer = customer.getText().toString();
                    p.phone = phone.getText().toString();
                    p.policyNo = policyNo.getText().toString();
                    p.total = number(premium);
                    p.cash = number(cash);
                    p.installments = (int) number(installments);

                    p.remainingAmount =
                            Math.max(0, p.total - p.cash);

                    showPolicies();
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private void showPayments() {

        content.removeAllViews();

        TextView title = new TextView(this);
        title.setText("پرداخت اقساط");
        title.setTextSize(22);

        content.addView(title);

        if (policies.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("ابتدا یک بیمه‌نامه ثبت کنید.");
            t.setTextSize(17);
            content.addView(t);
            return;
        }

        for (Policy p : policies) {

            Button pay = button(
                    "💳 پرداخت برای " + p.customer
            );

            pay.setOnClickListener(v -> {

                EditText amount = field(
                        "مبلغ پرداختی"
                );

                amount.setInputType(
                        InputType.TYPE_CLASS_NUMBER
                );

                new AlertDialog.Builder(this)
                        .setTitle("ثبت پرداخت")
                        .setView(amount)
                        .setPositiveButton("ثبت", (d, w) -> {

                            long value = number(amount);

                            p.cash += value;

                            p.remainingAmount =
                                    Math.max(
                                            0,
                                            p.total - p.cash
                                    );

                            Toast.makeText(
                                    this,
                                    "پرداخت ثبت شد",
                                    Toast.LENGTH_SHORT
                            ).show();

                            showPayments();
                        })
                        .setNegativeButton("انصراف", null)
                        .show();
            });

            content.addView(pay);
        }
    }

    private void showReports() {

        content.removeAllViews();

        long total = 0;
        long collected = 0;
        long remaining = 0;

        for (Policy p : policies) {
            total += p.total;
            collected += p.cash;
            remaining += p.remainingAmount;
        }

        TextView report = new TextView(this);

        report.setText(
                "📊 گزارش کلی\n\n" +
                "تعداد بیمه‌نامه‌ها: " + policies.size() +
                "\n\n" +
                "مجموع حق‌بیمه: " + total +
                "\n\n" +
                "مجموع دریافت‌شده: " + collected +
                "\n\n" +
                "مجموع مانده: " + remaining
        );

        report.setTextSize(19);
        report.setPadding(10, 20, 10, 20);

        content.addView(report);
    }

    static class Policy {

        String customer = "";
        String phone = "";
        String policyNo = "";

        long total = 0;
        long cash = 0;
        long remainingAmount = 0;

        int installments = 0;
    }
}
